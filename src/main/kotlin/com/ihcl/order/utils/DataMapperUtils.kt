package com.ihcl.order.utils


import com.ihcl.order.config.Configuration
import com.ihcl.order.model.dto.request.*
import com.ihcl.order.model.dto.request.Billing
import com.ihcl.order.model.dto.request.Currency
import com.ihcl.order.model.dto.request.EmailAddress
import com.ihcl.order.model.dto.request.Guest
import com.ihcl.order.model.dto.request.GuestCount
import com.ihcl.order.model.dto.request.Identifier
import com.ihcl.order.model.dto.request.Notification
import com.ihcl.order.model.dto.request.PaymentDetailsInfo
import com.ihcl.order.model.dto.request.PersonName
import com.ihcl.order.model.dto.request.Product
import com.ihcl.order.model.dto.request.ProductDetails
import com.ihcl.order.model.dto.request.Products
import com.ihcl.order.model.dto.request.RoomStay
import com.ihcl.order.model.dto.response.*
import com.ihcl.order.model.dto.response.GiftCardDetails
import com.ihcl.order.model.schema.*
import com.ihcl.order.model.schema.Address
import com.ihcl.order.model.schema.ExtraData
import com.ihcl.order.model.schema.Hotel
import com.ihcl.order.model.schema.OrderLineItem
import com.ihcl.order.model.schema.OrderType
import com.ihcl.order.model.schema.PaymentDetail
import com.ihcl.order.model.schema.PaymentMethod
import com.ihcl.order.model.schema.PaymentStatus
import com.ihcl.order.model.schema.Room
import com.ihcl.order.model.schema.User
import com.ihcl.order.service.v1.OrderService
import com.ihcl.order.utils.Constants.ADULT
import com.ihcl.order.utils.Constants.BOOKING
import com.ihcl.order.utils.Constants.CHILD
import com.ihcl.order.utils.Constants.CONFIRM_BOOKING
import com.ihcl.order.utils.Constants.COUNTRY
import com.ihcl.order.utils.Constants.COUPON_PROMO_TYPE
import com.ihcl.order.utils.Constants.CREATE_BOOKING_STATUS
import com.ihcl.order.utils.Constants.CREDIT_CARD
import com.ihcl.order.utils.Constants.CURRENCY_CODE
import com.ihcl.order.utils.Constants.DELIVERY_COMMENT
import com.ihcl.order.utils.Constants.DELIVERY_MODE_EMAIL
import com.ihcl.order.utils.Constants.DELIVERY_MODE_SMS
import com.ihcl.order.utils.Constants.EMAIL_ADDRESS_TYPE
import com.ihcl.order.utils.Constants.EMPTY
import com.ihcl.order.utils.Constants.GIFT_CARD
import com.ihcl.order.utils.Constants.GRAVITY_VOUCHER_REDEEM_AMOUNT
import com.ihcl.order.utils.Constants.GUEST_ROLE
import com.ihcl.order.utils.Constants.INITIATED
import com.ihcl.order.utils.Constants.JUS_PAY
import com.ihcl.order.utils.Constants.LINE_3
import com.ihcl.order.utils.Constants.MEMBERSHIP_PURCHASE
import com.ihcl.order.utils.Constants.NEU_COINS
import com.ihcl.order.utils.Constants.NON_WEB
import com.ihcl.order.utils.Constants.ORDER_STATUS_PENDING
import com.ihcl.order.utils.Constants.PAID_BY_MULTIPLE_TENDERS
import com.ihcl.order.utils.Constants.PAID_BY_NET_BANKING
import com.ihcl.order.utils.Constants.PAID_BY_NEU_COINS
import com.ihcl.order.utils.Constants.PAYMENT_STRING_PREFIX
import com.ihcl.order.utils.Constants.PAYONLINE
import com.ihcl.order.utils.Constants.PAY_AT_HOTEL
import com.ihcl.order.utils.Constants.PAY_DEPOSIT
import com.ihcl.order.utils.Constants.PAY_FULL
import com.ihcl.order.utils.Constants.PAY_NOW
import com.ihcl.order.utils.Constants.PAY_ONLINE
import com.ihcl.order.utils.Constants.PAY_ONLINE_PATTERN
import com.ihcl.order.utils.Constants.PENDING
import com.ihcl.order.utils.Constants.PHYSICAL_GIFT_CARD
import com.ihcl.order.utils.Constants.PURCHASE_GC
import com.ihcl.order.utils.Constants.RECEIVED_FROM
import com.ihcl.order.utils.Constants.ROLE
import com.ihcl.order.utils.Constants.ROOM_COUNT
import com.ihcl.order.utils.Constants.STORE_ORDER
import com.ihcl.order.utils.Constants.TATA_NEU
import com.ihcl.order.utils.Constants.TIME_ZONE_ID
import com.ihcl.order.utils.Constants.TYPE
import com.ihcl.order.utils.Constants.USE
import com.ihcl.order.utils.ValidatorUtils.validateRequestBody
import io.ktor.http.*
import org.koin.java.KoinJavaComponent
import org.litote.kmongo.json
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.math.RoundingMode
import java.security.Key
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import kotlin.collections.ArrayList
import kotlin.collections.HashSet
import kotlin.collections.LinkedHashSet
import kotlin.collections.List

object DataMapperUtils {
    val orderService by KoinJavaComponent.inject<OrderService>(OrderService::class.java)
    private val log: Logger = LoggerFactory.getLogger(javaClass)
      private val prop=Configuration.env

    fun getCurrentDateTime(): String? {
        val current = LocalDateTime.now( ZoneId.of(TIME_ZONE_ID))
        val offsetDate = OffsetDateTime.of(current, ZoneOffset.UTC)
        return offsetDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a", Locale.ENGLISH))
    }
    fun formattedDate(): String? {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")

        // Get the current date and time in UTC
        val currentDateTime = ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC)

        // Format the current date and time using the specified formatter

