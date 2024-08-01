package com.ihcl.order.service.v1

import com.ihcl.order.model.dto.request.*
import com.ihcl.order.model.schema.*
import com.ihcl.order.repository.ApportionOrderRepository
import org.koin.java.KoinJavaComponent
import org.litote.kmongo.json
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.math.MathContext
import java.math.RoundingMode
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class ApportionOrderService {

    private val orderRepository by KoinJavaComponent.inject<ApportionOrderRepository>(ApportionOrderRepository::class.java)
    val log: Logger = LoggerFactory.getLogger(javaClass)

    suspend fun findAndUpdateOrder(orderId: String, orders: ApportionOrder){
        orderRepository.findOneAndUpdateOrder(orderId, orders)
    }
    suspend fun getUpdatedOrder(orderId: String?): ApportionOrder {
        log.info("enter into get order service")
        return orderRepository.findUpdatedOrderByOrderId(orderId)
    }

    suspend fun createRooms(orderData: ApportionOrder) {
        orderRepository.saveOrderData(orderData)
    }

    private fun getApportionedAmount(totalAmount: Double, txnNetAmount: Double?, price: Double?): Double {
        if (txnNetAmount != null) {
            return (txnNetAmount.toBigDecimal()
                .divide(totalAmount.toBigDecimal(), MathContext.DECIMAL64) * price?.toBigDecimal()!!).setScale(
                2,
                RoundingMode.UP
            ).toDouble()
        }
        return 0.0
    }


    suspend fun getApportionedOrder(order: ApportionOrder, orderData: OrderDTO) {
        try {
            val roomList: ArrayList<Rooms> = arrayListOf()
            var roomsData: ArrayList<Rooms> = arrayListOf()
            var paymentsData: MutableList<PaymentDetailsDTO> = mutableListOf()

            // set order info into order response.

            order.orderId = orderData.orderId
            order.bookingNumber = orderData.bookingNumber
            order.type = orderData.type
            order.createdOn = getCurrentDateTime()

            // sum duplicate payment tenders in request
            orderData.paymentDetails.takeIf { it.isNotEmpty() }?.let {
                sumPaymentTenders(order, it)
                orderData.paymentDetails.clear()
                orderData.paymentDetails = order.paymentDetails
                order.paymentDetails = orderData.paymentDetails
            }
            // fetch existing recent order
            val orderId = orderData.orderId
            val response = orderId?.let { it1 -> getUpdatedOrder(it1) }
            log.info("apportioned order response $response")
            response?.let {
                roomsData = it.rooms
                paymentsData = it.paymentDetails
            }
            // Assign existing payments of recent order when amount is reduced ( request does not come with payment details)

            orderData.paymentDetails.takeUnless { it.isNotEmpty() }?.let {
                orderData.paymentDetails = paymentsData
            }

            // logic to calculate room wise apportion values

            for (room in orderData.rooms) {
                val paymentList: ArrayList<PaymentDetails> = arrayListOf()
                val roomInfo = Rooms()

                // assign room data to response
                setRoomData(roomInfo, room)

                // total of payment tenders received in request
                val totalAmount = orderData.paymentDetails.sumOf { it.txnNetAmount!! }

                for (payment in orderData.paymentDetails) {
                    val paymentDetail = PaymentDetails()
                    if (roomsData.isNotEmpty()) {
                        val roomsDTO = roomsData.first { r -> r.confirmationId.equals(room.confirmationId) }

                        // check if no of nights is less than existing no of nights or total amount is less than
                        // existing total amount for booked room

                        if ((room.noOfNights!!.toInt() <= roomsDTO.noOfNights!!.toInt()) &&
                            (room.totalAmount!!.toInt() <= roomsDTO.totalAmount!!.toInt())
                        ) {
                            if (room.noOfNights!!.toInt() == roomsDTO.noOfNights!!.toInt()) {
                                if(room.totalAmount!!.toInt() < roomsDTO.totalAmount!!.toInt()){
                                    paymentDetail.txnNetAmount = room.totalAmount!!.toDouble()
                                }else {
                                    if(roomsDTO.apportionValues.any { pay ->
                                            pay.paymentMethod.equals(payment.paymentMethod) &&
                                                    pay.cardNumber.equals(payment.cardNumber)
                                        }) {
                                        val apportionedAmount =
                                            roomsDTO.apportionValues.first { pay ->
                                                pay.paymentMethod.equals(payment.paymentMethod) &&
                                                        pay.cardNumber.equals(payment.cardNumber)
                                            }.txnNetAmount
                                        paymentDetail.txnNetAmount = apportionedAmount
                                    }
                                }
                            } else {
                                val apportionedAmount =
                                    getApportionedAmount(
                                        totalAmount,
                                        payment.txnNetAmount,
                                        room.totalAmount
                                    )
                                paymentDetail.txnNetAmount = apportionedAmount
                            }
                        } else {
                            var existingPaymentAmount = 0.0
                            if (paymentsData.any { pay ->
                                    pay.paymentMethod.equals(payment.paymentMethod) &&
                                            pay.cardNumber.equals(payment.cardNumber)
                                }) {
                                existingPaymentAmount =
                                    paymentsData.first { pay ->
                                        pay.paymentMethod.equals(payment.paymentMethod) &&
                                                pay.cardNumber.equals(payment.cardNumber)
                                    }.txnNetAmount!!

                            }
                            log.info("exist amount ..$existingPaymentAmount " + payment.txnNetAmount + " " + payment.cardNumber)
                            if (payment.txnNetAmount!! > existingPaymentAmount) {
                                val apportionedAmount = getApportionAmountForHigherPaymentAmount(
                                    room,
                                    roomsDTO,
                                    roomsData,
                                    payment,
                                    totalAmount,
                                    paymentsData,
                                    orderData,
                                    existingPaymentAmount
                                )
                                paymentDetail.txnNetAmount = apportionedAmount
                            } else {
                                val apportionedAmount = getExistingApportionAmount(roomsDTO,payment)
                                paymentDetail.txnNetAmount = apportionedAmount
                            }
                        }
                    } else {
                        val apportionedAmount =
                            getApportionedAmount(
                                totalAmount,
                                payment.txnNetAmount,
                                room.totalAmount
                            )
                        paymentDetail.txnNetAmount = apportionedAmount
                    }
                    paymentDetail.paymentType = payment.paymentType
                    paymentDetail.paymentMethod = payment.paymentMethod
                    paymentDetail.paymentMethodType = payment.paymentMethodType
                    paymentDetail.cardNumber = payment.cardNumber
                    paymentDetail.txnGateway = payment.txnGateway
                    paymentDetail.txnId = payment.txnId
                    paymentDetail.txnUUID = payment.txnUUID
                    paymentDetail.txnStatus = payment.txnStatus
                    paymentDetail.cardNo = payment.cardNo
                    paymentDetail.nameOnCard = payment.nameOnCard
                    paymentDetail.txnId = payment.txnId
                    paymentDetail.transactionDateAndTime = payment.txnId
                    paymentDetail.transactionId = payment.transactionId
                    paymentDetail.userId = payment.userId
                    paymentDetail.redemptionId = payment.redemptionId
                    paymentDetail.pointsRedemptionsSummaryId = payment.pointsRedemptionsSummaryId
                    paymentDetail.externalId = payment.externalId
                    paymentDetail.batchNumber = payment.batchNumber
                    paymentDetail.approvalCode = payment.approvalCode
                    paymentDetail.ccAvenueTxnId = payment.ccAvenueTxnId
                    paymentList.add(paymentDetail)
                }
                roomInfo.apportionValues.addAll(paymentList)
                roomList.add(roomInfo)
                order.rooms = (roomList)
            }
            // set payments when modified order amount is less than existing order.
            order.paymentDetails.takeUnless { it.isNotEmpty() }?.let {
                appendOrderPayments(order, roomList)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            log.info("Apportioned order... ${e.message}")
        }
    }

    fun getCurrentDateTime(): String? {
        val current = LocalDateTime.now()
        val offsetDate = OffsetDateTime.of(current, ZoneOffset.UTC)
        return offsetDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:ss"))
    }

    private fun getExistingApportionAmount(roomsDTO: Rooms, payment: PaymentDetailsDTO): Double {
        val apportionedAmount: Double = if (roomsDTO.apportionValues.any { pay ->
                pay.paymentMethod.equals(payment.paymentMethod) &&
                        pay.cardNumber.equals(payment.cardNumber)
            }) {
            roomsDTO.apportionValues.first { pay ->
                pay.paymentMethod.equals(payment.paymentMethod) &&
                        pay.cardNumber.equals(payment.cardNumber)
            }.txnNetAmount!!
        } else {
            payment.txnNetAmount!!
        }
        return apportionedAmount
    }

    private fun getApportionAmountForHigherPaymentAmount(
        room: RoomsDTO,
        roomsDTO: Rooms,
        roomsData: ArrayList<Rooms>,
        payment: PaymentDetailsDTO,
        totalAmount: Double,
        paymentsData: MutableList<PaymentDetailsDTO>,
        orderData: OrderDTO,
        existingPaymentMethod: Double
    ): Double {
        var originalPaymentAmount: Double? = 0.0
        val modifiedRoomAmount = room.totalAmount?.toInt()
        val originalRoomAmount = roomsDTO.totalAmount?.toInt()

        val originalTotalAmount =
            roomsData.filter { r -> !r.roomStatus.equals("Cancelled") }.sumOf { it.totalAmount?.toInt()!! }

        val modifiedPaymentTotalAmount = getModifiedPaymentTotalAmount(orderData, payment)

        val originalPaymentTotalAmount: Double? = getOriginalPaymentTotalAmount(paymentsData, payment)

        for (originalPayment in roomsDTO.apportionValues) {
            if (payment.paymentMethod.equals(originalPayment.paymentMethod)) {
                originalPaymentAmount = originalPayment.txnNetAmount
            }
        }
        val diffRoomAmount = (modifiedRoomAmount!! - originalRoomAmount!!)
        val diffTotalAmount = (totalAmount - originalTotalAmount)
        val divTotalAmount = diffRoomAmount.toBigDecimal()
            .divide(diffTotalAmount.toBigDecimal(), MathContext.DECIMAL64)
        log.info(
            "calc .. " + modifiedRoomAmount + "::" + originalRoomAmount + "::" + totalAmount + "::" + originalTotalAmount +
                    "::" + modifiedPaymentTotalAmount + "::" + originalPaymentTotalAmount + "::" + originalPaymentAmount + "::" +
                    diffRoomAmount + "::" + diffTotalAmount + "::" + divTotalAmount
        )

        val apportionedAmount: Double = if (existingPaymentMethod == 0.0) {
            ((divTotalAmount) * (modifiedPaymentTotalAmount!! - originalPaymentTotalAmount!!).toBigDecimal()).setScale(
                2,
                RoundingMode.UP
            ).toDouble()

        } else {
            ((divTotalAmount) * (modifiedPaymentTotalAmount!! - originalPaymentTotalAmount!!).toBigDecimal()).plus(
                originalPaymentAmount?.toBigDecimal()!!
            ).setScale(
                2,
                RoundingMode.UP
            ).toDouble()
        }
        return apportionedAmount
    }

    private fun getOriginalPaymentTotalAmount(paymentsData:List<PaymentDetailsDTO>, payment: PaymentDetailsDTO): Double? {
        var originalPaymentTotalAmount: Double? = 0.0
        if (paymentsData.any { pay ->
                pay.paymentMethod.equals(payment.paymentMethod) &&
                        pay.cardNumber.equals(payment.cardNumber)
            }) {
            originalPaymentTotalAmount =
                paymentsData.first { pay ->
                    pay.paymentMethod.equals(payment.paymentMethod) &&
                            pay.cardNumber.equals(payment.cardNumber)
                }.txnNetAmount!!
        }
        return originalPaymentTotalAmount
    }

    private fun getModifiedPaymentTotalAmount(orderData: OrderDTO, payment: PaymentDetailsDTO): Double? {
        return orderData.paymentDetails.first { p ->
            p.paymentType.equals(payment.paymentType) &&
                    p.cardNumber.equals(payment.cardNumber)
        }.txnNetAmount
    }

    private fun sumPaymentTenders(order: ApportionOrder, paymentDetails: List<PaymentDetailsDTO>) {
        val paymentsList = arrayListOf<PaymentDetailsDTO>()
        for (payment in paymentDetails) {
            if (paymentsList.any { p -> p.paymentMethod.equals(payment.paymentMethod) && p.cardNumber.equals(payment.cardNumber) }) {
                val existingPayment = paymentsList.first { p ->
                    p.paymentMethod.equals(payment.paymentMethod) && p.cardNumber.equals(payment.cardNumber)
                }
                existingPayment.txnNetAmount = payment.txnNetAmount!! + existingPayment.txnNetAmount!!
            } else {
                paymentsList.add(payment)
            }
        }
        order.paymentDetails = paymentsList

    }

    private fun appendOrderPayments(order: ApportionOrder, roomList: ArrayList<Rooms>) {
        val paymentDetailsDTOList: ArrayList<PaymentDetailsDTO> = arrayListOf()
        for (room in roomList) {
            for (payment in room.apportionValues) {
                if (paymentDetailsDTOList.any { p ->
                        p.paymentMethod.equals(payment.paymentMethod) &&
                                p.cardNumber.equals(payment.cardNumber)
                    }) {
                    val pay =
                        paymentDetailsDTOList.first { p ->
                            p.paymentMethod.equals(payment.paymentMethod) &&
                                    p.cardNumber.equals(payment.cardNumber)
                        }
                    pay.txnNetAmount = payment.txnNetAmount!! + pay.txnNetAmount!!
                } else {
                    val pay = PaymentDetailsDTO()
                    pay.paymentMethod = payment.paymentMethod
                    pay.txnNetAmount = payment.txnNetAmount
                    pay.cardNumber = payment.cardNumber
                    paymentDetailsDTOList.add(pay)
                }
            }
        }
        order.paymentDetails = paymentDetailsDTOList
    }

    private fun setRoomData(roomInfo: Rooms, room: RoomsDTO) {
        roomInfo.confirmationId = room.confirmationId
        roomInfo.price = room.price
        roomInfo.taxAmount = room.taxAmount
        roomInfo.noOfNights = room.noOfNights
        roomInfo.totalAmount = room.totalAmount
        roomInfo.checkIn = room.checkIn
        roomInfo.checkOut = room.checkOut
        roomInfo.roomStatus = room.roomStatus
    }

}