        return formatter.format(currentDateTime)
    }
    fun mapCreateOrder(
        customerHash: String,
        orderType: OrderType, response: OrderLineItems?, headers: String?, travelerDto: TravelerDto?
    ): Order {
        val voucherNumber = if(!travelerDto?.voucherNumber.isNullOrEmpty()){
            travelerDto?.voucherNumber?.let { encrypt(it) }
        }else ""
        val voucherPin = if(!travelerDto?.voucherPin.isNullOrEmpty()){
            travelerDto?.voucherPin?. let { encrypt(it) }
        }else ""
        val paymentMethod = when (travelerDto?.paymentMethod) {
            PAY_DEPOSIT, PAY_FULL, PAY_ONLINE, PAY_NOW -> {
                PAY_ONLINE
            }
            CONFIRM_BOOKING -> {
                CONFIRM_BOOKING
            }
            else -> {
                PAY_AT_HOTEL
            }
        }
        val rooms = response?.cartDetails?.items?.get(0)?.hotel?.get(0)?.room?.size
        var roomCount = 0
        var adultCount = 0
        var childrens = 0
        for (i in 0 until rooms!!) {
            roomCount++
            adultCount += response.cartDetails.items[0].hotel[0].room[i].adult!!
            childrens += response.cartDetails.items[0].hotel[0].room[i].children!!
        }
        val bookingNoOfNights: Int = getMaxNoOfNights(response.cartDetails.items.first().hotel.first().room,roomCount)
        val total = response.cartDetails.totalPrice
        val payableAmount =  response.cartDetails.payableAmount

        val travelerDetails = travelerDto?.let { _ ->
            listOf(
                TravellerDetail(
                    salutation = "${travelerDto.salutation}.",
                    dateOfBirth = "",
                    address = "",
                    city = "",
                    countryCode = travelerDto.countryCode,
                    customerId = "",
                    customerType = "",
                    email = travelerDto.email,
                    firstName = travelerDto.firstName.trim(),
                    gender = travelerDto.gender,
                    gstNumber = travelerDto.GSTNumber,
                    lastName = travelerDto.lastName.trim(),
                    membershipNumber = travelerDto.membershipNo,
                    mobile = travelerDto.phoneNo,
                    name = "",
                    secondaryContact = "",
                    state = "",
                    isCampaignOffer=travelerDto.isCampaignOffer,
                    offerIdentifier=travelerDto.offerIdentifier
                )
            )
        }
        val room = response.let { orderLine ->
            orderLine.cartDetails.items?.get(0)?.hotel?.get(0)?.room?.map { room ->
                Room(
                    isPackage = room.isPackageCode,
                    confirmationId = "0",
                    cancellationId = "0",
                    checkIn = room.checkIn,
                    checkOut = room.checkOut,
                    roomId = room.roomId,
                    roomName = room.roomName,
                    roomType = room.roomType,
                    rateCode = room.rateCode,
                    rateDescription = room.rateDescription,
                    roomDescription = room.roomDescription,
                    addOnDetails = listOf(
                        AddOnDetail(
                            addOnCode = "",
                            addOnType = "",
                            addOnPrice = 0.0,
                            addOnName = "",
                            addOnDesc = ""
                        )
                    ),
                    discountAmount = 0.0,
                    discountCode = "",
                    isModified = false,
                    isRefundedItem = false,
                    modifiedWith = "",
                    price = room.cost,
                    refundedAmount = "",
                    packageCode = room.packageCode,
                    adult = room.adult,
                    children = room.children,
                    travellerDetails = travelerDetails,
                    packageName = room.packageName,
                    roomNumber = room.roomNumber,
                    status = PENDING,
                    taxAmount = room.tax.amount,
                    tax = room.tax,
                    roomImgUrl = room.roomImgUrl,
                    changePrice = room.changePrice,
                    changeTax = room.changeTax,
                    bookingPolicyDescription = room.bookingPolicyDescription,
                    daily =room.daily,
                    cancelPolicyDescription = room.cancelPolicyDescription,
                    description = room.description,
                    detailedDescription = room.detailedDescription,
                    penaltyAmount = null,
                    penaltyDeadLine = null,
                    cancellationTime = null,
                    penaltyApplicable = null,
                    cancelRemark = null,
                    modifyBooking = null,
                    grandTotal = room.grandTotal,
                    paidAmount = room.paidAmount,
                    roomCode = room.roomCode,
                    roomDepositAmount = room.roomDepositAmount,
                    cancelPayableAmount = 0.0,
                    cancelRefundableAmount = 0.0,
                    currency = room.currency,
                    noOfNights = room.noOfNights,
                    couponDiscountValue = room.couponDiscountValue,
                    createdTimestamp = room.createdTimestamp,
                    modifiedTimestamp = room.modifiedTimestamp
                )

            }
        }
        val paymentDetails: MutableList<PaymentDetail>? = response.cartDetails.paymentDetails?.map { payment ->
            PaymentDetail(
                paymentType = payment.paymentType,
                paymentMethod = payment.paymentMethod,
                paymentMethodType = payment.paymentMethodType,
                txnGateway = payment.txnGateway,
                txnId = payment.txnId,
                txnNetAmount = payment.txnNetAmount,
                txnStatus = INITIATED,
                txnUUID = payment.txnUUID,
                cardNo = payment.cardNo,
                preAuthCode = payment.preAuthCode,
                batchNumber = payment.batchNumber,
                approvalCode = payment.approvalCode,
                transactionId = payment.transactionId,
                transactionDateAndTime = payment.transactionDateAndTime,
                nameOnCard = payment.nameOnCard,
                cardNumber = payment.cardNumber,
                cardPin = payment.cardPin,
                userId = payment.userId,
                externalId = payment.externalId,
                pointsRedemptionsSummaryId = payment.pointsRedemptionsSummaryId,
                redemptionId = payment.redemptionId,
                expiryDate = payment.expiryDate,
                ccAvenueTxnId = payment.ccAvenueTxnId
            )
        }?.toMutableList()
        return Order(
            headers.toString(),
            customerHash,
            customerEmail = travelerDto?.email.toString(),
            customerId = "",
            customerMobile = travelerDto?.phoneNo.toString(),
            orderType = orderType,
            billingAddress = BillingAddress(
                address1 = "",
                address2 = "",
                address3 = "",
                city = "",
                country = response.cartDetails.items.first().hotel.first().country.toString(),
                pinCode = "",
                firstName = travelerDto?.firstName.toString(),
                lastName = travelerDto?.lastName.toString(),
                phoneNumber = travelerDto?.phoneNo.toString(),
                countyCodeISO = travelerDto?.countryCode.toString(),
                state = ""
            ),
            channel = "WEB",
            currencyCode = travelerDto?.countryCode.toString(),
            discountAmount = 0.0,
            basePrice = response.cartDetails.basePrice,
            taxAmount = response.cartDetails.tax,
            payableAmount = payableAmount!!,
            gradTotal = total!!,
            isRefundable = false,
            offers = listOf(
                Offers(
                    offerAmount = 0.0,
                    offerName = "",
                    offerType = ""
                )
            ),
            orderLineItems = mutableListOf(
                OrderLineItem(
                    hotel = Hotel(
                        addOnDetails = listOf(
                            AddOnDetail(
                                addOnCode = "",
                                addOnDesc = "",
                                addOnName = "",
                                addOnPrice = 0.0,
                                addOnType = ""
                            )
                        ),
                        address = Address(
                            city = "",
                            contactNumber = "",
                            directions = "",
                            landmark = response.cartDetails.items.first().hotel.first().hotelAddress,
                            lat = "",
                            long = "",
                            mapLink = "",
                            pinCode = response.cartDetails.items.first().hotel.first().pinCode,
                            state = response.cartDetails.items.first().hotel.first().state,
                            street = ""
                        ),
                        bookingNumber = "",
                        category = "",
                        hotelId = response.cartDetails.items.first().hotel.first().hotelId,
                        invoiceNumber = "",
                        invoiceUrl = "",
                        name = response.cartDetails.items.first().hotel.first().hotelName,
                        reservationId = "",
                        rooms = room,
                        status = PENDING,
                        childrens = childrens,
                        roomCount = roomCount,
                        adultCount = adultCount,
                        country = response.cartDetails.items.first().hotel.first().country,
                        checkIn = response.cartDetails.items.first().hotel.first().checkIn,
                        checkOut = response.cartDetails.items.first().hotel.first().checkOut,
                        promoCode = response.cartDetails.items.first().hotel.first().promoCode,
                        promoType = response.cartDetails.items.first().hotel.first().promoType,
                        mobileNumber = response.cartDetails.items.first().hotel.first().mobileNumber,
                        emailId = response.cartDetails.items.first().hotel.first().emailId,
                        specialRequest = travelerDto?.specialRequest,
                        totalDepositAmount = response.cartDetails.totalDepositAmount,
                        isDepositAmount = response.cartDetails.isDepositAmount,
                        voucherRedemption = response.cartDetails.items.first().hotel.first().voucherRedemption,
                        neupassMembershipId = travelerDto?.membershipId,
                        revisedPrice = response.cartDetails.items.first().hotel.first().revisedPrice,
                        grandTotal =  response.cartDetails.items.first().hotel.first().grandTotal,
                        totalBasePrice =  response.cartDetails.items.first().hotel.first().totalBasePrice,
                        totalTaxPrice =  response.cartDetails.items.first().hotel.first().totalTaxPrice,
                        amountPaid = 0.0,
                        refundAmount = 0.0,
                        payableAmount = 0.0,
                        oldGrandTotal = total,
                        oldTotalTaxPrice = response.cartDetails.tax,
                        oldTotalBasePrice = response.cartDetails.basePrice,
                        totalTaxChange = 0.0,
                        totalPriceChange = 0.0,
                        totalCancelPayableAmount = 0.0,
                        totalCancelRefundableAmount = 0.0,
                        totalCancellationPaidAmount = 0.0,
                        totalCancellationPenaltyAmount = 0.0,
                        isDepositPaid = false,
                        balancePayable = response.cartDetails.balancePayable,
                        bookingNoOfNights = bookingNoOfNights,
                        totalCouponDiscountValue = response.cartDetails.totalCouponDiscountValue,
                        storeId = response.cartDetails.items.first().hotel.first().storeId,
                        hotelSponsorId =response.cartDetails.items.first().hotel.first().hotelSponsorId,
                        synxisId = response.cartDetails.items.first().hotel.first().synxisId,
                        voucherPin = voucherPin,
                        voucherNumber = voucherNumber,
                        isSeb = response.cartDetails.items.first().hotel.first().isSeb,
                        sebRequestId = response.cartDetails.items.first().hotel.first().sebRequestId


                    ),
                    giftCard = null,
                    loyalty = null
                )
            ),
            orderStatus = INITIATED,
            paymentDetails = TransactionInfo(
                transaction_1 = paymentDetails,
                transaction_2 = null,
                transaction_3 = null,
                transaction_4 = null
            ),
            paymentMethod = paymentMethod,
            paymentStatus = PENDING,
            refundAmount = 0.0,
            transactionId = "",
            modifyBookingCount = 0,
            transactionType = BOOKING,
            agreedTnc = travelerDto?.agreedTnc,
            agreedPrivacyPolicy = travelerDto?.agreedPrivacyPolicy,
            bookingCancelRemarks = null,
            transactionStatus = PENDING,
            createdTimestamp = Date(),
            brandName = ""
        )
    }


    fun mapPaymentDetails(response: OrderLineItems):MutableList<PaymentDetail>{
        val payableAmount = if(response.cartDetails.modifiedPaymentDetails!=null){
            response.cartDetails.modifiedPaymentDetails!!.modifiedPayableAmount
        }else response.cartDetails.payableAmount
        return mutableListOf(PaymentDetail(
            paymentType = JUS_PAY,
            paymentMethod = "",
            paymentMethodType = "",
            txnGateway = 0,
            txnId = "",
            txnNetAmount = payableAmount,
            txnStatus = PENDING,
            txnUUID = "",
            cardNumber = "",
            cardPin = "",
            preAuthCode = "",
            batchNumber = "0",
            approvalCode = "",
            transactionId = 0,
            transactionDateAndTime = "",
            cardNo = "",
            nameOnCard = "",
            userId = "",
            externalId = "",
            pointsRedemptionsSummaryId = "",
            redemptionId = "",
            expiryDate = "",
            ccAvenueTxnId = ""
        ))
    }

    fun mapGiftCardToCreateOrder(customerHash: String, request: GiftCardRequest, headers: String?): GiftCardResponse {
        val total = (request.giftCardDetails.amount).times(request.giftCardDetails.quantity)
        log.info("total amount $total")
        val qty = request.giftCardDetails.quantity
        val giftCard = arrayListOf<GiftCardInfo>()
    for (i in 1..qty){
        log.info("enter into loop$i")
           val list=     GiftCardInfo(
                    amount = request.giftCardDetails.amount,
                    sku = request.giftCardDetails.sku,
                    type = request.giftCardDetails.type
                )
        giftCard.add(list)

        log.info("GiftCard details $giftCard")
       }
        val giftCardDetails = GiftCardDto(
            deliveryMethods = DeliveryMethodsInfo(
                phone = request.deliveryMethods.phone,
                email = request.deliveryMethods.email,
                smsAndWhatsApp = request.deliveryMethods.smsAndWhatsApp
            ),
            giftCardDetails = giftCard,
            quantity = request.giftCardDetails.quantity,
            receiverAddress = ReceiverAddressDetails(
                addressLine1 = request.receiverAddress.addressLine1,
                addressLine2 = request.receiverAddress.addressLine2,
                city = request.receiverAddress.city,
                state = request.receiverAddress.state,
                country = request.receiverAddress.country,
                pinCode = request.receiverAddress.pinCode,
            ),
            receiverDetails = ReceiverInfo(
                email = request.receiverDetails.email,
                firstName = request.receiverDetails.firstName,
                lastName = request.receiverDetails.lastName,
                message = request.receiverDetails.message,
                phone = request.receiverDetails.phone,
                rememberMe = request.receiverDetails.rememberMe,
                scheduleOn = request.receiverDetails.scheduleOn
            ),
            senderDetails = SenderInfo(
                email = request.senderDetails.email,
                firstName = request.senderDetails.firstName,
                lastName = request.senderDetails.lastName,
                phone = request.senderDetails.phone,
                registerAsNeuPass = request.senderDetails.registerAsNeuPass
            )
        )
        return GiftCardResponse(
            headers,
            customerHash,
            customerEmail = request.senderDetails.email,
            customerMobile = request.senderDetails.phone,
            channel = "",
            currencyCode = "",
            discountAmount = 0.0,
            payableAmount = total,
            gradTotal = total,
            isRefundable = true,
            orderType = OrderType.GIFT_CARD_PURCHASE,
            transactionId = "",
            billingAddress = GiftCardBillingAddress(
                address1 = "billing 1",
                address2 = "billing 2",
                address3 = "billing 3 ",
                city = "bangalore",
                country = "IN",
                pinCode = "560095",
                firstName = "Sanesh",
                lastName = "G",
                phoneNumber = "+918989898987",
                countyCodeISO = "365",
                state = "hyd"
            ),
            offers = listOf(
                OffersForGiftCard(
                    offerAmount = 0.0,
                    offerName = "",
                    offerType = ""
                )
            ),
            orderItems = listOf(
                OrderItem(
                    shippingAddress = GiftCardShippingAddress(
                        address1 = "",
                        address2 = "",
                        address3 = "",
                        firstName = "",
                        lastName = "",
                        city = "",
                        country = "",
                        pinCode = "",
                        state = "",
                        phoneNumber = "+918789898990",
                        countyCodeISO = ""
                    ),
                    invoiceNumber = "",
                    invoiceUrl = "",
                    name = "",
                    status = "",
                    giftCard = giftCardDetails
                )
            ),
            customerId = "",
            paymentInfo = listOf(
                PaymentInfo(
                    paymentType = "",
                    paymentMethod = "",
                    paymentMethodType = "",
                    txnGateway = "",
                    txnId = "",
                    txnNetAmount = total,
                    txnStatus = "",
                    txnUUID = ""
                )
            ),
            paymentMethod = PaymentMethod.PAY_ONLINE,
            paymentStatus = PaymentStatus.PENDING,
            refundAmount = 0.0,
            taxAmount = 0.0,
            theme = request.giftCardDetails.theme

        )
    }

    fun mapCartToCreateOrder(order: Order, response: OrderLineItems, createBookingConfirmation:CreateBookingConfirmation?): OrderResponse {
        val rooms = order.orderLineItems.first().hotel?.rooms?.size
        var roomCount = 0
        var adultCount = 0
        var childrens = 0
        for (i in 0 until rooms!!) {
            roomCount++
            adultCount += order.orderLineItems.first().hotel?.rooms?.get(i)?.adult!!
            childrens += order.orderLineItems.first().hotel?.rooms?.get(i)?.children!!
        }
        val bookingNoOfNights: Int =
            getMaxNoOfNights(response.cartDetails.items?.first()?.hotel?.first()?.room,roomCount)
        val room = order.orderLineItems[0].hotel?.rooms?.map {
            RoomDetailsInfo(
                isPackageCode = it.isPackage!!,
                confirmationId = it.confirmationId,
                checkOut = response.cartDetails.items?.first()?.hotel?.first()?.checkOut,
                checkIn = response.cartDetails.items?.first()?.hotel?.first()?.checkIn,
                roomId = it.roomId,
                roomName = it.roomName,
                roomType = it.roomType,
                rateCode = it.rateCode,
                rateDescription = it.rateDescription,
                roomDescription = it.roomDescription,
                tax = it.tax!!,
                addOnDetails = listOf(
                    AddOnDetailInfo(
                        addOnCode = "",
                        addOnType = "",
                        addOnPrice = 0.0,
                        addOnName = "",
                        addOnDesc = ""
                    )
                ),
                discountAmount = 0.0,
                discountCode = "",
                isModified = it.isModified,
                isRefundedItem = it.isRefundedItem,
                modifiedWith = "",
                price = it.price,
                refundedAmount = "",
                packageName = it.packageName!!,
                roomNumber = it.roomNumber,
                status = it.status.toString(),
                roomImgUrl = it.roomImgUrl,
                travellerDetails = listOf(
                    TravellerDetailsInfo(
                        dateOfBirth = "",
                        address = "",
                        city = "",
                        countryCode = it.travellerDetails?.first()?.countryCode.toString(),
                        customerId = "",
                        customerType = "",
                        email = it.travellerDetails?.first()?.email.toString(),
                        firstName = it.travellerDetails?.first()?.firstName.toString(),
                        gender = it.travellerDetails?.first()?.gender.toString(),
                        gstNumber = it.travellerDetails?.first()?.gstNumber.toString(),
                        lastName = it.travellerDetails?.first()?.lastName.toString(),
                        membershipNumber = it.travellerDetails?.first()?.membershipNumber.toString(),
                        mobile = it.travellerDetails?.first()?.mobile.toString(),
                        name = "",
                        secondaryContact = "",
                        state = ""
                    )
                ),
                changePrice = it.changePrice,
                changeTax = it.changeTax,
                bookingPolicyDescription = it.bookingPolicyDescription,
                daily =it.daily,
                cancelPolicyDescription = it.cancelPolicyDescription,
                description = it.description,
                detailedDescription = it.detailedDescription,
                grandTotal = it.grandTotal,
                paidAmount = it.paidAmount,
                roomCode = it.roomCode,
                roomDepositAmount = it.roomDepositAmount,
                currency = it.currency,
                couponDiscountValue = it.couponDiscountValue,
                modifiedRoom = it.modifyBooking?.let {mb ->
                    ModifiedRoom(
                    isPackageCode = mb.isPackage!!,
                    confirmationId = mb.confirmationId!!,
                    checkOut = mb.checkOut,
                    checkIn = mb.checkIn,
                    roomId = mb.roomId,
                    roomName = mb.roomName,
                    roomType = mb.roomType,
                    rateCode = mb.rateCode,
                    rateDescription = mb.rateDescription,
                    roomDescription = mb.roomDescription,
                    addOnDetails = listOf(
                        AddOnDetailInfo(
                            addOnCode = "",
                            addOnType = "",
                            addOnPrice = 0.0,
                            addOnName = "",
                            addOnDesc = ""
                        )
                    ),
                    discountAmount = 0.0,
                    discountCode = "",
                    isModified = mb.isModified,
                    isRefundedItem = mb.isRefundedItem,
                    modifiedWith = "",
                    price = mb.price,
                    tax = mb.tax!!,
                    refundedAmount = "",
                    packageName = mb.packageName!!,
                    roomNumber = mb.roomNumber,
                    status = mb.status.toString(),
                    bookingPolicyDescription = mb.bookingPolicyDescription,
                    cancelPolicyDescription = mb.cancelPolicyDescription,
                    description = mb.description,
                    detailedDescription = mb.detailedDescription,
                    grandTotal = mb.grandTotal,
                    paidAmount = mb.paidAmount,
                    roomCode = it.roomCode,
                    roomDepositAmount = it.roomDepositAmount,
                    currency = mb.currency,
                    travellerDetails = listOf(
                        TravellerDetailsInfo(
                            dateOfBirth = "",
                            address = "",
                            city = "",
                            countryCode = it.travellerDetails?.first()?.countryCode.toString(),
                            customerId = "",
                            customerType = "",
                            email = it.travellerDetails?.first()?.email.toString(),
                            firstName = it.travellerDetails?.first()?.firstName.toString(),
                            gender = it.travellerDetails?.first()?.gender.toString(),
                            gstNumber = it.travellerDetails?.first()?.gstNumber.toString(),
                            lastName = it.travellerDetails?.first()?.lastName.toString(),
                            membershipNumber = it.travellerDetails?.first()?.membershipNumber.toString(),
                            mobile = it.travellerDetails?.first()?.mobile.toString(),
                            name = "",
                            secondaryContact = "",
                            state = ""
                        )
                    )
                    )
                }
            )

        }?.toMutableList()
        log.info("room ${room?.json}")
       val paymentInfo = order.paymentDetails?.transaction_1
        val paymentDetails: MutableList<PaymentDetailInfo>? = paymentInfo?.map { payment ->
            PaymentDetailInfo(
                paymentType = payment.paymentType,
                paymentMethod = payment.paymentMethod,
                paymentMethodType = payment.paymentMethodType,
                txnGateway = payment.txnGateway,
                txnId = payment.txnId,
                txnNetAmount = payment.txnNetAmount,
                txnStatus = payment.txnStatus,
                txnUUID = payment.txnUUID,
                cardNumber = payment.cardNumber,
                preAuthCode = payment.preAuthCode,
                batchNumber = payment.batchNumber,
                approvalCode = payment.approvalCode,
                transactionId = payment.transactionId,
                transactionDateAndTime = payment.transactionDateAndTime,
                cardNo = payment.cardNo,
                nameOnCard = payment.nameOnCard,
                expiryDate = payment.expiryDate
            )
        }?.toMutableList()

        return OrderResponse(
            orderId = order.orderId,
            customerHash = order.customerHash,
            customerEmail = "",
            orderType = order.orderType,
            customerMobile = order.customerMobile,
            channel = "",
            currencyCode = "",
            discountAmount = 0.0,
            basePrice = order.basePrice,
            taxAmount = order.taxAmount,
            gradTotal = order.gradTotal,
            payableAmount = order.payableAmount,
            isRefundable = order.isRefundable,
            billingAddress = BillingAddressDetails(
                address1 = "",
                address2 = "",
                address3 = "",
                city = "",
                country = "",
                pinCode = "",
                firstName = "",
                lastName = "",
                phoneNumber = order.customerMobile,
                countyCodeISO = "",
                state = ""
            ),
            offers = listOf(
                OffersDetails(
                    offerAmount = 0.0,
                    offerName = "",
                    offerType = ""
                )
            ),
            orderLineItems = listOf(
                OrderLineItemDetails(
                    hotel = HotelDetailsInfo(
                        addOnDetails = listOf(
                            AddOnDetailInfo(
                                addOnCode = "",
                                addOnDesc = "",
                                addOnName = "",
                                addOnPrice = 0.0,
                                addOnType = ""
                            )
                        ),
                        address = AddressDetails(
                            city = "",
                            contactNumber = "",
                            directions = "",
                            landmark = "",
                            lat = "",
                            long = "",
                            mapLink = "",
                            pinCode = "",
                            state = "",
                            street = ""
                        ),
                        bookingNumber = order.orderLineItems[0].hotel?.bookingNumber,
                        category = "",
                        hotelId = order.orderLineItems.first().hotel?.hotelId,
                        invoiceNumber = "",
                        invoiceUrl = "",
                        name = order.orderLineItems.first().hotel?.name,
                        country = order.orderLineItems.first().hotel?.country,
                        voucherRedemption =order.orderLineItems.first().hotel?.voucherRedemption,
                        reservationId = "",
                        rooms = room!!,
                        status = order.orderLineItems[0].hotel?.status.toString(),
                        childrens = childrens,
                        totalRooms = roomCount,
                        adults = adultCount,
                        promoCode = order.orderLineItems[0].hotel?.promoCode,
                        promoType = order.orderLineItems[0].hotel?.promoType,
                        specialRequest = order.orderLineItems[0].hotel?.specialRequest,
                        totalDepositAmount = order.orderLineItems[0].hotel?.totalDepositAmount,
                        totalBasePrice = order.orderLineItems.first().hotel?.totalBasePrice,
                        totalTaxPrice = order.orderLineItems.first().hotel?.totalTaxPrice,
                        grandTotal = order.orderLineItems.first().hotel?.grandTotal,
                        amountPaid = order.orderLineItems.first().hotel?.amountPaid,
                        oldTotalBasePrice = order.orderLineItems.first().hotel?.oldTotalBasePrice,
                        oldTotalTaxPrice = order.orderLineItems.first().hotel?.oldTotalTaxPrice,
                        oldGrandTotal = order.orderLineItems.first().hotel?.oldGrandTotal,
                        payableAmount = order.orderLineItems.first().hotel?.payableAmount,
                        refundAmount = order.orderLineItems.first().hotel?.refundAmount,
                        balancePayable = order.orderLineItems.first().hotel?.balancePayable,
                        bookingNoOfNights = bookingNoOfNights,
                        checkIn = order.orderLineItems.first().hotel?.checkIn,
                        checkOut = order.orderLineItems.first().hotel?.checkOut,
                        totalCouponDiscountValue = order.orderLineItems.first().hotel?.totalCouponDiscountValue,
                        storeId = order.orderLineItems.first().hotel?.storeId,
                        hotelSponsorId =order.orderLineItems.first().hotel?.hotelSponsorId
                    )
                )
            ),
            customerId = "",
            paymentDetails = paymentDetails,
            paymentMethod = order.paymentMethod,
            paymentStatus = order.paymentStatus,
            orderStatus = order.orderStatus,
            refundAmount = 0.0,
            transactionId = "",
            totalPriceChange = order.orderLineItems.first().hotel?.totalPriceChange,
            totalTaxChange = order.orderLineItems.first().hotel?.totalTaxChange,
            errorCode = HttpStatusCode.OK.value,
            errorMessage = createBookingConfirmation?.reservations?.get(0)?.notAvailableRooms
        )
    }
    fun notAvailableRoomsCreateOrderResponse(
        createBookingConfirmation:CreateBookingConfirmation?,
        customerHash: String,
        response: OrderLineItems,
        order: Order,
        headers: String?,
        travelerDetails: TravelerDto
    ): OrderResponse {
        val rooms = response.cartDetails.items?.get(0)?.hotel?.get(0)?.room?.size
        var roomCount = 0
        var adultCount = 0
        var childrens = 0
        for (i in 0 until rooms!!) {
            roomCount++
            adultCount += response.cartDetails.items.first().hotel.first().room[i].adult!!
            childrens += response.cartDetails.items.first().hotel.first().room[i].children!!
        }
        val bookingNoOfNights = getMaxNoOfNights(response.cartDetails.items.first().hotel.first().room,roomCount)
        val room = order.orderLineItems[0].hotel?.rooms?.map {
            RoomDetailsInfo(
                isPackageCode = it.isPackage!!,
                confirmationId = it.confirmationId,
                checkOut = response.cartDetails.items.first().hotel.first().checkOut,
                checkIn = response.cartDetails.items.first().hotel.first().checkIn,
                roomId = it.roomId,
                roomName = it.roomName,
                roomType = it.roomType,
                rateCode = it.rateCode,
                rateDescription = it.rateDescription,
                roomDescription = it.roomDescription,
                tax = it.tax!!,
                addOnDetails = listOf(
                    AddOnDetailInfo(
                        addOnCode = "",
                        addOnType = "",
                        addOnPrice = 0.0,
                        addOnName = "",
                        addOnDesc = ""
                    )
                ),
                discountAmount = 0.0,
                discountCode = "",
                isModified = it.isModified,
                isRefundedItem = it.isRefundedItem,
                modifiedWith = "",
                price = it.price,
                refundedAmount = "",
                packageName = it.packageName!!,
                roomNumber = it.roomNumber,
                status = PENDING,
                roomImgUrl = it.roomImgUrl,
                travellerDetails = listOf(
                    TravellerDetailsInfo(
                        dateOfBirth = "",
                        address = "",
                        city = "",
                        countryCode = travelerDetails.countryCode,
                        customerId = "",
                        customerType = "",
                        email = travelerDetails.email,
                        firstName = travelerDetails.firstName,
                        gender = travelerDetails.gender,
                        gstNumber = travelerDetails.GSTNumber,
                        lastName = travelerDetails.lastName,
                        membershipNumber = travelerDetails.membershipNo,
                        mobile = travelerDetails.phoneNo,
                        name = "",
                        secondaryContact = "",
                        state = ""
                    )
                ),
                changePrice = it.changePrice,
                changeTax = it.changeTax,
                bookingPolicyDescription = it.bookingPolicyDescription,
                daily =it.daily,
                cancelPolicyDescription = it.cancelPolicyDescription,
                description = it.description,
                detailedDescription = it.detailedDescription,
                grandTotal = it.grandTotal,
                paidAmount = it.paidAmount,
                roomCode = it.roomCode,
                roomDepositAmount = it.roomDepositAmount,
                currency = it.currency,
                couponDiscountValue = it.couponDiscountValue,
                modifiedRoom = it.modifyBooking?.let {mb ->
                    ModifiedRoom(
                        isPackageCode = mb.isPackage!!,
                        confirmationId = mb.confirmationId!!,
                        checkOut = mb.checkOut,
                        checkIn = mb.checkIn,
                        roomId = mb.roomId,
                        roomName = mb.roomName,
                        roomType = mb.roomType,
                        rateCode = mb.rateCode,
                        rateDescription = mb.rateDescription,
                        roomDescription = mb.roomDescription,
                        addOnDetails = listOf(
                            AddOnDetailInfo(
                                addOnCode = "",
                                addOnType = "",
                                addOnPrice = 0.0,
                                addOnName = "",
                                addOnDesc = ""
                            )
                        ),
                        discountAmount = 0.0,
                        discountCode = "",
                        isModified = mb.isModified,
                        isRefundedItem = mb.isRefundedItem,
                        modifiedWith = "",
                        price = mb.price,
                        tax = mb.tax!!,
                        refundedAmount = "",
                        packageName = mb.packageName!!,
                        roomNumber = mb.roomNumber,
                        status = PENDING,
                        bookingPolicyDescription = mb.bookingPolicyDescription,
                        cancelPolicyDescription = mb.cancelPolicyDescription,
                        description = mb.description,
                        detailedDescription = mb.detailedDescription,
                        grandTotal = mb.grandTotal,
                        paidAmount = mb.paidAmount,
                        roomCode = it.roomCode,
                        roomDepositAmount = it.roomDepositAmount,
                        currency = mb.currency,
                        travellerDetails = listOf(
                            TravellerDetailsInfo(
                                dateOfBirth = "",
                                address = "",
                                city = "",
                                countryCode = travelerDetails.countryCode,
                                customerId = "",
                                customerType = "",
                                email = travelerDetails.email,
                                firstName = travelerDetails.firstName,
                                gender = travelerDetails.gender,
                                gstNumber = travelerDetails.GSTNumber,
                                lastName = travelerDetails.lastName,
                                membershipNumber = travelerDetails.membershipNo,
                                mobile = travelerDetails.phoneNo,
                                name = "",
                                secondaryContact = "",
                                state = ""
                            )
                        )
                    )
                }
            )

        }?.toMutableList()
        log.info("room ${room?.json}")
        val paymentInfo = order.paymentDetails?.transaction_1
        val paymentDetails: MutableList<PaymentDetailInfo>? = paymentInfo?.map { payment ->
            PaymentDetailInfo(
                paymentType = payment.paymentType,
                paymentMethod = payment.paymentMethod,
                paymentMethodType = payment.paymentMethodType,
                txnGateway = payment.txnGateway,
                txnId = payment.txnId,
                txnNetAmount = payment.txnNetAmount,
                txnStatus = payment.txnStatus,
                txnUUID = payment.txnUUID,
                cardNumber = payment.cardNumber,
                preAuthCode = payment.preAuthCode,
                batchNumber = payment.batchNumber,
                approvalCode = payment.approvalCode,
                transactionId = payment.transactionId,
                transactionDateAndTime = payment.transactionDateAndTime,
                cardNo = payment.cardNo!!,
                nameOnCard = payment.nameOnCard!!,
                expiryDate = payment.expiryDate
            )
        }?.toMutableList()

        return OrderResponse(
            headers,
            customerHash,
            customerEmail = "",
            orderType = OrderType.HOTEL_BOOKING,
            customerMobile = order.customerMobile,
            channel = "",
            currencyCode = "",
            discountAmount = 0.0,
            basePrice = order.basePrice,
            taxAmount = order.taxAmount,
            gradTotal = order.gradTotal,
            payableAmount = order.payableAmount,
            isRefundable = order.isRefundable,
            billingAddress = BillingAddressDetails(
                address1 = "",
                address2 = "",
                address3 = "",
                city = "",
                country = "",
                pinCode = "",
                firstName = "",
                lastName = "",
                phoneNumber = order.customerMobile,
                countyCodeISO = "",
                state = ""
            ),
            offers = listOf(
                OffersDetails(
                    offerAmount = 0.0,
                    offerName = "",
                    offerType = ""
                )
            ),
            orderLineItems = listOf(
                OrderLineItemDetails(
                    hotel = HotelDetailsInfo(
                        addOnDetails = listOf(
                            AddOnDetailInfo(
                                addOnCode = "",
                                addOnDesc = "",
                                addOnName = "",
                                addOnPrice = 0.0,
                                addOnType = ""
                            )
                        ),
                        address = AddressDetails(
                            city = "",
                            contactNumber = "",
                            directions = "",
                            landmark = "",
                            lat = "",
                            long = "",
                            mapLink = "",
                            pinCode = "",
                            state = "",
                            street = ""
                        ),
                        bookingNumber = order.orderLineItems[0].hotel?.bookingNumber!!,
                        category = "",
                        hotelId = response.cartDetails.items.first().hotel.first().hotelId,
                        invoiceNumber = "",
                        invoiceUrl = "",
                        name = response.cartDetails.items.first().hotel.first().hotelName,
                        country = response.cartDetails.items.first().hotel.first().country,
                        voucherRedemption = response.cartDetails.items.first().hotel.first().voucherRedemption,
                        reservationId = "",
                        rooms = room!!,
                        status = PENDING,
                        childrens = childrens,
                        totalRooms = roomCount,
                        adults = adultCount,
                        promoCode = order.orderLineItems[0].hotel?.promoCode,
                        promoType = order.orderLineItems[0].hotel?.promoType,
                        specialRequest = order.orderLineItems[0].hotel?.specialRequest,
                        totalDepositAmount = order.orderLineItems[0].hotel?.totalDepositAmount,
                        totalBasePrice = order.orderLineItems.first().hotel?.totalBasePrice,
                        totalTaxPrice = order.orderLineItems.first().hotel?.totalTaxPrice,
                        grandTotal = order.orderLineItems.first().hotel?.grandTotal,
                        amountPaid = order.orderLineItems.first().hotel?.amountPaid,
                        oldTotalBasePrice = order.orderLineItems.first().hotel?.oldTotalBasePrice,
                        oldTotalTaxPrice = order.orderLineItems.first().hotel?.oldTotalTaxPrice,
                        oldGrandTotal = order.orderLineItems.first().hotel?.oldGrandTotal,
                        payableAmount = order.orderLineItems.first().hotel?.payableAmount,
                        refundAmount = order.orderLineItems.first().hotel?.refundAmount,
                        balancePayable = order.orderLineItems.first().hotel?.balancePayable,
                        bookingNoOfNights = bookingNoOfNights,
                        checkIn = "",
                        checkOut = "",
                        totalCouponDiscountValue = order.orderLineItems.first().hotel?.totalCouponDiscountValue,
                        storeId = order.orderLineItems.first().hotel?.storeId,
                        hotelSponsorId = order.orderLineItems.first().hotel?.hotelSponsorId
                    )
                )
            ),
            customerId = "",
            paymentDetails = paymentDetails,
            paymentMethod = order.paymentMethod,
            paymentStatus = order.paymentStatus,
            orderStatus = order.orderStatus,
            refundAmount = 0.0,
            transactionId = "",
            totalPriceChange = response.cartDetails.totalPriceChange,
            totalTaxChange = response.cartDetails.totalTaxChange,
            errorCode = HttpStatusCode.NotFound.value,
            errorMessage = createBookingConfirmation?.reservations?.get(0)?.notAvailableRooms
        )
    }

    fun qcSuccessOrderStatus(order: Order, paymentReference: String, paymentStatus: String): QCCreateBooking {
        val emailId = if(order.orderLineItems.first().giftCard?.isMySelf == true){
             order.orderLineItems.first().giftCard?.receiverDetails?.email
        }else{
            order.orderLineItems.first().giftCard?.senderDetails?.email
        }
        val city = order.billingAddress?.city
        val country = order.billingAddress?.country.toString()
        val email = emailId
        val firstname = order.billingAddress?.firstName.toString()
        val lastname = order.billingAddress?.lastName.toString()
        val line1 = order.billingAddress?.address1.toString()
        val line2 = order.billingAddress?.address2.toString()
        val telephone = order.billingAddress?.phoneNumber.toString()
        val postcode = order.billingAddress?.pinCode.toString()
        val region = order.billingAddress?.state.toString()

        val theme = order.orderLineItems[0].giftCard!!.giftCardDetails!![0].theme
        val deliveryMode = if(theme == PHYSICAL_GIFT_CARD){
            EMPTY
        }else{
            if(order.orderLineItems.first().giftCard?.deliveryMethods?.smsAndWhatsApp == true){
                DELIVERY_MODE_SMS
            }else{
                DELIVERY_MODE_EMAIL
            }
        }
        return QCCreateBooking(
            address =
            AddressDto(
                billToThis = true,
                city = order.orderLineItems[0].giftCard?.receiverAddress?.city,
                company = "",
                country = order.orderLineItems[0].giftCard?.receiverAddress?.country,
                email = order.orderLineItems[0].giftCard?.receiverDetails?.email,
                firstname = order.orderLineItems[0].giftCard?.receiverDetails?.firstName,
                lastname = order.orderLineItems[0].giftCard?.receiverDetails?.lastName,
                line1 = order.orderLineItems[0].giftCard?.receiverAddress?.addressLine1,
                line2 = order.orderLineItems[0].giftCard?.receiverAddress?.addressLine2,
                postcode = order.orderLineItems[0].giftCard?.receiverAddress?.pinCode,
                region = order.orderLineItems[0].giftCard?.receiverAddress?.state,
                telephone = order.orderLineItems[0].giftCard?.receiverDetails?.phone

            ),
            billing = Billing(
                city = city,
                line1 = line1,
                line2 = line2,
                company = "",
                email = email,
                firstname = firstname,
                lastname = lastname,
                postcode = postcode,
                country = country,
                region = region,
                telephone = telephone,
            ),
            deliveryMode = deliveryMode,
            remarks = paymentStatus ,
            products = listOf(
                Product(
                    currency = order.currencyCode,
                    qty = order.orderLineItems[0].giftCard!!.quantity,
                    price = order.orderLineItems[0].giftCard?.giftCardDetails!![0].amount?.toInt(),
                    theme = "",
                    sku = order.orderLineItems[0].giftCard?.giftCardDetails!![0].sku,
                    payout = PayOut(
                          id = paymentReference
                        ),
                    giftMessage = order.orderLineItems[0].giftCard?.receiverDetails?.message
                )),
            payments = listOf(PaymentDto(
                amount = order.gradTotal.toInt(),
                code = "svc"
            )),
           refno = order.orderId)
    }
    fun qcConfirmOrderSuccessMapping(order: Order, paymentTransactionId: String?): Any{
         var cardNo: String? = null
         var nameOnCard: String? = null
         var cardBrand: String? = null
        var paymentMethodType = ""
        val receivedFrom = if(order.orderLineItems[0].giftCard?.isMySelf == true) RECEIVED_FROM else
            order.orderLineItems[0].giftCard?.senderDetails?.firstName
        val paymentInfo = order.paymentDetails?.transaction_1
        paymentInfo?.forEach { paymentDetail ->
            if (paymentDetail.paymentType == JUS_PAY) {
                cardNo = paymentDetail.cardNo!!
                nameOnCard = paymentDetail.nameOnCard!!
                cardBrand = paymentDetail.paymentMethod!!
                paymentMethodType = paymentDetail.paymentMethodType.toString()
            }

        }
        if(order.transactionStatus.equals(PAID_BY_MULTIPLE_TENDERS, ignoreCase = true) || order.transactionStatus.equals(
                PAID_BY_NEU_COINS, ignoreCase = true) || paymentMethodType.equals(PAID_BY_NET_BANKING, ignoreCase = true)){
            cardNo = null
        }
        val theme: String? = if(order.orderLineItems.first().giftCard!!.giftCardDetails!!.first().theme == PHYSICAL_GIFT_CARD){
           EMPTY
       }else order.orderLineItems.first().giftCard!!.giftCardDetails!!.first().theme
        var jusPayAmount = 0.0
        var neuCoinAmount = 0.0
        order.paymentDetails?.transaction_1?.forEach {
            if(it.paymentType == JUS_PAY){
                jusPayAmount = it.txnNetAmount.toString().toDouble()
            }else if(it.paymentType == TATA_NEU){
                neuCoinAmount = it.txnNetAmount.toString().toDouble()
            }
        }


        val buyGC= FEBuyGCSuccessResponse(
            receiverFirstName = order.orderLineItems[0].giftCard?.receiverDetails!!.firstName,
            receiverLastName = order.orderLineItems[0].giftCard?.receiverDetails!!.lastName,
            senderFirstName = order.orderLineItems[0].giftCard?.senderDetails!!.firstName,
            senderLastName = order.orderLineItems[0].giftCard?.senderDetails!!.lastName,
            email = order.orderLineItems[0].giftCard?.receiverDetails?.email,
            purchaseOrderNo = order.orderLineItems[0].giftCard?.giftCardDetails?.get(0)?.orderId,
            priceTotal = order.payableAmount,
            cardNumber = cardNo,
            nameOnCard = nameOnCard,
            cardBrand = cardBrand,
            receivedFrom = receivedFrom,
            paymentMethod = order.transactionStatus.toString(),
            senderPhoneNumber = order.orderLineItems[0].giftCard?.senderDetails!!.phone,
            receiverPhoneNumber = order.orderLineItems[0].giftCard?.receiverDetails!!.phone,
            deliveryMethods = order.orderLineItems[0].giftCard?.deliveryMethods,
            receiverAddress = order.orderLineItems[0].giftCard?.receiverAddress,
            priceBreakUp = PriceBreakUp(
                price = jusPayAmount,
                neuCoinsAmount = neuCoinAmount,
                totalPrice = jusPayAmount + neuCoinAmount
            ),
                giftCard = order.orderLineItems[0].giftCard?.giftCardDetails?.map {gc ->
                    GiftCardDetails(
                    message = order.orderLineItems[0].giftCard?.receiverDetails!!.message,
                    giftCardNumber = gc.cardNumber,
                    theme = theme,
                    validity = gc.validity,
                    sku = gc.sku,
                    amount = gc.amount,
                    scheduledOn = order.orderLineItems[0].giftCard!!.receiverDetails!!.scheduleOn,
                    createdOn = order.createdTimestamp.toString())

                },
            paymentTransactionId = paymentTransactionId
                )
        return buyGC
    }

    fun qcConfirmOrderSuccessMappingForReload(order: Order?, paymentTransactionId: String?): Any{
        var cardNo: String? = null
        var nameOnCard: String? = null
        var cardBrand: String? = null
        val paymentInfo = order?.paymentDetails?.transaction_1
        paymentInfo?.forEach { paymentDetail ->
            if(paymentDetail.paymentType == JUS_PAY){
                cardNo = paymentDetail.cardNo
                nameOnCard = paymentDetail.nameOnCard
                cardBrand = paymentDetail.paymentMethod
            }
        }
        val buyGC= FEBuyGCSuccessResponse(
            receiverFirstName = order?.orderLineItems?.first()?.giftCard?.receiverDetails!!.firstName,
            receiverLastName = order.orderLineItems[0].giftCard?.receiverDetails!!.lastName,
            senderFirstName = order.orderLineItems[0].giftCard?.senderDetails!!.firstName,
            senderLastName = order.orderLineItems[0].giftCard?.senderDetails!!.lastName,
            email = order.orderLineItems[0].giftCard?.receiverDetails?.email,
            purchaseOrderNo = order.orderId,
            priceTotal = order.payableAmount,
            cardNumber = cardNo,
            nameOnCard = nameOnCard,
            cardBrand = cardBrand,
            receivedFrom = RECEIVED_FROM,
            paymentMethod =  order.transactionStatus.toString(),
            receiverPhoneNumber = order.orderLineItems[0].giftCard?.receiverDetails?.phone,
            senderPhoneNumber = order.orderLineItems[0].giftCard?.senderDetails?.phone,
            deliveryMethods = order.orderLineItems[0].giftCard?.deliveryMethods,
            receiverAddress = order.orderLineItems[0].giftCard?.receiverAddress,
            priceBreakUp = null,
            giftCard = order.orderLineItems[0].giftCard?.giftCardDetails?.map {gc ->
                GiftCardDetails(
                    message = order.orderLineItems[0].giftCard?.receiverDetails!!.message,
                    giftCardNumber = gc.cardNumber?.let { decrypt(it) },
                    theme = gc.theme,
                    validity = gc.validity,
                    sku = gc.sku,
                    amount = gc.amount,
                    scheduledOn = order.orderLineItems[0].giftCard!!.receiverDetails!!.scheduleOn,
                    createdOn = order.createdTimestamp.toString()
                    )

            },
            paymentTransactionId = paymentTransactionId
        )
        return buyGC
    }
    fun qcConfirmOrderSuccessMappingForLoyalty(order: Order, paymentTransactionId: String?):Any {
        log.info("QC confirm order success mapping ....")
        val inputDateString=order.orderLineItems[0].loyalty?.membershipDetails?.user?.date_of_birth
        val inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val inputDate = LocalDate.parse(inputDateString?.substring(0, 10), inputFormatter)
        val outputDateString = inputDate.format(outputFormatter)
        val paymentInfo = order.paymentDetails?.transaction_1
        var neuCoinsAmount = 0.0
        var paymentMethodType = ""
        var cardNo: String? = null
            order.paymentDetails?.transaction_1?.forEach {
            if(it.paymentType == JUS_PAY){
                paymentMethodType = it.paymentMethodType.toString()
                cardNo = it.cardNo.toString()
            }else if(it.paymentType == TATA_NEU){
                neuCoinsAmount = it.txnNetAmount.toString().toDouble()
            }
        }

        if(order.transactionStatus.equals(PAID_BY_MULTIPLE_TENDERS, ignoreCase = true) || order.transactionStatus.equals(
                PAID_BY_NEU_COINS, ignoreCase = true) || paymentMethodType.equals(PAID_BY_NET_BANKING, ignoreCase = true)){
            cardNo = ""
        }
        val addOnCard = order.orderLineItems.first().loyalty?.memberCardDetails?.addOnCardDetails
        val addOnCardDetails = if(addOnCard?.obtainAddOnCard == true){
            AddOnCardDetails(
               email = addOnCard.email,
               firstName = addOnCard.firstName,
               lastName = addOnCard.lastName,
               title = addOnCard.title,
               dateOfBirth = addOnCard.dateOfBirth,
               mobile = addOnCard.mobile,
               mobileCountryCode = addOnCard.mobileCountryCode,
               obtainAddOnCard = addOnCard.obtainAddOnCard
            )
        }else null
        return ConfirmLoyaltyResponse(
            "${paymentInfo?.get(0)?.txnId}",
            order.orderId,
            "${order.orderLineItems[0].loyalty?.membershipDetails?.memberId}",
            "${order.orderLineItems[0].loyalty?.membershipDetails?.user?.first_name} ${order.orderLineItems[0].loyalty?.membershipDetails?.user?.last_name}",
            "${order.orderLineItems[0].loyalty?.membershipDetails?.user?.email}",
            "${order.orderLineItems[0].loyalty?.memberCardDetails?.extra_data?.country_code} ${order.orderLineItems[0].loyalty?.membershipDetails?.mobile}",
                outputDateString,
            "${order.orderLineItems[0].loyalty?.memberCardDetails?.extra_data?.gstNumber}",
            "${order.transactionStatus}",
            cardNo,
            LoyaltyAddress(
                "${order.orderLineItems[0].loyalty?.membershipDetails?.user?.address}, ",
                "${order.orderLineItems[0].loyalty?.memberCardDetails?.extra_data?.domicile}, ",
                "${order.orderLineItems[0].loyalty?.memberCardDetails?.extra_data?.state}, ",
                "${order.orderLineItems[0].loyalty?.membershipDetails?.user?.pincode}, ",
                "${order.orderLineItems[0].loyalty?.memberCardDetails?.extra_data?.country}"
            ),
            listOf(
           com.ihcl.order.model.dto.response.Product(
               "${order.orderLineItems[0].loyalty?.memberCardDetails?.extra_data?.epicure_type}",
               "${order.orderLineItems[0].loyalty?.memberCardDetails?.epicure_price}",
               "${order.orderLineItems[0].loyalty?.memberCardDetails?.taxAmount}",
               "1",
               "${order.orderLineItems[0].loyalty?.memberCardDetails?.epicure_price!! + order.orderLineItems[0].loyalty?.memberCardDetails?.taxAmount!!}"
           )
            ),
            order.orderStatus,
            "${paymentInfo?.get(0)?.nameOnCard}",
            "",
            LoyaltyPriceSummary(
                 price = order.orderLineItems.first().loyalty?.memberCardDetails?.epicure_price!!,
                 tax = order.orderLineItems.first().loyalty?.memberCardDetails?.taxAmount!!,
                 discountPercent = order.orderLineItems.first().loyalty?.memberCardDetails?.discountPercent!!,
                 discountPrice = order.orderLineItems.first().loyalty?.memberCardDetails?.discountPrice!!,
                 discountTax = order.orderLineItems.first().loyalty?.memberCardDetails?.discountTax!!,
                 neuCoins = neuCoinsAmount,
                 totalPayableAmount = order.payableAmount,
                 totalPrice = order.gradTotal
            ),
            addOnCardDetails,
            paymentTransactionId = paymentTransactionId
            )

    }

    fun mapReloadGiftCard(
        customerHash: String,
        request: ReloadBalRequest?,
        orderType: OrderType, headers: String?
    ): Order{

        var total = 0.0
        val giftCardDetails = request?.let {
            total = request.amount
            log.info("total amount $total")
            GiftCard(
                deliveryMethods = DeliveryMethodsDto(
                    phone = "",
                    email = false,
                    smsAndWhatsApp = false
                ),
                giftCardDetails = listOf( GiftCardDetailsDto(
                    amount = request.amount,
                    sku = "",
                    type = "",
                    theme = "",
                    cardId = "",
                    validity = "",
                    cardNumber = encrypt( request.cardNumber ),
                    cardPin = "",
                    orderId = ""
                )),
                receiverAddress = ReceiverAddressDto(
                    addressLine1 = "",
                    addressLine2 = "",
                    city = "",
                    state = "",
                    country = "",
                    pinCode = "",
                ),
                receiverDetails = ReceiverDetailsDto(
                    email = "",
                    firstName = "",
                    lastName = "",
                    message = "",
                    phone = "",
                    rememberMe = true,
                    scheduleOn = ""
                ),
                senderDetails = SenderDetailsDto(
                    email = "",
                    firstName = "",
                    lastName = "",
                    phone = "",
                    registerAsNeuPass = true
                ),
                quantity = 0,
                isMySelf = true
            )
        }
        return Order(
            headers.toString(),
            customerHash,
            customerEmail = "",
            customerId = "",
            customerMobile = "",
            orderType = orderType,
            billingAddress = BillingAddress(
                address1 = "billing 1",
                address2 = "billing 2",
                address3 = "billing 3 ",
                city = "bangalore",
                country = "IN",
                pinCode = "560095",
                firstName = "Sanesh",
                lastName = "G",
                phoneNumber = "+918989898987",
                countyCodeISO = "356",
                state = "Karnataka"
            ),
            channel = "WEB",
            currencyCode = "356",
            discountAmount = 0.0,
            basePrice = total,
            taxAmount = 0.0,
            payableAmount = total,
            gradTotal = total,
            isRefundable = true,
            offers = listOf(
                Offers(
                    offerAmount = 0.0,
                    offerName = "",
                    offerType = ""
                )
            ),
            orderLineItems = mutableListOf(
                OrderLineItem(
                    hotel = null,
                    giftCard = giftCardDetails,
                    loyalty = null
                )
            ),
            orderStatus = INITIATED,
            paymentDetails = TransactionInfo(
                transaction_1 = mutableListOf(
                    PaymentDetail(
                        paymentType = JUS_PAY,
                        paymentMethod = "",
                        paymentMethodType = "",
                        txnGateway = 0,
                        txnId = "",
                        txnNetAmount = total,
                        txnStatus = INITIATED,
                        txnUUID = "",
                        cardNumber = "",
                        cardPin = "",
                        preAuthCode = "",
                        batchNumber = "0",
                        approvalCode = "",
                        transactionId = 0,
                        transactionDateAndTime = "",
                        cardNo = "",
                        nameOnCard = "",
                        userId = "",
                        externalId = "",
                        pointsRedemptionsSummaryId = "",
                        redemptionId = "",
                        expiryDate = "",
                        ccAvenueTxnId = ""
                    )
                ),
                transaction_2 = null,
                transaction_3 = null,
                transaction_4 = null
            ),
            paymentMethod = "PAY ONLINE",
            paymentStatus = PENDING,
            refundAmount = 0.0,
            transactionId = "",
            transactionType = "RELOAD_GC",
            modifyBookingCount = 0,
            bookingCancelRemarks = null,
            transactionStatus = PENDING,
            createdTimestamp = Date(),
            agreedTnc = request?.agreedTnc,
            agreedPrivacyPolicy = request?.agreedPrivacyPolicy,
            brandName = ""
        )
    }

    fun mapBalanceEnquiryRequest(cardNumber: String): BalanceEnquiryRequest{
        return BalanceEnquiryRequest(
            balanceEnquiry = listOf(
                BalanceEnquiry(
                    CardNumber = cardNumber
                )
            )
        )
    }
    fun mapBalanceEnquiry(cardNumber: String?,cardPin:String?): OriginalBalanceEnquiryRequest{
        return OriginalBalanceEnquiryRequest(
            balanceEnquiry = listOf(
                BalanceEnquiryDto(
                    CardNumber = cardNumber,
                    CardPin = cardPin
                )
            )
        )
    }
    fun mapReloadGiftCard(order: Order): LoyaltyReloadRequest {
        val giftCard = order.orderLineItems[0].giftCard?.giftCardDetails
        val paymentDetails = order.paymentDetails?.transaction_1?.first()
        return LoyaltyReloadRequest(
            cardNumber = decrypt( giftCard?.get(0)?.cardNumber!! ),
            amount = giftCard[0].amount!!,
            invoiceNumber = order.orderId.plus("|").plus(paymentDetails?.ccAvenueTxnId),
            idempotencyKey = order.orderId
        )
    }

    fun mapLoyaltyRequest(
        customerHash: String,
        request: LoyaltyCreateOrderRequest,
        orderId: String?,
        memberId:String?,
        loyaltyCartResponse:LoyaltyCartResponse,
        memberShipPlanName: String?,
        memberShipPlanCode: String?
    ): Order {
        val price = (loyaltyCartResponse.priceSummary.price - loyaltyCartResponse.priceSummary.discountPrice)
        val tax = (loyaltyCartResponse.priceSummary.tax - loyaltyCartResponse.priceSummary.discountTax)
        val total = loyaltyCartResponse.priceSummary.totalPrice
        val addOnCardDetails = if(request.addOnCardDetails?.obtainAddOnCard ==true){
            validateRequestBody(validateAddOnCardReq.validate(request.addOnCardDetails))
            request.addOnCardDetails
        }else null
        return Order(
            orderId.toString(),
            customerHash,
            request.user?.email.toString(),
            "",
            request.mobile.toString(),
            "",
            "",
            0.0,
            price,
            tax,
            loyaltyCartResponse.priceSummary.totalPrice!!,
            loyaltyCartResponse.priceSummary.totalPayableAmount,
            true,
            OrderType.MEMBERSHIP_PURCHASE,
            "",
            BillingAddress("", "", "", "", "", "", "", "", "", "", ""),
            listOf(Offers(0.0, "", "")),
            mutableListOf(
                OrderLineItem(
                    null,
                    null,
                    Loyalty(
                        MemberCardDetails(
                            "",
                            0,
                            "",
                            0,
                            ExtraData(
                                request.extraData?.countryCode.toString(),
                                request.extraData?.city.toString(),
                                loyaltyCartResponse.items.epicureDetails.epicureType,
                                request.extraData?.state.toString(),
                                request.country!!,
                                request.gstNumber!!
                            ),
                            loyaltyCartResponse.priceSummary.price,
                            loyaltyCartResponse.priceSummary.tax,
                            loyaltyCartResponse.priceSummary.discountPercent,
                            loyaltyCartResponse.priceSummary.discountPrice,
                            loyaltyCartResponse.priceSummary.discountTax,
                            addOnCardDetails
                        ),
                        MembershipDetails(
                            memberId!!,
                            request.mobile!!,
                            User(
                                request.user?.email.toString(),
                                request.user?.firstName.toString(),
                                request.user?.lastName!!.toString(),
                                request.gender.toString(),
                                request.salutation.toString(),
                                request.dateOfBirth.toString(),
                                request.address.toString(),
                                request.pinCode.toString()
                            ),
                            addOnCardDetails
                        ),
                        ShareHolderDetails(
                            memberShipPlanName,
                            memberShipPlanCode,
                            loyaltyCartResponse.items.epicureDetails.memberShipPurchaseType,
                            loyaltyCartResponse.items.epicureDetails.bankName
                        ),
                        loyaltyCartResponse.items.epicureDetails.memberShipPurchaseType,
                        loyaltyCartResponse.items.epicureDetails.isBankUrl,
                        loyaltyCartResponse.items.epicureDetails.isShareHolder,
                        loyaltyCartResponse.items.epicureDetails.isTata,
                        request.extraData?.gravityVoucherCode?.let { encrypt(it) },
                        request.extraData?.gravityVoucherPin?.let { encrypt(it) },
                        loyaltyCartResponse.items.epicureDetails.offerName,
                        loyaltyCartResponse.items.epicureDetails.offerCode,
                    )
                )
            ),0,
            TransactionInfo(mutableListOf(PaymentDetail(JUS_PAY, "", "", 0, "", "",total, INITIATED, "", "", "","", "", "", "","","","","0", "", 0, "","")), null,null, null),
            PAY_ONLINE,
            PENDING,
            INITIATED,
            MEMBERSHIP_PURCHASE,
            0.0, null,
            request.agreedTnc,request.agreedPrivacyPolicy,0,
            Date(),
            Date(),
            null,
            brandName = ""
        )
    }


    fun giftCardCreateOrder(
        customerHash: String,
        cartRequest: GiftCardCartResponse,
        orderType: OrderType,
        headers: String?,
        request:GCCreateOrderRequest): Order{
        val giftCardDetails = cartRequest.let {
            GiftCard(
                deliveryMethods = cartRequest.items?.deliveryMethods,
                giftCardDetails = cartRequest.items?.giftCardDetails,
                receiverAddress = cartRequest.items?.receiverAddress,
                receiverDetails =cartRequest.items?.receiverDetails,
                senderDetails = cartRequest.items?.senderDetails,
                quantity = cartRequest.items?.quantity,
                isMySelf = cartRequest.items?.isMySelf
            )
        }
        val city:String
        val email: String
        val firstname: String
        val lastname: String
        val country: String
        val line1: String
        val line2: String
        val telephone: String
        val postcode: String

        if(cartRequest.items?.isMySelf == true){
            city = cartRequest.items.receiverAddress?.city.toString()
            country = cartRequest.items.receiverAddress?.country.toString()
            email = cartRequest.items.receiverDetails?.email.toString()
            firstname = cartRequest.items.receiverDetails?.firstName.toString()
            lastname = cartRequest.items.receiverDetails?.lastName.toString()
            line1 = cartRequest.items.receiverAddress?.addressLine1.toString()
            line2 = cartRequest.items.receiverAddress?.addressLine2.toString()
            telephone = cartRequest.items.receiverDetails?.phone.toString()
            postcode = cartRequest.items.receiverAddress?.pinCode.toString()
        }
        else{
            city = cartRequest.items?.senderAddress?.city.toString()
            line1 = cartRequest.items?.senderAddress?.addressLine1.toString()
            line2 = cartRequest.items?.senderAddress?.addressLine2.toString()
            email = cartRequest.items?.senderDetails?.email.toString()
            firstname = cartRequest.items?.senderDetails?.firstName.toString()
            lastname = cartRequest.items?.senderDetails?.lastName.toString()
            country = cartRequest.items?.senderAddress?.country.toString()
            telephone = cartRequest.items?.senderDetails?.phone.toString()
            postcode = cartRequest.items?.senderAddress?.pinCode.toString()
        }

        return Order(
            headers.toString(),
            customerHash,
            customerEmail = email,
            customerId = "",
            customerMobile = telephone,
            orderType = orderType,
            billingAddress = BillingAddress(
                address1 = line1,
                address2 = line2,
                address3 = LINE_3,
                city = city,
                country = country,
                pinCode = postcode,
                firstName = firstname,
                lastName = lastname,
                phoneNumber = telephone,
                countyCodeISO = "IN",
                state = ""
            ),
            channel = "WEB",
            currencyCode = "356",
            discountAmount = 0.0,
            basePrice = cartRequest.priceSummary?.totalPrice,
            taxAmount = 0.0,
            payableAmount = cartRequest.priceSummary?.totalPayableAmount!!,
            gradTotal = cartRequest.priceSummary?.totalPrice!!,
            isRefundable = true,
            offers = listOf(
                Offers(
                    offerAmount = 0.0,
                    offerName = "",
                    offerType = ""
                )
            ),
            orderLineItems = mutableListOf(
                OrderLineItem(
                    hotel = null,
                    giftCard = giftCardDetails,
                    loyalty = null
                )
            ),
            orderStatus = INITIATED,
            paymentDetails = TransactionInfo(
                transaction_1 = cartRequest.paymentDetails,
                transaction_2 = null,
                transaction_3 = null,
                transaction_4 = null
            ),
            paymentMethod = PAY_ONLINE,
            paymentStatus = PENDING,
            refundAmount = 0.0,
            transactionId = "",
            transactionType = PURCHASE_GC,
            modifyBookingCount = 0,
            bookingCancelRemarks = null,
            transactionStatus = PENDING,
            createdTimestamp = Date(),
            agreedTnc = request.agreedTnc,
            agreedPrivacyPolicy = request.agreedPrivacyPolicy,
            brandName = ""
        )
    }


    fun mapConfirmHotelBookingResponse(order: Order, paymentTransactionId: String?): ConfirmHotelResponse {
        var cardNumber: String? = null
        var nameOnCard: String? = null
        var cardBrand: String? = null
        val paymentDetails = order.paymentDetails?.transaction_1

        log.info("confirm hotel booking response is $order")
        val currentHotelData = order.orderLineItems[0].hotel
        var redeemNeuCoins = 0.0
        var giftCardPrice = 0.0
        var gstAmount = 0.0
        var paymentMethodType = ""
        paymentDetails?.forEach { payment ->
            when (payment.paymentType) {
                TATA_NEU -> {
                    redeemNeuCoins += payment.txnNetAmount!!
                }
                GIFT_CARD -> {
                    giftCardPrice += payment.txnNetAmount!!
                }
                else -> {
                    nameOnCard = payment.nameOnCard
                    cardNumber = payment.cardNo
                    cardBrand = payment.paymentMethod
                    paymentMethodType = payment.paymentMethodType.toString()
                }
            }
        }
        if(order.transactionStatus.equals(PAID_BY_MULTIPLE_TENDERS, ignoreCase = true) || order.transactionStatus.equals(
                PAID_BY_NEU_COINS, ignoreCase = true) || paymentMethodType.equals(PAID_BY_NET_BANKING, ignoreCase = true)){
            cardNumber = null
        }
      order.orderLineItems[0].hotel?.rooms?.forEach {
            if(it.modifyBooking!= null){
                it.tax?.breakDown?.forEach {breakDown ->
                        gstAmount += breakDown.amount!!
                }
            }else{
                it.tax?.breakDown?.forEach {taxBreakDown ->
                    gstAmount +=taxBreakDown.amount!!
                }
            }
          if(it.penaltyApplicable != null && !it.penaltyApplicable!!) it.penaltyAmount = 0.0
        }
        val room = arrayListOf<com.ihcl.order.model.dto.response.Room>()
        order.orderLineItems[0].hotel?.rooms?.map {
            if(it.modifyBooking != null){
                room.add(
                    com.ihcl.order.model.dto.response.Room(
                        it.isPackage,
                        ADDONS(
                            it.addOnDetails?.get(0)?.addOnPrice.toString(),
                            it.addOnDetails?.get(0)?.addOnCode,
                            it.addOnDetails?.get(0)?.addOnName
                        ),
                        it.adult,
                        it.children,
                        it.noOfNights,
                        it.confirmationId,
                        it.cancellationId,
                        it.status,
                        it.checkIn,
                        it.checkOut,
                        it.tax!!,
                        it.bookingPolicyDescription,
                        it.daily,
                        it.cancelPolicyDescription,
                        it.description,
                        it.detailedDescription,
                        it.penaltyAmount,
                        it.penaltyDeadLine,
                        it.cancellationTime,
                        it.penaltyApplicable,
                        GuestInformation(
                            it.travellerDetails?.get(
                                0
                            )?.email,
                            it.travellerDetails?.get(0)?.firstName,
                            it.travellerDetails?.get(0)?.mobile,
                            it.travellerDetails?.get(0)?.salutation
                        ),
                        Package(it.price.toString(), it.packageCode, it.packageName),
                        it.packageName.toString(),
                        it.roomName,
                        it.roomImgUrl,
                        it.roomNumber,
                        it.changePrice,
                        it.changeTax,
                        cancelRemark = it.cancelRemark,
                        modifyBooking = ModifyBookingInfo(
                         it.isPackage,
                        ADDONS(
                            it.modifyBooking.addOnDetails[0].addOnPrice.toString(),
                            it.modifyBooking.addOnDetails[0].addOnCode,
                            it.modifyBooking.addOnDetails[0].addOnName
                        ),
                        it.modifyBooking.adult,
                        it.modifyBooking.children,
                        it.modifyBooking.noOfNights,
                        it.modifyBooking.confirmationId,
                        it.modifyBooking.cancellationId,
                        it.modifyBooking.status,
                        it.modifyBooking.checkIn,
                        it.modifyBooking.checkOut,
                        it.modifyBooking.tax,
                        it.modifyBooking.daily,
                        it.modifyBooking.bookingPolicyDescription,
                        it.modifyBooking.cancelPolicyDescription,
                            it.modifyBooking.description,
                            it.modifyBooking.detailedDescription,
                        GuestInformation(
                            it.modifyBooking.travellerDetails?.get(
                                0
                            )?.email,
                            it.modifyBooking.travellerDetails?.get(0)?.firstName,
                            it.modifyBooking.travellerDetails?.get(0)?.mobile,
                            it.modifyBooking.travellerDetails?.get(0)?.salutation
                        ),
                        Package(it.modifyBooking.price.toString(), it.modifyBooking.packageCode, it.modifyBooking.packageName),
                        it.modifyBooking.packageName.toString(),
                        it.modifyBooking.roomName,
                        it.modifyBooking.roomImgUrl,
                        it.modifyBooking.roomNumber,
                        it.cancelRemark,
                        grandTotal = it.modifyBooking.grandTotal,
                        paidAmount = it.paidAmount,
                        roomCode = it.modifyBooking.roomCode,
                        roomDepositAmount = it.roomDepositAmount,
                        currency =it.modifyBooking.currency,
                        createdTimestamp = it.modifyBooking.createdTimestamp.toString(),
                        modifiedTimestamp = it.modifyBooking.modifiedTimestamp.toString()
                    ),
                        grandTotal = it.grandTotal,
                        paidAmount = it.paidAmount,
                        roomCode = it.roomCode,
                        roomDepositAmount = it.roomDepositAmount,
                        cancelRefundableAmount = it.cancelRefundableAmount,
                        cancelPayableAmount = it.cancelPayableAmount,
                        currency = it.currency,
                        couponDiscountValue = it.couponDiscountValue,
                        createdTimestamp = it.createdTimestamp.toString(),
                        modifiedTimestamp = it.modifiedTimestamp.toString()
                ))
            }else {
                room.add(
                    com.ihcl.order.model.dto.response.Room(
                        it.isPackage,
                        ADDONS(
                            it.addOnDetails?.get(0)?.addOnPrice.toString(),
                            it.addOnDetails?.get(0)?.addOnCode,
                            it.addOnDetails?.get(0)?.addOnName
                        ),
                        it.adult,
                        it.children,
                        it.noOfNights,
                        it.confirmationId,
                        it.cancellationId,
                        it.status,
                        it.checkIn,
                        it.checkOut,
                        it.tax!!,
                        it.bookingPolicyDescription,
                        it.daily,
                        it.cancelPolicyDescription,
                        it.description,
                        it.detailedDescription,
                        it.penaltyAmount,
                        it.penaltyDeadLine,
                        it.cancellationTime,
                        it.penaltyApplicable,
                        GuestInformation(
                            it.travellerDetails?.get(
                                0
                            )?.email,
                            it.travellerDetails?.get(0)?.firstName,
                            it.travellerDetails?.get(0)?.mobile,
                            it.travellerDetails?.get(0)?.salutation
                        ),
                        Package(it.price.toString(), it.packageCode, it.packageName),
                        it.packageName!!,
                        it.roomName,
                        it.roomImgUrl,
                        it.roomNumber,
                        changePrice = null,
                        changeTax = null,
                        cancelRemark = it.cancelRemark,
                        modifyBooking = null,
                        grandTotal = it.grandTotal,
                        paidAmount = it.paidAmount,
                        roomCode = it.roomCode,
                        roomDepositAmount = it.roomDepositAmount,
                        currency = it.currency,
                        cancelPayableAmount = it.cancelPayableAmount,
                        cancelRefundableAmount = it.cancelRefundableAmount,
                        couponDiscountValue = it.couponDiscountValue,
                        createdTimestamp = it.createdTimestamp.toString(),
                        modifiedTimestamp = it.modifiedTimestamp.toString()
                    )
                )
            }
        }
        val totalPayment = if(order.orderLineItems.first().hotel?.promoType.equals(COUPON_PROMO_TYPE, ignoreCase = true) ){
            order.orderLineItems.first().hotel?.grandTotal.toString()
        }else{
            order.gradTotal.toString()
        }
        return ConfirmHotelResponse(
            orderId = order.orderId,
            booking_status = order.orderLineItems[0].hotel?.status,
            isSeb = order.orderLineItems[0].hotel?.isSeb,
            hotelId = order.orderLineItems[0].hotel?.hotelId,
            hotelName = order.orderLineItems[0].hotel?.name,
            hotelAddress = HotelAddress(
                order.orderLineItems.first().hotel?.address?.landmark,
                order.orderLineItems.first().hotel?.address?.pinCode,
                order.orderLineItems.first().hotel?.address?.state
            ),
            check_in = order.orderLineItems[0].hotel?.checkIn,
            check_out = order.orderLineItems[0].hotel?.checkOut,
            guest_name = currentHotelData?.rooms?.get(0)?.travellerDetails?.get(0)?.salutation+" "+currentHotelData?.rooms?.get(0)?.travellerDetails?.get(0)?.firstName + " " + currentHotelData?.rooms?.get(
                0
            )?.travellerDetails?.get(0)?.lastName,
            salutation = currentHotelData?.rooms?.get(0)?.travellerDetails?.get(0)?.salutation,
            itinerary_number = order.orderLineItems[0].hotel?.bookingNumber,
            number_of_guests_ADULTS = order.orderLineItems[0].hotel?.adultCount.toString(),
            number_of_guests_CHILDREN = order.orderLineItems[0].hotel?.childrens.toString(),
            number_of_rooms = order.orderLineItems[0].hotel?.roomCount.toString(),
            paymentBreakup = PaymentBreakup(
                "",
                giftCardPrice.toString(),
                "",
                redeemNeuCoins.toString(),
                "",
                order.basePrice.toString(),
                TaxesAndFees(
                    order.taxAmount.toString(),
                    gstAmount.toBigDecimal().setScale(
                        0,
                        RoundingMode.HALF_UP
                    ).toDouble().toString()
                ),
                totalPayment,
                order.payableAmount.toString()
            ),
            refundBreakUp = null,
            transactionStatus = order.transactionStatus,
            paymentStatus = order.paymentStatus,
            transactionType = order.transactionType,
            cardNumber = cardNumber,
            nameOnCard = nameOnCard,
            cardBrand = cardBrand,
            modifiedCount = order.modifyBookingCount,
            promoCode = order.orderLineItems.first().hotel?.promoCode,
            promoType = order.orderLineItems.first().hotel?.promoType,
            rooms = room,
            emailId = order.orderLineItems.first().hotel?.emailId,
            mobileNumber = order.orderLineItems.first().hotel?.mobileNumber,
            specialRequest = order.orderLineItems.first().hotel?.specialRequest,
            voucherRedemption = order.orderLineItems.first().hotel?.voucherRedemption,
            totalDepositAmount = order.orderLineItems.first().hotel?.totalDepositAmount,
            isDepositPaid = order.orderLineItems.first().hotel?.isDepositPaid,
            totalBasePrice = order.orderLineItems.first().hotel?.totalBasePrice,
            totalTaxPrice = order.orderLineItems.first().hotel?.totalTaxPrice,
            revisedPrice = order.orderLineItems.first().hotel?.revisedPrice,
            grandTotal = order.orderLineItems.first().hotel?.grandTotal,
            amountPaid = order.orderLineItems.first().hotel?.amountPaid,
            payableAmount = order.orderLineItems.first().hotel?.payableAmount,
            refundAmount = order.orderLineItems.first().hotel?.refundAmount,
            oldTotalBasePrice = order.orderLineItems.first().hotel?.oldTotalBasePrice,
            oldGrandTotal = order.orderLineItems.first().hotel?.oldGrandTotal,
            oldTotalTaxPrice = order.orderLineItems.first().hotel?.oldTotalTaxPrice,
            totalPriceChange = order.orderLineItems.first().hotel?.totalPriceChange,
            totalTaxChange = order.orderLineItems.first().hotel?.totalTaxChange,
            paidAmount = order.orderLineItems.first().hotel?.totalCancellationPaidAmount,
            cancellationAmount = order.orderLineItems.first().hotel?.totalCancellationPenaltyAmount,
            cancelPayableAmount = if(order.orderLineItems.first().hotel?.totalCancelPayableAmount!! >0) order.orderLineItems.first().hotel?.totalCancelPayableAmount!! else 0.0,
            cancelRefundableAmount = if(order.orderLineItems.first().hotel?.totalCancelRefundableAmount!! >0) order.orderLineItems.first().hotel?.totalCancelRefundableAmount!! else 0.0,
            paymentMethod = order.paymentMethod,
            country = order.orderLineItems.first().hotel?.country,
            bookingCancelRemarks = order.bookingCancelRemarks,
            balancePayable = order.orderLineItems.first().hotel?.balancePayable,
            bookingNoOfNights = order.orderLineItems.first().hotel?.bookingNoOfNights,
            totalCouponDiscountValue = order.orderLineItems.first().hotel?.totalCouponDiscountValue,
            isDepositFull = order.orderLineItems.first().hotel?.isDepositFull,
            createdTimestamp = order.createdTimestamp.toString(),
            modifiedTimestamp = order.modifiedTimestamp.toString(),
            firstName =order.orderLineItems.first().hotel?.rooms?.first()?.travellerDetails?.first()?.firstName,
            lastName = order.orderLineItems.first().hotel?.rooms?.first()?.travellerDetails?.first()?.lastName,
            paymentTransactionId = paymentTransactionId
        )
    }

    fun mapPreAuthCancelRequest( paymentDetail: PaymentDetail): PreAuthCancelRequest{
            return PreAuthCancelRequest(
                CardNumber = paymentDetail.cardNumber!!,
                OriginalRequest = OriginalRequest(
                    OriginalAmount = paymentDetail.txnNetAmount!!,
                    OriginalApprovalCode = paymentDetail.approvalCode!!,
                    OriginalBatchNumber = paymentDetail.batchNumber!!.toString(),
                    OriginalTransactionId = paymentDetail.transactionId!!.toString()
                )
            )
        }
    fun mapReverseNeuCoinRequest( paymentDetail: PaymentDetailsDTO): ReverseNeuCoinRequest{
        return ReverseNeuCoinRequest(
           redemptionId = paymentDetail.redemptionId,
           pointsToBeReversed = paymentDetail.txnNetAmount.toString(),
           identifier =  Identifier(
               type = "externalId",
               value = paymentDetail.externalId
           )
        )
    }

    fun cancelBookingReqImp(order: Order): CancelBookingRequest{
      return CancelBookingRequest(
          hotelId = order.orderLineItems[0].hotel?.hotelId!!,
          room = order.orderLineItems[0].hotel?.rooms!!.map {
              ConfirmationDetails(
                 roomNumber = it.roomNumber
              )
          },
          isFullCancellation = true,
          cancelReason = "",
          orderId = order.orderId,
          cancelType = "R"
      )
    }
    fun cancelBookingReq(confirmationId: String, cancelBookingRequest: CancelBookingRequest): CancelBookingReq{
        return CancelBookingReq(
            hotelId = cancelBookingRequest.hotelId,
            cancellationReason = cancelBookingRequest.cancelReason,
            crsConfirmationNumber = confirmationId
        )
    }

    fun mapPaymentFailureStatus(paymentStatus: OrderStatusResponse): PaymentFailure{
        return PaymentFailure(
            status = paymentStatus.status!!,
            bankErrorCode = paymentStatus.bank_error_code!!,
            bankErrorMessage =paymentStatus.bank_error_message!!,
            transactionId = paymentStatus.txn_id!!,
            orderId = paymentStatus.order_id!!
        )
    }

    fun mapCreateBookingRequest(orderLineItems: OrderLineItems, i: Int, travelerDetails: TravelerDto, itineraryNumber: String, promoAccessKey: String?, travelIndustryId: String?, promoType: String?):CreateBookingRequest{
        val adultCount = orderLineItems.cartDetails.items?.first()?.hotel?.first()?.room?.get(i)?.adult
        val childCount = orderLineItems.cartDetails.items?.first()?.hotel?.first()?.room?.get(i)?.children
        val inputString = travelerDetails.phoneNo
        val parts = inputString.split(" ")
        val countryCode = parts[0].substring(1) // Remove the '+' symbol
        val phoneNumber = parts[1]
        val product: MutableList<Products> = mutableListOf(Products(
                    product = ProductDetails(
                        rateCode = orderLineItems.cartDetails.items?.first()?.hotel?.first()?.room?.get(i)?.rateCode.toString(),
                        roomCode = orderLineItems.cartDetails.items?.first()?.hotel?.first()?.room?.get(i)?.roomType.toString()
                    ),
                    startDate = orderLineItems.cartDetails.items?.get(0)?.hotel?.get(0)?.checkIn.toString(),
                    endDate = orderLineItems.cartDetails.items?.get(0)?.hotel?.get(0)?.checkOut.toString(),
                ))
        val type = orderLineItems.cartDetails.items?.first()?.hotel?.first()?.promoType.toString()
        val promoCode: String = if(type.contains("-")) {
            val split = type.split("-")
            val promoTypeFirstPart = split[0]
            if (promoTypeFirstPart.equals(Constants.COUPON_PROMO_TYPE, ignoreCase = true)){
                orderLineItems.cartDetails.items?.get(0)?.hotel?.get(0)?.promoCode.toString()
            }else ""
        }else{
            if (type.equals(Constants.COUPON_PROMO_TYPE, ignoreCase = true)){
                orderLineItems.cartDetails.items?.get(0)?.hotel?.get(0)?.promoCode.toString()
            }else ""
        }
        var membershipProgramId : String?=null

       when(travelerDetails.membershipProgramId){
           Constants.MEMBERSHIP_NEU_PASS     ->   {membershipProgramId ="TCP"}
           Constants.MEMBERSHIP_EPICURE      ->   { membershipProgramId = "EPI"}
           Constants.MEMBERSHIP_CHAMBER      ->   { membershipProgramId ="CH"}
       }
        return CreateBookingRequest(
            hotelId = orderLineItems.cartDetails.items?.get(0)?.hotel?.get(0)?.hotelId.toString(),
            status = CREATE_BOOKING_STATUS,
            itineraryNumber = itineraryNumber,
            currency = Currency(
                code = CURRENCY_CODE
            ),
            guests = listOf(GuestDetails(
              role = GUEST_ROLE,
              emailAddress = listOf( EmailAddressDetails(
                  type = EMAIL_ADDRESS_TYPE,
                  value = travelerDetails.email
              )
              ),
              contactNumbers = listOf((ContactNumbers(
                  number = countryCode.plus("-").plus(phoneNumber),
                  code = countryCode,
                  role = ROLE,
                  sortOrder = STORE_ORDER,
                  type = TYPE,
                  use = USE
              )
                      ))
            )),
            notification = Notification(
                bookingComment = travelerDetails.specialRequest,
                DeliveryComments(
                    comment = DELIVERY_COMMENT
                )
            ),
            promotionAccessKey = promoAccessKey,
            promotionType = promoType,
            couponOffersCode = promoCode,
            travelIndustryId = travelIndustryId,
            roomStay = RoomStay(
                startDate = orderLineItems.cartDetails.items?.get(0)?.hotel?.get(0)?.checkIn.toString(),
                endDate = orderLineItems.cartDetails.items?.get(0)?.hotel?.get(0)?.checkOut.toString(),
                numRooms = ROOM_COUNT,
                guestCount = mutableListOf(GuestCount(ADULT, adultCount!!), GuestCount(CHILD, childCount!!)),
                products = product
            ),
            loyaltyMemberships = LoyaltyMemberships(
                levelCode = travelerDetails.memberShipType,
                source = prop.membershipSource,
                membershipID = travelerDetails.membershipId,
                programID = membershipProgramId
            )
        )
    }

    fun orderData(order: Order): OrderDTO{
       var totalAmount: Double
        val rooms = order.orderLineItems[0].hotel!!.rooms!!.map {
            totalAmount = if (order.orderLineItems.first().hotel?.isDepositAmount == true) {
                it.roomDepositAmount!!
            } else if (order.orderLineItems[0].hotel?.voucherRedemption?.isComplementary == true) {
                it.taxAmount!!
            } else {
                it.grandTotal
            }

            log.info("total Amount:$totalAmount and isDepositAmount:${order.orderLineItems.first().hotel?.isDepositAmount}")
            RoomsDTO(
                confirmationId = it.confirmationId,
                price = it.price,
                taxAmount = it.tax!!.amount,
                noOfNights = it.noOfNights,
                totalAmount = totalAmount,
                checkIn = it.checkIn,
                checkOut = it.checkOut,
                roomStatus = it.status
            )
        }

        val paymentDetails = order.paymentDetails?.transaction_1

        val paymentInfo = paymentDetails?.map { payment ->
            PaymentDetailsDTO(
                paymentType = payment.paymentType,
                paymentMethod = payment.paymentMethod,
                paymentMethodType = payment.paymentMethodType,
                txnGateway = payment.txnGateway,
                txnId = payment.txnId,
                txnNetAmount = payment.txnNetAmount,
                txnStatus = payment.txnStatus,
                txnUUID = payment.txnUUID,
                cardNumber = payment.cardNumber,
                cardPin = payment.cardPin,
                preAuthCode = payment.preAuthCode,
                batchNumber = payment.batchNumber,
                approvalCode = payment.approvalCode,
                transactionId = payment.transactionId,
                transactionDateAndTime = payment.transactionDateAndTime,
                cardNo = payment.cardNo,
                nameOnCard = payment.nameOnCard,
                userId = payment.userId,
                redemptionId = payment.redemptionId,
                pointsRedemptionsSummaryId = payment.pointsRedemptionsSummaryId,
                externalId = payment.externalId,
                expiryDate = payment.expiryDate,
                ccAvenueTxnId = payment.ccAvenueTxnId

            )
        }!!.toMutableList()


        return OrderDTO(
            orderId = order.orderId,
            bookingNumber = order.orderLineItems[0].hotel!!.bookingNumber,
            type = "C",
            rooms = rooms,
            paymentDetails = paymentInfo
        )
    }

        fun mapUpdateBookingRequest(order: Order, room: Int, status: String, paymentStringList: String):UpdateBookingRequest{
            val startDate: String?
            val endDate: String?
            val rateCode: String?
            val roomCode: String?
            val expiryDate = order.paymentDetails?.transaction_1?.first()?.expiryDate
            val parts = expiryDate.toString().split("/")
            val inputString = order.orderLineItems[0].hotel!!.rooms!![room].travellerDetails!![0].mobile
            val mobileNumber = inputString.split(" ")
            val countryCode = mobileNumber[0].substring(1) // Remove the '+' symbol
            val phoneNumber = mobileNumber[1]
            val cardNumber = order.paymentDetails?.transaction_1?.first()?.cardNo.toString()
            val cardNumberFormat = cardNumber.filter { it != ' ' }
            val confirmationId: String = order.orderLineItems[0].hotel!!.rooms!![room].confirmationId.toString()
                startDate = order.orderLineItems.first().hotel?.rooms?.get(room)?.checkIn.toString()
                endDate = order.orderLineItems.first().hotel?.rooms?.get(room)?.checkOut.toString()
                rateCode = order.orderLineItems.first().hotel?.rooms?.get(room)?.rateCode.toString()
                roomCode = order.orderLineItems.first().hotel?.rooms?.get(room)?.roomType.toString()
            val adultCount: Int = order.orderLineItems.first().hotel?.rooms?.get(room)?.adult!!.toInt()
            val childCount: Int = order.orderLineItems.first().hotel?.rooms?.get(room)?.children!!.toInt()
            val cardCode = order.paymentDetails?.transaction_1?.firstOrNull()?.paymentMethod.takeIf { !it.isNullOrEmpty() } ?: "VI"
            val paymentDetails = if(order.paymentMethod == PAY_AT_HOTEL && (!order.orderLineItems.first().hotel?.country.equals(COUNTRY, ignoreCase = true))){
                PaymentDetailsInfo(
                    PaymentCard(
                        cardCode = cardCode,
                        cardHolder = order.paymentDetails?.transaction_1?.first()?.nameOnCard,
                        cardNumber = cardNumberFormat,
                        cardSecurityCode = "XXX",
                        expireDate = parts.first().plus(parts.last().takeLast(2))
                    ),
                    type = CREDIT_CARD)
            }else{
                PaymentDetailsInfo(
                    PaymentCard(
                        cardCode = "VI",
                        cardHolder = "Proxy Card",
                        cardNumber = "4111111111111111",
                        cardSecurityCode = "XXX",
                        expireDate = "1225"
                    ),
                    type = CREDIT_CARD)
            }
            return UpdateBookingRequest(
                crsConfirmationNumber = confirmationId,
                hotelId = order.orderLineItems[0].hotel!!.hotelId!!,
                guests = listOf(
                    Guest(
                        personName = PersonName(
                            firstName = order.orderLineItems[0].hotel!!.rooms!![room].travellerDetails!![0].firstName,
                            lastName = order.orderLineItems[0].hotel!!.rooms!![room].travellerDetails!![0].lastName,
                            prefix = order.orderLineItems[0].hotel!!.rooms!![room].travellerDetails!![0].salutation
                        ),
                        payments = listOf(paymentDetails),
                        emailAddress = listOf(
                            EmailAddress(
                                value = order.orderLineItems[0].hotel!!.rooms!![room].travellerDetails!![0].email
                            )
                        ),
                        contactNumbers = listOf(
                            ContactNumber(
                                number = countryCode.plus("-").plus(phoneNumber),
                                type = TYPE
                            )
                        ),
                    )
                ),
                roomStay = RoomStayInfo(
                    endDate = endDate,
                    startDate = startDate,
                    guestCount = mutableListOf(GuestCount(ADULT, adultCount), GuestCount(CHILD, childCount)),
                    numRooms = ROOM_COUNT,
                    products = listOf(
                        ProductInfo(
                            startDate = startDate,
                            endDate = endDate,
                            product = ProductDetailsInfo(
                             rateCode = rateCode,
                             roomCode = roomCode
                            )
                        )
                    )

                ),
                notification = Notification(
                    bookingComment = order.orderLineItems[0].hotel?.specialRequest,
                    DeliveryComments(
                        comment = paymentStringList
                    )
                ),
                status = status
            )
        }


    fun mapTravelerDetails(order: Order): TravelerDto{
        val travelerDetails = order.orderLineItems[0].hotel?.rooms?.get(0)?.travellerDetails?.get(0)
        return TravelerDto(
            salutation = travelerDetails?.salutation,
            gender = travelerDetails!!.gender,
            title = "",
            firstName = travelerDetails.firstName,
            lastName = travelerDetails.lastName,
            email = travelerDetails.email,
            countryCode = travelerDetails.countryCode,
            phoneNo = travelerDetails.mobile,
            membershipNo = travelerDetails.membershipNumber,
            GSTNumber = travelerDetails.gstNumber,
            memberShipType = "",
            specialRequest = "",
            paymentMethod = order.paymentMethod,
            agreedPrivacyPolicy = order.agreedPrivacyPolicy,
            agreedTnc = order.agreedTnc,
            voucherNumber = order.orderLineItems.first().hotel?.voucherNumber?.let { decrypt(it) },
            voucherPin = order.orderLineItems.first().hotel?.voucherPin?. let { decrypt(it) },
            membershipProgramId = "",
            membershipId = "",
            isCampaignOffer = travelerDetails.isCampaignOffer,
            offerIdentifier=travelerDetails.offerIdentifier
        )
    }
    fun mapModifiedPaymentDetails(response: OrderLineItems): MutableList<PaymentDetail>?{
        val paymentDetails = response.cartDetails.paymentDetails?.map { payment ->
            PaymentDetail(
                paymentType = payment.paymentType,
                paymentMethod = payment.paymentMethod,
                paymentMethodType = payment.paymentMethodType,
                txnGateway = payment.txnGateway,
                txnId = payment.txnId,
                txnNetAmount = payment.txnNetAmount,
                txnStatus = payment.txnStatus,
                txnUUID = payment.txnUUID,
                cardNumber = payment.cardNumber,
                cardPin = payment.cardPin,
                preAuthCode = payment.preAuthCode,
                batchNumber = payment.batchNumber,
                approvalCode = payment.approvalCode,
                transactionId = payment.transactionId,
                transactionDateAndTime = payment.transactionDateAndTime,
                cardNo = payment.cardNo,
                nameOnCard = payment.nameOnCard,
                pointsRedemptionsSummaryId = "",
                redemptionId = "",
                externalId = "",
                userId = "",
                expiryDate = "",
                ccAvenueTxnId = ""
            )
        }?.toMutableList()
        return paymentDetails
    }

    fun mapModifiedOrder(
        customerHash: String,
        orderType: OrderType, response: OrderLineItems?, headers: String, travelerDto: TravelerDto?,
        order: Order, modifyBookingCount: Int
    ): Order {

        val rooms = response?.cartDetails?.items?.get(0)?.hotel?.get(0)?.room?.size
        var roomCount = 0
        var adultCount = 0
        var childrens = 0
        for (i in 0 until rooms!!) {
            roomCount++
            if(response.cartDetails.items[0].hotel[0].room[i].modifyBooking != null){
                adultCount += response.cartDetails.items[0].hotel[0].room[i].modifyBooking?.adult!!
                childrens += response.cartDetails.items[0].hotel[0].room[i].modifyBooking?.children!!
            }else{
                adultCount += response.cartDetails.items[0].hotel[0].room[i].adult!!
                childrens += response.cartDetails.items[0].hotel[0].room[i].children!!
            }
        }

        val bookingNoOfNights: Int = getMaxNoOfNights(response.cartDetails.items.first().hotel.first().room,roomCount)
        val travelerDetails = travelerDto?.let { _ ->
            listOf(
                TravellerDetail(
                    salutation = travelerDto.salutation,
                    dateOfBirth = "",
                    address = "",
                    city = "",
                    countryCode = travelerDto.countryCode,
                    customerId = "",
                    customerType = "",
                    email = travelerDto.email,
                    firstName = travelerDto.firstName,
                    gender = travelerDto.gender,
                    gstNumber = travelerDto.GSTNumber,
                    lastName = travelerDto.lastName,
                    membershipNumber = travelerDto.membershipNo,
                    mobile = travelerDto.phoneNo,
                    name = "",
                    secondaryContact = "",
                    state = "",
                    isCampaignOffer=travelerDto.isCampaignOffer,
                    offerIdentifier = travelerDto.offerIdentifier
                )
            )
        }
        val room = response.let { orderLine ->
            orderLine.cartDetails.items?.get(0)?.hotel?.get(0)?.room?.map { room ->
                Room(
                    isPackage = room.isPackageCode,
                    confirmationId = room.confirmationId,
                    cancellationId = room.cancellationId,
                    checkOut = room.checkOut,
                    checkIn = room.checkIn,
                    taxAmount = room.tax.amount,
                    tax = room.tax,
                    roomId = room.roomId,
                    roomName = room.roomName,
                    roomType = room.roomType,
                    rateCode = room.rateCode,
                    rateDescription = room.rateDescription,
                    roomDescription = room.roomDescription,
                    addOnDetails = listOf(
                        AddOnDetail(
                            addOnCode = "",
                            addOnType = "",
                            addOnPrice = 0.0,
                            addOnName = "",
                            addOnDesc = ""
                        )
                    ),
                    discountAmount = 0.0,
                    discountCode = "",
                    isModified = true,
                    isRefundedItem = false,
                    modifiedWith = "",
                    price = room.cost,
                    refundedAmount = "",
                    packageCode = room.packageCode,
                    adult = room.adult,
                    children = room.children,
                    travellerDetails = travelerDetails,
                    packageName = room.packageName,
                    roomNumber = room.roomNumber,
                    status = room.status,
                    roomImgUrl = room.roomImgUrl,
                    changePrice = room.changePrice,
                    changeTax = room.changeTax,
                    bookingPolicyDescription = room.bookingPolicyDescription,
                    daily =room.daily,
                    cancelPolicyDescription = room.cancelPolicyDescription,
                    description = room.description,
                    detailedDescription = room.detailedDescription,
                    penaltyAmount = room.penaltyAmount,
                    penaltyDeadLine = room.penaltyDeadLine,
                    cancellationTime = room.cancellationTime,
                    penaltyApplicable = room.penaltyApplicable,
                    cancelRemark = room.cancelRemark,
                    cancelPayableAmount = room.cancelPayableAmount,
                    cancelRefundableAmount = room.cancelRefundableAmount,
                    grandTotal = room.grandTotal,
                    paidAmount = room.paidAmount,
                    roomDepositAmount = room.roomDepositAmount,
                    roomCode = room.roomCode,
                    currency = room.currency,
                    noOfNights = room.noOfNights,
                    couponDiscountValue = room.couponDiscountValue,
                    createdTimestamp = room.createdTimestamp,
                    modifiedTimestamp = room.modifiedTimestamp,
                    modifyBooking = room.modifyBooking?.let {modify ->
                        ModifyBooking(
                            isPackage = modify.isPackageCode,
                            cancellationId = "0",
                            checkOut = modify.checkOut,
                            checkIn = modify.checkIn,
                            taxAmount = modify.tax?.amount,
                            tax = modify.tax,
                            daily = modify.daily,
                            roomId = modify.roomId,
                            roomName = modify.roomName,
                            roomType = modify.roomType,
                            rateDescription = modify.rateDescription,
                            roomDescription = modify.roomDescription,
                            addOnDetails = listOf(
                                AddOnDetail(
                                    addOnCode = "",
                                    addOnType = "",
                                    addOnPrice = 0.0,
                                    addOnName = "",
                                    addOnDesc = ""
                                )
                            ),
                            discountAmount = 0.0,
                            discountCode = "",
                            isModified = true,
                            isRefundedItem = false,
                            modifiedWith = "",
                            price = modify.cost,
                            refundedAmount = "",
                            packageCode = modify.packageCode,
                            adult = modify.adult,
                            children = modify.children,
                            travellerDetails = travelerDetails,
                            packageName = modify.packageName,
                            roomNumber = modify.roomNumber,
                            status = modify.status,
                            roomImgUrl = modify.roomImgUrl,
                            rateCode = modify.rateCode,
                            confirmationId = modify.confirmationId,
                            bookingPolicyDescription = modify.bookingPolicyDescription,
                            cancelPolicyDescription = modify.cancelPolicyDescription,
                            description = modify.description,
                            detailedDescription = modify.detailedDescription,
                            cancelRemark = null,
                            paidAmount = room.paidAmount,
                            grandTotal = modify.grandTotal,
                            penaltyAmount = modify.penaltyAmount,
                            penaltyDeadLine = modify.penaltyDeadLine,
                            cancellationTime = null,
                            penaltyApplicable = null,
                            currency = modify.currency,
                            noOfNights = modify.noOfNights,
                            roomCode = modify.roomCode,
                            createdTimestamp = modify.createdTimestamp,
                            modifiedTimestamp = modify.modifiedTimestamp
                        )
                    }
                )

            }
        }
        return Order(
            headers,
            customerHash,
            customerEmail = order.customerEmail,
            customerId = "",
            customerMobile = order.customerMobile,
            orderType = orderType,
            billingAddress = BillingAddress(
                address1 = "",
                address2 = "",
                address3 = "",
                city = "",
                country = response.cartDetails.items.first().hotel.first().country.toString(),
                pinCode = "",
                firstName = travelerDto?.firstName.toString(),
                lastName = travelerDto?.lastName.toString(),
                phoneNumber = travelerDto?.phoneNo.toString(),
                countyCodeISO = travelerDto?.countryCode.toString(),
                state = ""
            ),
            channel = "WEB",
            currencyCode = travelerDto?.countryCode.toString(),
            discountAmount = 0.0,
            basePrice = order.basePrice,
            taxAmount = order.taxAmount,
            payableAmount = order.payableAmount,
            gradTotal = order.gradTotal,
            isRefundable = true,
            offers = listOf(
                Offers(
                    offerAmount = 0.0,
                    offerName = "",
                    offerType = ""
                )
            ),
            orderLineItems = mutableListOf(
                OrderLineItem(
                    hotel = Hotel(
                        addOnDetails = listOf(
                            AddOnDetail(
                                addOnCode = "",
                                addOnDesc = "",
                                addOnName = "",
                                addOnPrice = 0.0,
                                addOnType = ""
                            )
                        ),
                        address = Address(
                            city = "",
                            contactNumber = "",
                            directions = "",
                            landmark = response.cartDetails.items.first().hotel.first().hotelAddress.toString(),
                            lat = "",
                            long = "",
                            mapLink = "",
                            pinCode =response.cartDetails.items.first().hotel.first().pinCode.toString(),
                            state = response.cartDetails.items.first().hotel.first().state.toString(),
                            street = ""
                        ),
                        bookingNumber = response.cartDetails.items.first().hotel.first().bookingNumber,
                        category = "",
                        hotelId = response.cartDetails.items.first().hotel.first().hotelId,
                        invoiceNumber = "",
                        invoiceUrl = "",
                        name = response.cartDetails.items.first().hotel.first().hotelName,
                        reservationId = "",
                        rooms = room,
                        status = order.orderLineItems.first().hotel?.status.toString(),
                        childrens = childrens,
                        roomCount = roomCount,
                        adultCount = adultCount,
                        country = response.cartDetails.items.first().hotel.first().country,
                        checkIn = response.cartDetails.items.first().hotel.first().checkIn,
                        checkOut = response.cartDetails.items.first().hotel.first().checkOut,
                        promoCode = response.cartDetails.items.first().hotel.first().promoCode,
                        promoType = response.cartDetails.items.first().hotel.first().promoType,
                        emailId = response.cartDetails.items.first().hotel.first().emailId,
                        mobileNumber = response.cartDetails.items.first().hotel.first().mobileNumber,
                        specialRequest = order.orderLineItems[0].hotel?.specialRequest,
                        voucherRedemption = order.orderLineItems[0].hotel?.voucherRedemption,
                        neupassMembershipId = order.orderLineItems[0].hotel?.neupassMembershipId,
                        isDepositAmount = order.orderLineItems.first().hotel?.isDepositAmount,
                        totalDepositAmount = order.orderLineItems.first().hotel?.totalDepositAmount,
                        revisedPrice = response.cartDetails.items.first().hotel.first().revisedPrice,
                        grandTotal = response.cartDetails.items.first().hotel.first().grandTotal,
                        totalBasePrice = response.cartDetails.items.first().hotel.first().totalBasePrice,
                        totalTaxPrice = response.cartDetails.items.first().hotel.first().totalTaxPrice,
                        amountPaid = response.cartDetails.items.first().hotel.first().amountPaid,
                        refundAmount = response.cartDetails.refundableAmount,
                        payableAmount = response.cartDetails.modifiedPayableAmount,
                        oldTotalBasePrice = order.basePrice,
                        oldTotalTaxPrice = order.taxAmount,
                        oldGrandTotal = order.gradTotal,
                        totalPriceChange = response.cartDetails.totalPriceChange,
                        totalTaxChange = response.cartDetails.totalTaxChange,
                        totalCancelPayableAmount = order.orderLineItems.first().hotel?.totalCancelPayableAmount,
                        totalCancelRefundableAmount = order.orderLineItems.first().hotel?.totalCancelRefundableAmount,
                        totalCancellationPaidAmount = order.orderLineItems.first().hotel?.totalCancellationPaidAmount,
                        totalCancellationPenaltyAmount = order.orderLineItems.first().hotel?.totalCancellationPenaltyAmount,
                        isDepositPaid = order.orderLineItems.first().hotel?.isDepositPaid,
                        balancePayable = response.cartDetails.balancePayable,
                        bookingNoOfNights = bookingNoOfNights,
                        totalCouponDiscountValue = order.orderLineItems.first().hotel?.totalCouponDiscountValue,
                        storeId = order.orderLineItems.first().hotel?.storeId,
                        hotelSponsorId =order.orderLineItems.first().hotel?.hotelSponsorId,
                        synxisId = order.orderLineItems.first().hotel?.synxisId,
                        voucherPin = order.orderLineItems.first().hotel?.voucherPin?. let { decrypt(it) },
                        voucherNumber = order.orderLineItems.first().hotel?.voucherNumber?.let { decrypt(it) },
                        isSeb = response.cartDetails.items.first().hotel.first().isSeb,
                        sebRequestId = response.cartDetails.items.first().hotel.first().sebRequestId
                    ),
                    giftCard = null,
                    loyalty = null
                )
            ),
            orderStatus = INITIATED,
            paymentDetails = TransactionInfo(
                transaction_1 = order.paymentDetails?.transaction_1,
                transaction_2 = null,
                transaction_3 = null,
                transaction_4 = null
            ),
            paymentMethod = order.paymentMethod,
            paymentStatus = order.paymentStatus,
            refundAmount = 0.0,
            transactionId = "",
            modifyBookingCount = modifyBookingCount,
            transactionType = order.transactionType,
            transactionStatus = order.transactionStatus,
            agreedTnc = order.agreedTnc,
            agreedPrivacyPolicy = order.agreedPrivacyPolicy,
            bookingCancelRemarks = null,
            createdTimestamp = order.modifiedTimestamp,
            brandName = ""
        )
    }

    fun mapRedeemGiftCardRequest(paymentDetails: List<PaymentDetail>,i: Int, billAmount: Double,
                                 propertyName: String, bookingNumber: String, orderId: String):RedeemGiftCardRequest{
        return RedeemGiftCardRequest(
            amount = paymentDetails[i].txnNetAmount.toString(),
            cardNumber = decrypt( paymentDetails[i].cardNumber.toString()),
            cardPin = decrypt( paymentDetails[i].cardPin.toString()),
            billAmount = billAmount,
            propertyName = propertyName,
            invoiceNumber = bookingNumber,
            idempotencyKey = orderId.plus("_").plus(i)
        )
    }


    fun mapUpdateOrderResponse(order: Order): OrderResponse {
        val room = order.orderLineItems[0].hotel?.rooms?.map {
            RoomDetailsInfo(
                isPackageCode = it.isPackage,
                confirmationId = it.confirmationId,
                checkOut = it.checkOut,
                checkIn = it.checkIn,
                roomId = it.roomId,
                roomName = it.roomName,
                roomType = it.roomType,
                rateCode = it.rateCode,
                rateDescription = it.rateDescription,
                roomDescription = it.roomDescription,
                tax = it.tax,
                addOnDetails = listOf(
                    AddOnDetailInfo(
                        addOnCode = "",
                        addOnType = "",
                        addOnPrice = 0.0,
                        addOnName = "",
                        addOnDesc = ""
                    )
                ),
                discountAmount = 0.0,
                discountCode = "",
                isModified = true,
                isRefundedItem = true,
                modifiedWith = "",
                price = it.price,
                refundedAmount = "",
                packageName = it.packageName,
                roomNumber = it.roomNumber,
                status = PENDING,
                roomImgUrl = it.roomImgUrl,
                travellerDetails = it.travellerDetails?.map {
                    TravellerDetailsInfo(
                        dateOfBirth = "",
                        address = "",
                        city = "",
                        countryCode = it.countryCode,
                        customerId = "",
                        customerType = "",
                        email = it.email,
                        firstName = it.firstName,
                        gender = it.gender,
                        gstNumber = it.gstNumber,
                        lastName = it.lastName,
                        membershipNumber = it.membershipNumber,
                        mobile = it.mobile,
                        name = "",
                        secondaryContact = "",
                        state = ""
                    )},
                changePrice = it.changePrice,
                changeTax = it.changeTax,
                bookingPolicyDescription = it.bookingPolicyDescription,
                daily =it.daily,
                cancelPolicyDescription = it.cancelPolicyDescription,
                description = it.description,
                detailedDescription = it.detailedDescription,
                grandTotal = it.grandTotal,
                paidAmount = it.paidAmount,
                roomDepositAmount = it.roomDepositAmount,
                roomCode = it.roomCode,
                currency = it.currency,
                couponDiscountValue = it.couponDiscountValue,
                modifiedRoom = it.modifyBooking?.let {mb ->
                    ModifiedRoom(
                        isPackageCode = mb.isPackage,
                        confirmationId = "",
                        checkOut = mb.checkOut,
                        checkIn = mb.checkIn,
                        roomId = mb.roomId,
                        roomName = mb.roomName,
                        roomType = mb.roomType,
                        rateCode = mb.rateCode,
                        rateDescription = mb.rateDescription,
                        roomDescription = mb.roomDescription,
                        addOnDetails = listOf(
                            AddOnDetailInfo(
                                addOnCode = "",
                                addOnType = "",
                                addOnPrice = 0.0,
                                addOnName = "",
                                addOnDesc = ""
                            )
                        ),
                        discountAmount = 0.0,
                        discountCode = "",
                        isModified = true,
                        isRefundedItem = true,
                        modifiedWith = "",
                        price = mb.price,
                        tax = mb.tax,
                        refundedAmount = "",
                        packageName = mb.packageName.toString(),
                        roomNumber = mb.roomNumber,
                        status = PENDING,
                        bookingPolicyDescription = mb.bookingPolicyDescription,
                        cancelPolicyDescription = mb.cancelPolicyDescription,
                        description = mb.description,
                        detailedDescription = mb.detailedDescription,
                        grandTotal = mb.grandTotal,
                        paidAmount = mb.paidAmount,
                        roomCode = it.roomCode,
                        roomDepositAmount = it.roomDepositAmount,
                        currency = it.currency,
                        travellerDetails = it.travellerDetails?.map{
                            TravellerDetailsInfo(
                                dateOfBirth = "",
                                address = "",
                                city = "",
                                countryCode = it.countryCode,
                                customerId = "",
                                customerType = "",
                                email = it.email,
                                firstName = it.firstName,
                                gender = it.gender,
                                gstNumber = it.gstNumber,
                                lastName = it.lastName,
                                membershipNumber = it.membershipNumber,
                                mobile = it.mobile,
                                name = "",
                                secondaryContact = "",
                                state = ""
                            )
                        }


                    )
                }
            )

        }?.toMutableList()
        val paymentInfo = order.paymentDetails?.transaction_1
        val paymentDetailsInfo: MutableList<PaymentDetailInfo>? = paymentInfo?.map { payment ->
            PaymentDetailInfo(
                paymentType = payment.paymentType,
                paymentMethod = payment.paymentMethod,
                paymentMethodType = payment.paymentMethodType,
                txnGateway = payment.txnGateway,
                txnId = payment.txnId,
                txnNetAmount = payment.txnNetAmount,
                txnStatus = payment.txnStatus,
                txnUUID = payment.txnUUID,
                cardNumber = payment.cardNumber,
                preAuthCode = payment.preAuthCode,
                batchNumber = payment.batchNumber,
                approvalCode = payment.approvalCode,
                transactionId = payment.transactionId,
                transactionDateAndTime = payment.transactionDateAndTime,
                cardNo = payment.cardNo,
                nameOnCard = payment.nameOnCard,
                expiryDate = payment.expiryDate
            )
        }?.toMutableList()

        return OrderResponse(
            order.orderId,
            order.customerHash,
            customerEmail = "",
            orderType = OrderType.HOTEL_BOOKING,
            customerMobile = order.customerMobile,
            channel = "",
            currencyCode = "",
            discountAmount = 0.0,
            basePrice = order.basePrice,
            taxAmount = order.taxAmount,
            gradTotal = order.gradTotal,
            payableAmount = order.payableAmount,
            isRefundable = true,
            billingAddress = BillingAddressDetails(
                address1 = "",
                address2 = "",
                address3 = "",
                city = "",
                country = "",
                pinCode = "",
                firstName = "",
                lastName = "",
                phoneNumber = order.customerMobile,
                countyCodeISO = "",
                state = ""
            ),
            offers = listOf(
                OffersDetails(
                    offerAmount = 0.0,
                    offerName = "",
                    offerType = ""
                )
            ),
            orderLineItems = listOf(
                OrderLineItemDetails(
                    hotel = HotelDetailsInfo(
                        addOnDetails = listOf(
                            AddOnDetailInfo(
                                addOnCode = "",
                                addOnDesc = "",
                                addOnName = "",
                                addOnPrice = 0.0,
                                addOnType = ""
                            )
                        ),
                        address = AddressDetails(
                            city = "",
                            contactNumber = "",
                            directions = "",
                            landmark = "",
                            lat = "",
                            long = "",
                            mapLink = "",
                            pinCode = "",
                            state = "",
                            street = ""
                        ),
                        bookingNumber = order.orderLineItems[0].hotel?.bookingNumber,
                        category = "",
                        hotelId = order.orderLineItems.first().hotel?.hotelId.toString(),
                        invoiceNumber = "",
                        invoiceUrl = "",
                        name = order.orderLineItems.first().hotel?.name.toString(),
                        reservationId = "",
                        rooms = room,
                        status = PENDING,
                        country = order.orderLineItems.first().hotel?.country,
                        childrens = order.orderLineItems.first().hotel?.childrens,
                        totalRooms = order.orderLineItems.first().hotel?.roomCount,
                        adults = order.orderLineItems.first().hotel?.adultCount,
                        promoCode = order.orderLineItems.first().hotel?.promoCode,
                        promoType = order.orderLineItems.first().hotel?.promoType,
                        specialRequest = order.orderLineItems[0].hotel?.specialRequest,
                        totalDepositAmount = order.orderLineItems[0].hotel?.totalDepositAmount,
                        totalBasePrice = order.orderLineItems.first().hotel?.totalBasePrice,
                        totalTaxPrice = order.orderLineItems.first().hotel?.totalTaxPrice,
                        grandTotal = order.orderLineItems.first().hotel?.grandTotal,
                        amountPaid = order.orderLineItems.first().hotel?.amountPaid,
                        oldGrandTotal = order.orderLineItems.first().hotel?.oldGrandTotal,
                        oldTotalTaxPrice = order.orderLineItems.first().hotel?.oldTotalTaxPrice,
                        oldTotalBasePrice = order.orderLineItems.first().hotel?.oldTotalBasePrice,
                        payableAmount = order.orderLineItems.first().hotel?.payableAmount,
                        refundAmount = order.orderLineItems.first().hotel?.refundAmount,
                        voucherRedemption = order.orderLineItems.first().hotel?.voucherRedemption,
                        balancePayable = order.orderLineItems.first().hotel?.balancePayable,
                        bookingNoOfNights = order.orderLineItems.first().hotel?.bookingNoOfNights,
                        checkIn = "",
                        checkOut = "",
                        totalCouponDiscountValue = order.orderLineItems.first().hotel?.totalCouponDiscountValue,
                        storeId = order.orderLineItems.first().hotel?.storeId,
                        hotelSponsorId =order.orderLineItems.first().hotel?.hotelSponsorId
                    )
                )
            ),
            customerId = "",
            paymentDetails = paymentDetailsInfo,
            paymentMethod = order.paymentMethod,
            paymentStatus = order.paymentStatus,
            orderStatus = order.orderStatus,
            refundAmount = 0.0,
            transactionId = "",
            totalPriceChange = order.orderLineItems.first().hotel?.totalPriceChange,
            totalTaxChange = order.orderLineItems.first().hotel?.totalTaxChange,
            errorCode = null,
            errorMessage = null
        )
    }

    fun mapGetBookingDetailsRequest(order: Order, room: Int): GetBookingDetailsRequest{
        return GetBookingDetailsRequest(
            hotelId = order.orderLineItems.first().hotel?.hotelId,
            confirmationNumber = order.orderLineItems.first().hotel?.rooms?.get(room)?.confirmationId
        )
    }

    fun mapOrderCancelRequest(order: Order):OrderCancelDTO{
       val rooms = order.orderLineItems.first().hotel?.rooms!!.map {
           CancelRoomsDTO(
               confirmationId = it.confirmationId,
               roomStatus = it.status,
               penaltyApplicable = it.penaltyApplicable,
               penaltyAmount = it.penaltyAmount
           )
       }

        return OrderCancelDTO(
            orderId = order.orderId,
            bookingNumber = order.orderLineItems.first().hotel?.bookingNumber,
            rooms = rooms
        )
    }

    fun mapRefundPaymentDetails(updatedOrders: ApportionOrder): MutableList<PaymentDetail>{
        val paymentDetails: MutableList<PaymentDetail> = updatedOrders.paymentDetails.map { payment ->
            PaymentDetail(
                paymentType = payment.paymentType,
                paymentMethod = payment.paymentMethod,
                paymentMethodType = payment.paymentMethodType,
                txnGateway = payment.txnGateway,
                txnId = payment.txnId,
                txnNetAmount = payment.txnNetAmount,
                txnStatus = payment.txnStatus,
                txnUUID = payment.txnUUID,
                cardNumber = payment.cardNumber,
                cardPin = payment.cardPin,
                preAuthCode = payment.preAuthCode,
                batchNumber = payment.batchNumber,
                approvalCode = payment.approvalCode,
                transactionId = payment.transactionId,
                transactionDateAndTime = payment.transactionDateAndTime,
                cardNo = payment.cardNo,
                nameOnCard = payment.nameOnCard,
                externalId = payment.externalId,
                redemptionId = payment.redemptionId,
                userId = payment.userId,
                pointsRedemptionsSummaryId = payment.pointsRedemptionsSummaryId,
                expiryDate = payment.expiryDate,
                ccAvenueTxnId = payment.ccAvenueTxnId
            )
        }.toMutableList()
        return paymentDetails
    }

    fun mapVoucherRedemptionRequest(order: Order):VoucherRedemptionAvailRequest{
        val voucherRedemption = order.orderLineItems.first().hotel?.voucherRedemption
        return VoucherRedemptionAvailRequest(
            hBitDate = formattedDate(),
            hMemberId = voucherRedemption?.memberId,
            hPrivileges = voucherRedemption?.privileges,
            pin = voucherRedemption?.pin,
            type = voucherRedemption?.type,
            hotelSponsorId =order.orderLineItems.first().hotel?.hotelSponsorId
        )
    }

    fun mapVoucherCancelRedemptionRequest(order: Order):BitCancellationVoucherReversal{
        val voucherCancelRedemption = order.orderLineItems.first().hotel?.voucherRedemption
        return BitCancellationVoucherReversal(
            hBitDate = formattedDate(),
            hMemberId = voucherCancelRedemption?.memberId,
            cancelBitId = voucherCancelRedemption?.availBitId,
            type = voucherCancelRedemption?.type,
            hotelSponsorId = order.orderLineItems.first().hotel?.hotelSponsorId
        )
    }

    fun mapfetchBookingRequest(cancelBookingRequest: CancelBookingRequest, order: Order?): FetchBookingDetailsRequest {
        return FetchBookingDetailsRequest(
            hotelId = cancelBookingRequest.hotelId,
            itineraryNumber = order?.orderLineItems?.get(0)?.hotel?.bookingNumber,
            emailId = null,
            guestPhoneNumber = null,
            arrivalDate = null
        )

    }

    fun mapNeuCoinPaymentDetails(request: TenderModeRequest, amount:String):PaymentDetail {
        val paymentDetails = PaymentDetail(
            paymentType = request.tenderMode,
            paymentMethod = NEU_COINS,
            paymentMethodType = NEU_COINS,
            txnGateway = 0,
            txnId = "",
            txnNetAmount = amount.toDouble(),
            txnStatus =ORDER_STATUS_PENDING,
            txnUUID = "",
            cardNumber = "",
            cardPin = "",
            preAuthCode = "",
            batchNumber = "",
            approvalCode = "",
            transactionId = 0,
            transactionDateAndTime = "",
            cardNo = "",
            nameOnCard = "",
            externalId = "",
            redemptionId = "",
            userId = "",
            pointsRedemptionsSummaryId = "",
            expiryDate = "",
            ccAvenueTxnId = ""
        )
      return paymentDetails
    }
    fun mapJuspayPaymentDetails(amount: String):PaymentDetail {
        val paymentDetails = PaymentDetail(
            paymentType = JUS_PAY,
            paymentMethod ="",
            paymentMethodType = "",
            txnGateway = 0,
            txnId = "",
            txnNetAmount = amount.toDouble(),
            txnStatus =ORDER_STATUS_PENDING,
            txnUUID = "",
            cardNumber = "",
            cardPin = "",
            preAuthCode = "",
            batchNumber = "",
            approvalCode = "",
            transactionId = 0,
            transactionDateAndTime = "",
            cardNo = "",
            nameOnCard = "",
            externalId = "",
            redemptionId = "",
            userId = "",
            pointsRedemptionsSummaryId = "",
            expiryDate = "",
            ccAvenueTxnId = ""
        )
        return paymentDetails
    }
    fun mapTenderModeResponse(order: Order):GiftCardResponse{
        val giftCardDetails: List<GiftCardInfo>? = order.orderLineItems.first().giftCard?.giftCardDetails?.map {
            GiftCardInfo(amount = it.amount,
                sku = it.sku,
                type = it.type)
        }
        return GiftCardResponse(
        order.orderId,
        order.customerHash,
        orderType = order.orderType,
        customerEmail = order.customerEmail,
        customerId = order.customerId,
        customerMobile = order.customerMobile,
        channel = order.channel,
        currencyCode = order.currencyCode,
        discountAmount = order.discountAmount,
        gradTotal = order.gradTotal,
        isRefundable = order.isRefundable,
        payableAmount =order.payableAmount,
        transactionId = order.transactionId,
        billingAddress = GiftCardBillingAddress(
            order.billingAddress?.address1,
            order.billingAddress?.address2,
            order.billingAddress?.address3,
            order.billingAddress?.city,
            order.billingAddress?.country,
            order.billingAddress?.firstName,
            order.billingAddress?.lastName,
            order.billingAddress?.pinCode,
            order.billingAddress?.state,
            order.billingAddress?.phoneNumber,
            order.billingAddress?.countyCodeISO
        ),
        offers = listOf(
            OffersForGiftCard(
                offerAmount = order.offers?.get(0)?.offerAmount,
                offerName = order.offers?.get(0)?.offerName,
                offerType = order.offers?.get(0)?.offerType
            )
        ),
        orderItems = listOf(
            OrderItem(
                shippingAddress = GiftCardShippingAddress(
                    address1 = "",
                    address2 = "",
                    address3 = "",
                    firstName = "",
                    lastName = "",
                    city = "",
                    country = "",
                    pinCode = "",
                    state = "",
                    phoneNumber = "+918789898990",
                    countyCodeISO = ""
                ),
                invoiceNumber = "",
                invoiceUrl = "",
                name = "",
                status = "",
                giftCard =  GiftCardDto(
                    deliveryMethods = DeliveryMethodsInfo(
                        phone =order.orderLineItems[0].giftCard?.deliveryMethods?.phone,
                        email = order.orderLineItems[0].giftCard?.deliveryMethods?.email,
                        smsAndWhatsApp = order.orderLineItems.first().giftCard?.deliveryMethods?.smsAndWhatsApp
                    ),
                    giftCardDetails = giftCardDetails,
                    quantity =order.orderLineItems[0].giftCard?.quantity,
                    receiverAddress = ReceiverAddressDetails(
                        addressLine1 =order.orderLineItems[0].giftCard?.receiverAddress?.addressLine1,
                        addressLine2 =order.orderLineItems[0].giftCard?.receiverAddress?.addressLine2,
                        city = order.orderLineItems[0].giftCard?.receiverAddress?.city,
                        state = order.orderLineItems[0].giftCard?.receiverAddress?.state,
                        country = order.orderLineItems[0].giftCard?.receiverAddress?.country,
                        pinCode = order.orderLineItems[0].giftCard?.receiverAddress?.pinCode,
                    ),
                    receiverDetails = ReceiverInfo(
                        email = order.orderLineItems[0].giftCard?.receiverDetails?.email,
                        firstName = order.orderLineItems[0].giftCard?.receiverDetails?.firstName,
                        lastName = order.orderLineItems[0].giftCard?.receiverDetails?.lastName,
                        message = order.orderLineItems[0].giftCard?.receiverDetails?.message,
                        phone = order.orderLineItems[0].giftCard?.receiverDetails?.phone,
                        rememberMe =order.orderLineItems[0].giftCard?.receiverDetails?.rememberMe,
                        scheduleOn = order.orderLineItems[0].giftCard?.receiverDetails?.scheduleOn
                    ),
                    senderDetails = SenderInfo(
                        email = order.orderLineItems[0].giftCard?.senderDetails?.email,
                        firstName = order.orderLineItems[0].giftCard?.senderDetails?.firstName,
                        lastName = order.orderLineItems[0].giftCard?.senderDetails?.lastName,
                        phone = order.orderLineItems[0].giftCard?.senderDetails?.phone,
                        registerAsNeuPass = order.orderLineItems[0].giftCard?.senderDetails?.registerAsNeuPass
                    )
                )
            )
        ),
         paymentInfo =order.paymentDetails?.transaction_1?.map {
                   PaymentInfo(
                       it.paymentType,
                      it.paymentMethod,
                      it.paymentMethodType,
                      "",
                      "",
                      it.txnNetAmount,
                      ORDER_STATUS_PENDING,
                      "",

                  ) },
        paymentMethod = PaymentMethod.PAY_ONLINE,
        paymentStatus = PaymentStatus.PENDING,
        refundAmount = 0.0,
        taxAmount = 0.0,
        theme = order.orderLineItems[0].giftCard?.giftCardDetails?.get(0)?.theme

        )
    }
    fun mapTenderModeEpicureResponse(order: Order):EpicureNeucoinsRedemptionResponse{
        return EpicureNeucoinsRedemptionResponse(
            order.orderId,
            order.customerHash,
            orderType = order.orderType,
            gradTotal = order.gradTotal,
            isRefundable = order.isRefundable,
            payableAmount =order.payableAmount,
            paymentInfo =order.paymentDetails?.transaction_1?.map {
                PaymentInfo(
                    it.paymentType,
                    it.paymentMethod,
                    it.paymentMethodType,
                    "",
                    "",
                    it.txnNetAmount,
                    ORDER_STATUS_PENDING,
                    "",

                    ) },
            paymentMethod =PaymentMethod.PAY_ONLINE,
            paymentStatus = PaymentStatus.PENDING,
            )
    }

    fun mapGravityVoucherRedeemRequest(voucherCode: String, voucherPin: String, orderId: String, planId: String,billAmount:Double):RedeemGiftCardRequest{
        return RedeemGiftCardRequest(
            amount = GRAVITY_VOUCHER_REDEEM_AMOUNT,
            cardNumber = decrypt( voucherCode ),
            cardPin = decrypt( voucherPin ),
            propertyName = "",
            billAmount =billAmount,
            invoiceNumber = planId,
            idempotencyKey = orderId.plus("_").plus(0)
        )
    }

    private fun getMaxNoOfNights(rooms: List<RoomDetails>?, roomCount: Int?): Int {
        var maxNoOfNights: Int = 0
        if (roomCount == 1) {

            maxNoOfNights = rooms?.first()?.noOfNights!!
        } else {
            val filterRooms = rooms?.filter { r -> !r.status.equals(Constants.CANCELLED, ignoreCase = true) }
            if(filterRooms!!.isNotEmpty()) {
                maxNoOfNights = filterRooms.maxOf { it.noOfNights!! }
            }
        }
        return maxNoOfNights
    }
    fun mapGetBookingDetails(response: HashSet<GetBookingDetailsResponse>):LinkedHashSet<Order> {
        log.info("response of url$response")
        val upcomingBookingResponse = LinkedHashSet<Order>()
        response.first().data?.getHotelBookingDetails?.reservations?.map {
            val couponDiscount = if(response.first().data?.getHotelBookingDetails?.reservations?.first()?.discounts != null){
                response.first().data?.getHotelBookingDetails?.reservations?.first()?.discounts?.first()?.adjustmentAmount
            }else{
                0.0
            }
            val records=Order(
                "", "", "", "", "0", "NON-WEB", "",
                0.0, 0.0, 0.0, 0.0, 0.0,
                false, OrderType.HOTEL_BOOKING, "", null, null,
                orderLineItems = mutableListOf(
                    OrderLineItem(
                        hotel =Hotel(
                            null,
                            null,
                            "","","","","",it?.hotel?.name,"",
                            0,it?.roomStay?.guestCount?.first()?.numGuests,0,it?.roomStay?.startDate,it?.roomStay?.endDate,"","",
                            listOf(
                                Room(false,it?.crsConfirmationNumber,"",it?.status,null,it?.roomStay?.products?.first()?.startDate,
                                    it?.roomStay?.products?.first()?.endDate,it?.roomPrices?.totalPrice?.price?.tax?.amount,Tax(it?.roomPrices?.totalPrice?.price?.tax?.amount,
                                        breakDown = null),"", listOf(DailyRates("",0.0,null)),"","","",0.0,"","",
                                false,"",0.0,"",false,false,"",it?.roomPrices?.totalPrice?.price?.totalAmount,
                                "","","","",it?.content?.rooms?.first()?.name,0,"",
                                "","",0,0,"","",null,"",0.0,
                                0.0,null,0.0,0.0,"",0.0,0.0,0.0,
                                0,couponDiscount
                            )
                            ),"","","","","","","","",0.0,0.0,
                            false,false,false,null,"",0.0,0.0,0.0,0.0,
                            0.0,it?.roomPrices?.totalPrice?.price?.amountPayableNow,0.0,0.0,0.0,0.0,0.0,
                            0.0,couponDiscount,0.0,0.0,0.0,
                            0.0,0.0,0,"",""
                        ),
                        giftCard = null,
                        loyalty = null
                    )
                ),
                0,
                null,
                "",
                "",
                "",
                "",
                0.0,"",false,false,0,null,null,
                "",
                brandName = ""
            )
            upcomingBookingResponse.add(records)
        }
        return upcomingBookingResponse
    }
    suspend fun mapReservationToOrder(response:LinkedHashSet<Reservation?>):LinkedHashSet<Order>{
        log.info("response of url$response")
        val upcomingBookingResponse = LinkedHashSet<Order>()
        response.map {
            val couponDiscount = if(it?.discounts != null){
                 it.discounts.first()?.adjustmentAmount
            }else{
                0.0
            }
            val records=Order(
                "", "", it?.guests?.first()?.emailAddress?.first()?.value!!, "", it.guests[0]?.contactNumbers?.first()!!.number, "NON-WEB", "",
                0.0, 0.0, 0.0, 0.0, 0.0,
                false, OrderType.HOTEL_BOOKING, "", null, null,
                orderLineItems = mutableListOf(
                    OrderLineItem(
                        hotel =Hotel(
                            null,
                            null,
                            it.itineraryNumber,"","","","",it.hotel?.name,"",
                            0,it.roomStay?.guestCount?.first()?.numGuests,0,it.roomStay?.startDate,it.roomStay?.endDate,"","",
                            listOf(
                                Room(false,it.crsConfirmationNumber,"",it.status,null,it.roomStay?.products?.first()?.startDate,
                                    it.roomStay?.products?.first()?.endDate,it.roomPrices?.totalPrice?.price?.tax?.amount,Tax(0.0,
                                        breakDown = null),"", listOf(DailyRates("",0.0,null)),"","","",0.0,"","",
                                    false,"",0.0,"",
                                    isModified = false,
                                    isRefundedItem = false,
                                    modifiedWith = "",
                                    price = it.roomPrices?.totalPrice?.price?.totalAmount,
                                    rateDescription = "",
                                    refundedAmount = "",
                                    roomDescription = "",
                                    roomId = "",
                                    roomName = it.content?.rooms?.first()?.name,
                                    roomNumber = 0,
                                    roomType = "",
                                    rateCode = "",
                                    packageCode = "",
                                    adult = 0,
                                    children = 0,
                                    packageName =it.content?.rates?.first()?.name,
                                    currency = "",
                                    travellerDetails = null,
                                    roomImgUrl = "",
                                    changePrice = 0.0,
                                    changeTax = 0.0,
                                    modifyBooking = null,
                                    grandTotal = 0.0,
                                    paidAmount = 0.0,
                                    roomCode = "",
                                    roomDepositAmount = 0.0,
                                    cancelPayableAmount = 0.0,
                                    cancelRefundableAmount = 0.0,
                                    noOfNights = 0,
                                    couponDiscountValue = couponDiscount
                                )
                            ),"","","","","","","","",0.0,0.0,
                            false,false,false,null,"",0.0,0.0,0.0,0.0,
                            0.0,it.roomPrices?.totalPrice?.price?.amountPayableNow,0.0,0.0,0.0,0.0,0.0,
                            0.0,couponDiscount,0.0,0.0,0.0,
                            0.0,0.0,0,"",""
                        ),
                        giftCard = null,
                        loyalty = null
                    )
                ),
                0,
                null,
                "",
                "",
                "",
                "",
                0.0,"",false,false,0,null,null,
                "",
                brandName = ""
            )
            upcomingBookingResponse.add(records)
        }
        return upcomingBookingResponse
   }

    fun mapModifiedUpdateBookingRequest(order: Order, room: Int,status: String, paymentStringList: String):UpdateBookingRequest{
        val startDate: String?
        val endDate: String?
        val rateCode: String?
        val roomCode: String?
        val inputString = order.orderLineItems[0].hotel!!.rooms!![room].travellerDetails!![0].mobile
        val mobileNumber = inputString.split(" ")
        val countryCode = mobileNumber[0].substring(1) // Remove the '+' symbol
        val phoneNumber = mobileNumber[1]
        val expiryDate = order.paymentDetails?.transaction_1?.first()?.expiryDate
        val cardNumber = order.paymentDetails?.transaction_1?.first()?.cardNo.toString()
        val cardNumberFormat = cardNumber.filter { it != ' ' }

        val parts = expiryDate.toString().split("/")
        val confirmationId: String = order.orderLineItems.first().hotel?.rooms?.get(room)?.modifyBooking?.confirmationId.toString()
        startDate = order.orderLineItems.first().hotel?.rooms?.get(room)?.modifyBooking?.checkIn.toString()
        endDate = order.orderLineItems.first().hotel?.rooms?.get(room)?.modifyBooking?.checkOut.toString()
        rateCode = order.orderLineItems.first().hotel?.rooms?.get(room)?.modifyBooking?.rateCode.toString()
        roomCode = order.orderLineItems.first().hotel?.rooms?.get(room)?.modifyBooking?.roomType.toString()
        val adultCount: Int = order.orderLineItems.first().hotel?.rooms?.get(room)?.modifyBooking?.adult!!.toInt()
        val childCount: Int = order.orderLineItems.first().hotel?.rooms?.get(room)?.modifyBooking?.children!!.toInt()

        val paymentDetails = if(order.paymentMethod == PAY_AT_HOTEL && (!order.orderLineItems.first().hotel?.country.equals(COUNTRY, ignoreCase = true))){
            PaymentDetailsInfo(
                PaymentCard(
                    cardCode = "VI",
                    cardHolder = order.paymentDetails?.transaction_1?.first()?.nameOnCard,
                    cardNumber = cardNumberFormat,
                    cardSecurityCode = "XXX",
                    expireDate = parts.first().plus(parts.last().takeLast(2))
                ),
                type = CREDIT_CARD)
        }else{
            PaymentDetailsInfo(
                PaymentCard(
                    cardCode = "VI",
                    cardHolder = "Proxy Card",
                    cardNumber = "4111111111111111",
                    cardSecurityCode = "XXX",
                    expireDate = "1225"
                ),
                type = CREDIT_CARD)
        }

         return UpdateBookingRequest(
            crsConfirmationNumber = confirmationId,
            hotelId = order.orderLineItems[0].hotel!!.hotelId!!,
            guests = listOf(
                Guest(
                    personName = PersonName(
                        firstName = order.orderLineItems[0].hotel!!.rooms!![room].travellerDetails!![0].firstName,
                        lastName = order.orderLineItems[0].hotel!!.rooms!![room].travellerDetails!![0].lastName,
                        prefix = ""
                    ),
                    payments = listOf(paymentDetails),
                    emailAddress = listOf(
                        EmailAddress(
                            value = order.orderLineItems[0].hotel!!.rooms!![room].travellerDetails!![0].email
                        )
                    ),
                    contactNumbers = listOf(
                        ContactNumber(
                            number = countryCode.plus("-").plus(phoneNumber),
                            type = TYPE
                        )
                    ),
                )
            ),
            roomStay = RoomStayInfo(
                endDate = endDate,
                startDate = startDate,
                guestCount = mutableListOf(GuestCount(ADULT, adultCount), GuestCount(CHILD, childCount)),
                numRooms = ROOM_COUNT,
                products = listOf(
                    ProductInfo(
                        startDate = startDate,
                        endDate = endDate,
                        product = ProductDetailsInfo(
                            rateCode = rateCode,
                            roomCode = roomCode
                        )
                    )
                )

            ),
            notification = Notification(
                bookingComment = order.orderLineItems[0].hotel?.specialRequest,
                DeliveryComments(
                    comment = paymentStringList
                )
            ),
            status = status
        )
    }
    fun mapLoyaltyCreateOrderResponse(order: Order): LoyaltyCreateOrderResponse{
        return LoyaltyCreateOrderResponse(
            orderId = order.orderId,
            customerHash = order.customerHash,
            bankName = order.orderLineItems.first().loyalty?.shareHolderDetails?.bankName.toString(),
            memberShipPurchaseType = order.orderLineItems.first().loyalty?.memberShipPurchaseType.toString(),
            isBankUrl = order.orderLineItems.first().loyalty?.isBankUrl!!,
            isShareHolder = order.orderLineItems.first().loyalty?.isShareHolder!!,
            gstNumber = order.orderLineItems.first().loyalty?.memberCardDetails?.extra_data?.gstNumber,
            address = order.orderLineItems.first().loyalty?.membershipDetails?.user?.address,
            country = order.orderLineItems.first().loyalty?.memberCardDetails?.extra_data?.country,
            dateOfBirth = order.orderLineItems.first().loyalty?.membershipDetails?.user?.date_of_birth,
            extraData = ExtraDataRequest(
                city = order.orderLineItems.first().loyalty?.memberCardDetails?.extra_data?.domicile,
                countryCode = order.orderLineItems.first().loyalty?.memberCardDetails?.extra_data?.country_code,
                state = order.orderLineItems.first().loyalty?.memberCardDetails?.extra_data?.state,
                gravityVoucherCode = order.orderLineItems.first().loyalty?.gravityVoucherCode,
                gravityVoucherPin = order.orderLineItems.first().loyalty?.gravityVoucherPin
            ),
           mobile = order.orderLineItems.first().loyalty?.membershipDetails?.mobile,
            salutation = order.orderLineItems.first().loyalty?.membershipDetails?.user?.salutation,
            user = com.ihcl.order.model.dto.request.User(
                firstName = order.orderLineItems.first().loyalty?.membershipDetails?.user?.first_name,
                lastName = order.orderLineItems.first().loyalty?.membershipDetails?.user?.last_name,
                email = order.orderLineItems.first().loyalty?.membershipDetails?.user?.email
            ),
            addOnCardDetails = order.orderLineItems.first().loyalty?.memberCardDetails?.addOnCardDetails,
            pinCode = order.orderLineItems.first().loyalty?.membershipDetails?.user?.pincode,
            priceSummary = LoyaltyPriceSummaryInfo(
               price = order.basePrice,
                tax = order.taxAmount,
                neuCoins = 0.0,
                totalPrice = order.gradTotal,
                totalPayableAmount = order.payableAmount
            )
        )
    }

    fun mapFetchBookingDetailsRequest(order: Order): FetchBookingDetailsRequest{
        return FetchBookingDetailsRequest(
            emailId = null,
            guestPhoneNumber = null,
            hotelId = order.orderLineItems.first().hotel?.hotelId,
            itineraryNumber = order.orderLineItems.first().hotel?.bookingNumber,
            arrivalDate = null
        )
    }
    private fun extractPaymentMethod(paymentString: String?): String{
        // Define a regex pattern to match "PayOnline" or "Pay At Hotel"
        val pattern = Regex(PAYMENT_STRING_PREFIX, RegexOption.IGNORE_CASE)

        // Find the payment method in the input string
        val matchResult = pattern.find(paymentString.toString())
        // Extract the payment method if found

        return if(matchResult?.value != null){
            if(matchResult.value == PAYONLINE){
                matchResult.value.replace(Regex(PAY_ONLINE_PATTERN), " ").uppercase()
            }else{
                matchResult.value.uppercase()
            }

        }else PAY_AT_HOTEL

    }

    private fun getNoOfNights(checkIn:String?, checkOut:String?):Int{
        val checkInDate = LocalDateTime.parse(checkIn)
        val checkOutDate = LocalDateTime.parse(checkOut)

        val noOfNights = ChronoUnit.DAYS.between(checkInDate.toLocalDate(), checkOutDate.toLocalDate())
        return noOfNights.toInt()
    }

    fun mapUpComingBookings(bookings: List<List<Reservation>>): ArrayList<BookingsResponse> {
        val bookingList = arrayListOf<BookingsResponse>()

        for(i in bookings.indices){
            val bookings: List<BookingsResponse> = bookings[i].map {
                BookingsResponse(
                    customerEmail = it.guests?.first()?.emailAddress?.first()?.value,
                    customerMobile = it.guests?.first()?.contactNumbers?.first()?.number,
                    channel = NON_WEB,
                    orderType = OrderType.HOTEL_BOOKING,
                    paymentMethod = extractPaymentMethod(it.notification?.deliveryComments?.first()?.comment),
                    transactionStatus = extractPaymentMethod(it.notification?.deliveryComments?.first()?.comment),
                    orderLineItems = listOf(
                        OrderItems(
                            hotel = Hotels(
                                bookingNumber = it.itineraryNumber,
                                name = it.hotel?.name,
                                checkIn = it.roomStay?.startDate,
                                checkOut = it.roomStay?.endDate,
                                storeId = it.hotel?.code,
                                synxisId = it.hotel?.id.toString(),
                                bookingNoOfNights = getNoOfNights(it.roomStay?.startDate, it.roomStay?.endDate),
                                rooms = bookings[i].map { room ->
                                    RoomDto(
                                        confirmationId = room.crsConfirmationNumber,
                                        status = room.status,
                                        checkIn = room.roomStay?.startDate,
                                        checkOut = room.roomStay?.endDate,
                                        description = room.content?.rates?.first()?.description,
                                        detailedDescription = room.content?.rates?.first()?.detailedDescription,
                                        roomName = room.content?.rooms?.first()?.name,
                                        rateCode = room.roomStay?.products?.first()?.product?.rateCode,
                                        roomType = room.roomStay?.products?.first()?.product?.roomCode,
                                        roomId = room.roomStay?.products?.first()?.product?.roomCode,
                                        roomCode = room.bookingPolicy?.code,
                                        packageName = room.content?.rates?.first()?.name,
                                        currency = room.currency?.code,
                                        tax = Tax(
                                            amount = room.roomPrices?.averagePrice?.price?.tax?.amount,
                                            breakDown = room.roomPrices?.averagePrice?.price?.tax?.breakDown?.map {
                                                BreakDown(
                                                    amount = it?.amount,
                                                    code = it?.code
                                                )
                                            }
                                        ),
                                        price = room.roomPrices?.totalPrice?.price?.originalAmount,
                                        grandTotal = room.roomPrices?.totalPrice?.price?.totalAmountIncludingTaxesFees,
                                        penaltyAmount = room.bookingDues?.cancelPenalty?.amount,
                                        penaltyDeadLine = room.bookingDues?.cancelPenalty?.deadline,
                                        guestCount = room.roomStay?.guestCount,
                                        cancelPolicyDescription = room.cancelPolicy?.description,
                                        bookingPolicyDescription = room.bookingPolicy?.description,
                                        travellerDetails = listOf(
                                            TravellerDetails(
                                                salutation = it.guests?.first()?.personName?.prefix,
                                                email = it.guests?.first()?.emailAddress?.first()?.value,
                                                firstName = it.guests?.first()?.personName?.firstName,
                                                lastName = it.guests?.first()?.personName?.lastName,
                                                mobile = it.guests?.first()?.contactNumbers?.first()?.number,
                                                countryCode = it.guests?.first()?.contactNumbers?.first()?.number?.substringBefore("-")
                                            )
                                        )
                                    )
                                }
                            )
                        )
                    )

                )

            }
            bookingList.addAll(bookings)
        }
        return bookingList
    }
    fun encrypt(text: String): String {
        val cipher = Cipher.getInstance(Constants.SECURITY_ENCRYPT_ALGORITH)
        val secretKey: Key = SecretKeySpec(prop.dataEncryptionAndDecryptionKey.toByteArray(), Constants.AES)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedBytes = cipher.doFinal(text.toByteArray())
        return Base64.getEncoder().encodeToString(encryptedBytes)
    }

    fun decrypt(encryptedText: String): String {
        val cipher = Cipher.getInstance(Constants.SECURITY_ENCRYPT_ALGORITH)
        val secretKey: Key = SecretKeySpec(prop.dataEncryptionAndDecryptionKey.toByteArray(), Constants.AES)
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val encryptedBytes = Base64.getDecoder().decode(encryptedText)
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes)
    }

}
