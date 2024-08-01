package com.ihcl.order.service.v1

import com.ihcl.order.config.Configuration
import com.ihcl.order.client.client
import com.ihcl.order.config.RedisConfig
import com.ihcl.order.model.dto.request.*
import com.ihcl.order.model.dto.request.ExtraData
import com.ihcl.order.model.dto.response.*
import com.ihcl.order.model.exception.GravtyVoucherNotFoundException
import com.ihcl.order.model.exception.HttpResponseException
import com.ihcl.order.model.exception.PrimaryCardCreationException
import com.ihcl.order.model.exception.QCInternalServerException
import com.ihcl.order.model.exception.ResponseException
import com.ihcl.order.model.schema.*
import com.ihcl.order.model.schema.PaymentDetails
import com.ihcl.order.model.schema.Room
import com.ihcl.order.repository.OrderRepository
import com.ihcl.order.utils.*
import com.ihcl.order.utils.Constants.ACTIVE
import com.ihcl.order.utils.Constants.BALANCE_ENQUIRY
import com.ihcl.order.utils.Constants.BANK
import com.ihcl.order.utils.Constants.BANK_URL_ID
import com.ihcl.order.utils.Constants.BOOKED_STATUS
import com.ihcl.order.utils.Constants.BRAND_TAJ
import com.ihcl.order.utils.Constants.CANCELLED
import com.ihcl.order.utils.Constants.CANCEL_BOOKING
import com.ihcl.order.utils.Constants.CARD_STATUS
import com.ihcl.order.utils.Constants.VOUCHER_CODE_OR_VOUCHER_PIN_EXPIRED
import com.ihcl.order.utils.Constants.VOUCHER_CODE_DE_ACTIVE_CODE
import com.ihcl.order.utils.Constants.VOUCHER_CODE_DE_ACTIVE_ERROR_MESSAGE
import com.ihcl.order.utils.Constants.VOUCHER_CODE_INCORRECT
import com.ihcl.order.utils.Constants.VOUCHER_CODE_OR_VOUCHER_PIN_INCORRECT_ERROR_MESSAGE
import com.ihcl.order.utils.Constants.VOUCHER_PIN_INCORRECT
import com.ihcl.order.utils.Constants.VOUCHER_CARDTYPE_ERROR_MESSAGE
import com.ihcl.order.utils.Constants.CARD_TRANSACTION
import com.ihcl.order.utils.Constants.CARD_TYPE
import com.ihcl.order.utils.Constants.CARD_TYPE_ERROR_MESSAGE
import com.ihcl.order.utils.Constants.CC_AVENUE
import com.ihcl.order.utils.Constants.CHANNEL_WEB
import com.ihcl.order.utils.Constants.CHARGED
import com.ihcl.order.utils.Constants.VOUCHER_CHECK_BALANCE_ERROR_MESSAGE
import com.ihcl.order.utils.Constants.CONFIRMED
import com.ihcl.order.utils.Constants.CONFIRM_BOOKING
import com.ihcl.order.utils.Constants.CONTENT_TYPE
import com.ihcl.order.utils.Constants.CORPORATE
import com.ihcl.order.utils.Constants.COUNTRY
import com.ihcl.order.utils.Constants.COUPON_DISCOUNT_URL
import com.ihcl.order.utils.Constants.COUPON_PROMO_TYPE
import com.ihcl.order.utils.Constants.CPG_ID
import com.ihcl.order.utils.Constants.CREATE_BOOKING
import com.ihcl.order.utils.Constants.CUG_OFFER_TYPE
import com.ihcl.order.utils.Constants.DATE_PATTERN
import com.ihcl.order.utils.Constants.EMPTY_CART_URL
import com.ihcl.order.utils.Constants.EPICURE
import com.ihcl.order.utils.Constants.EPICURE_CARD_TYPE
import com.ihcl.order.utils.Constants.EPICURE_COUNTRY_ID
import com.ihcl.order.utils.Constants.EPICURE_FIESTA
import com.ihcl.order.utils.Constants.EPICURE_NEU_COINS_PREFIX
import com.ihcl.order.utils.Constants.ERROR_CODE_EXPIRY_GC
import com.ihcl.order.utils.Constants.ERROR_CODE_INACTIVE_GC
import com.ihcl.order.utils.Constants.ERROR_CODE_MULTI_ATTEM_EXPIRY_GC
import com.ihcl.order.utils.Constants.ERROR_RESPONSE_MESSAGE_OF_VOUCHER
import com.ihcl.order.utils.Constants.EXPIRY_DATE
import com.ihcl.order.utils.Constants.FAILED
import com.ihcl.order.utils.Constants.FETCH_BOOKING_DETAILS
import com.ihcl.order.utils.Constants.FETCH_NEUCOINS
import com.ihcl.order.utils.Constants.FULFILMENT_STATUS
import com.ihcl.order.utils.Constants.GCO_PAYMENT_STRING
import com.ihcl.order.utils.Constants.GC_NEU_COINS_PREFIX
import com.ihcl.order.utils.Constants.GET_CART_URL
import com.ihcl.order.utils.Constants.GET_CUSTOMER
import com.ihcl.order.utils.Constants.GIFT_CARD
import com.ihcl.order.utils.Constants.GTA_PAYMENT_STRING
import com.ihcl.order.utils.Constants.HASH_FORMAT
import com.ihcl.order.utils.Constants.HOTEL_BOOKING
import com.ihcl.order.utils.Constants.HOTEL_BOOKING_CONFIRMED_STATUS
import com.ihcl.order.utils.Constants.IATA
import com.ihcl.order.utils.Constants.IGNORED_STATUS
import com.ihcl.order.utils.Constants.IHCL
import com.ihcl.order.utils.Constants.INITIATED
import com.ihcl.order.utils.Constants.JUS_PAY
import com.ihcl.order.utils.Constants.LOYALTY_GET_PRIVILEGES
import com.ihcl.order.utils.Constants.LOYALTY_PRIMARY_CARD_URL
import com.ihcl.order.utils.Constants.LOYALTY_REDEEM_NEU_COINS
import com.ihcl.order.utils.Constants.LOYALTY_VOUCHER_AVAIL
import com.ihcl.order.utils.Constants.LOYALTY_VOUCHER_CANCEL_AVAIL
import com.ihcl.order.utils.Constants.LOYOLTY_GIFTCARD_URL
import com.ihcl.order.utils.Constants.MASK
import com.ihcl.order.utils.Constants.MAX_RELOAD_LIMIT
import com.ihcl.order.utils.Constants.MEMBER_ADD_ON_CARD
import com.ihcl.order.utils.Constants.MEMBER_ENROLLMENT
import com.ihcl.order.utils.Constants.MEMBER_LOOKUP_VALIDATION
import com.ihcl.order.utils.Constants.MEMBER_SHIP_PLAN
import com.ihcl.order.utils.Constants.MIN_RELOAD_LIMIT
import com.ihcl.order.utils.Constants.NET_BANKING
import com.ihcl.order.utils.Constants.NEU_COINS
import com.ihcl.order.utils.Constants.ORDER_NOT_FOUND_ERROR_MESSAGE
import com.ihcl.order.utils.Constants.ORDER_STATUS_FAILED
import com.ihcl.order.utils.Constants.ORDER_STATUS_SUCCESS
import com.ihcl.order.utils.Constants.OTHERS
import com.ihcl.order.utils.Constants.PAID_BY
import com.ihcl.order.utils.Constants.PAID_BY_GIFT_CARD
import com.ihcl.order.utils.Constants.PAID_BY_GIFT_CARDS
import com.ihcl.order.utils.Constants.PAID_BY_MULTIPLE_TENDERS
import com.ihcl.order.utils.Constants.PAID_BY_NEU_COINS
import com.ihcl.order.utils.Constants.PAID_ONLINE
import com.ihcl.order.utils.Constants.PAID_PARTIALLY
import com.ihcl.order.utils.Constants.PARAMETERS_ERROR_MESSAGE
import com.ihcl.order.utils.Constants.PARTIALLY_CANCELLED
import com.ihcl.order.utils.Constants.PARTIALLY_CHARGED
import com.ihcl.order.utils.Constants.PARTIALLY_CONFIRMATION
import com.ihcl.order.utils.Constants.PAYMENT_AWAITING
import com.ihcl.order.utils.Constants.PAYMENT_PENDING
import com.ihcl.order.utils.Constants.PAYMENT_URL
import com.ihcl.order.utils.Constants.PAY_AT_HOTEL
import com.ihcl.order.utils.Constants.PAY_DEPOSIT
import com.ihcl.order.utils.Constants.PAY_NOW
import com.ihcl.order.utils.Constants.PAY_ONLINE
import com.ihcl.order.utils.Constants.PENDING
import com.ihcl.order.utils.Constants.PENDING_CONFIRMATION
import com.ihcl.order.utils.Constants.POST_QUERY
import com.ihcl.order.utils.Constants.PRE_QUERY
import com.ihcl.order.utils.Constants.PROCESSING
import com.ihcl.order.utils.Constants.PROMOTION
import com.ihcl.order.utils.Constants.REDEEM_GIFT_CARD
import com.ihcl.order.utils.Constants.REFRESH_SSO_TOKEN_URL
import com.ihcl.order.utils.Constants.RELATIONSHIP_TYPE
import com.ihcl.order.utils.Constants.RELOAD_GIFT_CARD_URL
import com.ihcl.order.utils.Constants.REQUEST_TIME_OUT
import com.ihcl.order.utils.Constants.ROOM_FAILED_STATUS
import com.ihcl.order.utils.Constants.ROOM_STATUS_CANCELLED
import com.ihcl.order.utils.Constants.SEB_BOOKINGS
import com.ihcl.order.utils.Constants.SEPERATOR
import com.ihcl.order.utils.Constants.SHAREHOLDER
import com.ihcl.order.utils.Constants.STATUS_CONFIRMED
import com.ihcl.order.utils.Constants.SUCCESS
import com.ihcl.order.utils.Constants.TAJ
import com.ihcl.order.utils.Constants.TATA_NEU
import com.ihcl.order.utils.Constants.TEGC_LAST_DIGITS
import com.ihcl.order.utils.Constants.TOTAL_BALANCE
import com.ihcl.order.utils.Constants.TOTAL_PAYABLE_AMOUNT_ZERO
import com.ihcl.order.utils.Constants.UPDATE_BOOKING
import com.ihcl.order.utils.Constants.VOUCHER_REDEEM
import com.ihcl.order.utils.Constants.WEB
import com.ihcl.order.utils.Constants.WRONG_VOUCHER_CODE_AND_PIN_ERROR_MESSAGE
import com.ihcl.order.utils.DataMapperUtils.decrypt
import com.ihcl.order.utils.DataMapperUtils.encrypt
import com.ihcl.order.utils.DataMapperUtils.mapGravityVoucherRedeemRequest
import com.ihcl.order.utils.DataMapperUtils.qcConfirmOrderSuccessMappingForLoyalty
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.*
import org.koin.java.KoinJavaComponent
import org.litote.kmongo.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.math.RoundingMode
import java.text.MessageFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet
import kotlin.collections.LinkedHashSet


class OrderService {
    private val apportionOrderService by KoinJavaComponent.inject<ApportionOrderService>(ApportionOrderService::class.java)
    private val orderRepository by KoinJavaComponent.inject<OrderRepository>(OrderRepository::class.java)
    private val log: Logger = LoggerFactory.getLogger(javaClass)
    private val prop = Configuration.env
    private val DATE_FORMAT = "yyyy-MM-dd"
    private val Redemption_Failed="Successfully updated the document"
    private val DATE_MONTH_FORMAT = "dd/MM/yyyy"

    suspend fun createOrder(customerHash: String, travelerDetails: TravelerDto,referer :String?, accessToken: String, refreshToken: String): Any {
        val paymentMethod = travelerDetails.paymentMethod
        handleVoucher(travelerDetails)
        val cartResponse = fetchCartDetails(customerHash)
        val cartItems = cartResponse.cartDetails.items
        if (cartItems.isNullOrEmpty()) {
            throw HttpResponseException(EMPTY_CART_ERR_MSG, HttpStatusCode.BadRequest)
        }
        validateBookingConfirmation(cartResponse, travelerDetails)
        val isFacilityFeeInclude = cartResponse.cartDetails.items
            .firstOrNull()
            ?.hotel
            ?.firstOrNull()
            ?.room
            ?.any { room ->
                room.daily?.any { daily ->
                    daily.tax?.breakDown?.any { breakdown ->
                        breakdown.code.equals("FACILITY", ignoreCase = true)
                    } ?: false
                } ?: false
            } ?: false
        log.info("response from cart ${cartResponse.json}")
        if (!travelerDetails.offerIdentifier.isNullOrEmpty() && travelerDetails.isCampaignOffer && (travelerDetails.paymentMethod.equals(PAY_ONLINE)||travelerDetails.paymentMethod.equals(
                PAY_NOW))){
            log.info("entered into campaign offer validation if block")
            val offerValidationResult=validateOfferCondition(accessToken,travelerDetails.offerIdentifier)
            log.info("Offer validation : $offerValidationResult")
        }
        val orderId = orderRepository.orderIdGeneration(prop.orderIdBooking)

        val orderType = if (cartItems.first().category.equals("HOLIDAYS", ignoreCase = true)) {
            OrderType.HOLIDAYS
        } else {
            OrderType.HOTEL_BOOKING
        }
        val createBookingConfirmation: CreateBookingConfirmation = createBooking(cartResponse, travelerDetails)
        var order = DataMapperUtils.mapCreateOrder(
            customerHash,
            orderType,
            cartResponse,
            orderId,
            travelerDetails
        )

        order.orderLineItems[0].hotel?.bookingNumber = createBookingConfirmation.reservations.first().itineraryNumber

        /*room level confirmation details mapping from hudini create booking API response*/
        for (i in 0 until createBookingConfirmation.reservations.size) {
            order.orderLineItems[0].hotel?.rooms!![i].confirmationId =
                createBookingConfirmation.reservations[i].crsConfirmationNumber
        }

        if (paymentMethod == PAY_AT_HOTEL || paymentMethod == CONFIRM_BOOKING) {
            order.paymentDetails?.transaction_1?.forEach {
                it.paymentType = CC_AVENUE
            }
        } else if (paymentMethod == PAY_DEPOSIT) {
            order.paymentDetails?.transaction_1?.forEach {
                it.txnNetAmount = cartResponse.cartDetails.totalDepositAmount
            }
        }
        if (cartItems.first().hotel.first().room.size == createBookingConfirmation.reservations[0].notAvailableRooms.size) {
            log.info("No rooms available ")
            return DataMapperUtils.notAvailableRoomsCreateOrderResponse(createBookingConfirmation,customerHash, cartResponse,order, orderId, travelerDetails)
        }
        setBrandName(order,referer)
        createRedisEntry(order, accessToken, refreshToken)
        orderRepository.saveOrder(order)
        val type = order.orderLineItems.first().hotel?.promoType.toString()
        val promoType: String = if(type.contains("-")) {
            val split = type.split("-")
            split[0]
        }else type

        if(promoType.equals(COUPON_PROMO_TYPE, ignoreCase = true)) {
            val couponDiscountRequest = getCouponDiscountValue(customerHash, order, isFacilityFeeInclude)

            val couponDiscountAPI = prop.cartServiceHost.plus(COUPON_DISCOUNT_URL)
            val couponDiscountResponse = client.post(couponDiscountAPI) {
                timeout {
                    requestTimeoutMillis = REQUEST_TIME_OUT.toLong()
                }
                contentType(ContentType.Application.Json)
                setBody(couponDiscountRequest)
            }
            if(couponDiscountResponse.status == HttpStatusCode.OK || couponDiscountResponse.status == HttpStatusCode.Created || couponDiscountResponse.status == HttpStatusCode.Accepted){
                val couponDiscountCartResponse = couponDiscountResponse.body<OrderLineItems>()
                log.info("Coupon Discount Response ${couponDiscountCartResponse.json}")
            }
            else{
                throw HttpResponseException(EMPTY_CART_ERR_MSG, HttpStatusCode.BadRequest)
            }
        }

        if(createBookingConfirmation.reservations[0].notAvailableRooms.isNotEmpty()){
            return DataMapperUtils.notAvailableRoomsCreateOrderResponse(createBookingConfirmation,customerHash, cartResponse,order, orderId, travelerDetails)
        }
        order = orderRepository.findOrderByOrderId(orderId!!)
        return DataMapperUtils.mapCartToCreateOrder(order, cartResponse,createBookingConfirmation)
    }
    private suspend fun  getCouponDiscountValue(customerHash: String, order: Order, isFacilityFeeInclude: Boolean): DiscountRequest{
        val discountPriceList = ArrayList<Double>()
        val discountTaxList = ArrayList<Double>()
        val roomNumberList = ArrayList<Int>()
        var discountPrice = 0.0
        var taxPrice = 0.0
        val daily = ArrayList<DailyRates>()
        var dailyRates = DailyRates()
        var tax = Tax()
        var feeAmount = 0.0

        for (room in 0 until order.orderLineItems.first().hotel?.rooms?.size!!) {
            val getBookingDetailsReq = DataMapperUtils.mapGetBookingDetailsRequest(order, room)
            val getBookingDetailsRes = getBookingDetailsImp(getBookingDetailsReq)
            log.info("Get booking details response ${getBookingDetailsRes?.json}")
            if(getBookingDetailsRes != null){
                if (getBookingDetailsRes.data?.getHotelBookingDetails?.reservations?.first()?.discounts != null && order.orderLineItems.first().hotel?.rooms != null) {
                    discountPrice =
                        getBookingDetailsRes.data.getHotelBookingDetails.reservations.first()?.discounts?.first()?.adjustmentAmount!!
                    val taxAndFee =
                        getBookingDetailsRes.data.getHotelBookingDetails.reservations.first()?.roomPrices?.totalPrice?.price
                    getBookingDetailsRes.data.getHotelBookingDetails.reservations.first()?.roomPrices?.priceBreakdowns?.first()?.productPrices?.forEach {
                        val combinedBreakDown= taxAndFee(it, isFacilityFeeInclude)
                        val total = combinedBreakDown.sumOf { it.amount ?: 0.0}
                        dailyRates = DailyRates(
                            date = it?.startDate!!,
                            tax = TaxBreakDown(
                                amount = total,
                                breakDown = combinedBreakDown
                            ),
                            amount = it.price?.totalAmount!!
                        )
                        daily.add(DailyRates(dailyRates.date, dailyRates.amount, dailyRates.tax))
                    }
                    val feeBreakDown = taxAndFee?.fees?.breakDown?.map { breakDown ->
                        BreakDown(
                            amount = breakDown?.amount,
                            code = breakDown?.code
                        )

                    }?: emptyList()
                    feeAmount = feeBreakDown.sumOf { it.amount ?: 0.0 }
                    log.info("feeAmount: $feeAmount")
                    val taxBreakDown = taxAndFee?.tax?.breakDown?.map { breakDown ->
                        BreakDown(
                            amount = breakDown?.amount,
                            code = breakDown?.code
                        )
                    }?: emptyList()
                    tax = if(isFacilityFeeInclude){
                        Tax(
                            amount = taxAndFee?.tax?.amount?.plus(feeAmount),
                            breakDown = taxBreakDown.plus(feeBreakDown)
                        )
                    }else{
                        Tax(
                            amount = taxAndFee?.tax?.amount,
                            breakDown = taxBreakDown
                        )
                    }
                    tax.amount.also {
                        if (it != null) {
                            taxPrice = it
                        }
                    }
                    discountPriceList.add(discountPrice)
                    discountTaxList.add(taxPrice)
                    roomNumberList.add(order.orderLineItems.first().hotel?.rooms!![room].roomNumber)
                }else{
                    throw HttpResponseException("Coupon applied Failed", HttpStatusCode.BadRequest)
                }
            }else{
                throw HttpResponseException("Coupon applied Failed", HttpStatusCode.BadRequest)
            }
        }
        val mapDiscountPrices = roomNumberList.zip(discountPriceList.zip(discountTaxList)).toMap()
        log.info("Discount prices mapping with roomNumbers $mapDiscountPrices")

            return DiscountRequest(customerHash, order.orderId, mapDiscountPrices, daily, tax)

    }
    private fun taxAndFee(productPrices: ProductPrices?, isFacilityFeeInclude: Boolean): List<BreakDownDetails>{

        val taxBreakDown = productPrices?.price?.tax?.breakDown?.map { breakDown ->
            BreakDownDetails(
                amount = breakDown?.amount,
                code = breakDown?.code,
                name=breakDown?.name
            )

        }?: emptyList()

        val feeBreakDown = if(isFacilityFeeInclude) {
            productPrices?.price?.fees?.breakDown?.map { breakDown ->
                BreakDownDetails(
                    amount = breakDown?.amount,
                    code = breakDown?.code,
                    name=breakDown?.name
                )

            }?: emptyList()
        }else{
            emptyList()
        }
        val combinedBreakDown = taxBreakDown + feeBreakDown

        return combinedBreakDown
    }


    private suspend fun handleVoucher(travelerDetails: TravelerDto) {
        if (!travelerDetails.voucherPin.isNullOrEmpty() && !travelerDetails.voucherNumber.isNullOrEmpty()) {
            voucherCheckBalance(travelerDetails.voucherNumber,travelerDetails.voucherPin)
        }
    }
    private suspend fun fetchCartDetails(customerHash: String): OrderLineItems {
        val getCartURL = prop.cartServiceHost.plus(GET_CART_URL)
        log.info("calling get cart api")
        val cartResponse = client.get(getCartURL) {
            contentType(ContentType.Application.Json)
            headers {
                append(CUSTOMERHASH, customerHash)
                append(CATEGORY_TYPE, HOTEL_BOOKING)
            }
        }
        log.info("response body ${cartResponse.bodyAsText()}")
        return cartResponse.body<OrderLineItems>()
    }

    private fun validateBookingConfirmation(cartResponse: OrderLineItems, travelerDetails: TravelerDto) {
        val firstCartItem = cartResponse.cartDetails.items?.first()
        val travelerPaymentMethod = travelerDetails.paymentMethod

        if (travelerPaymentMethod.equals(CONFIRM_BOOKING, ignoreCase = true) &&
            !Constants.GAGCO.split(",").toList().any { t ->
                firstCartItem?.hotel?.first()?.room?.any { room -> room.roomCode.toString() == t } ==true
            }
        ) {
            throw HttpResponseException("Confirm booking not allowed", HttpStatusCode.InternalServerError)
        }

        log.info("response from cart ${cartResponse.json}")
    }

    //Update order when rooms are not available
    private fun mapCreateOrder(
        customerHash: String,
        orderType: OrderType, response: OrderLineItems?, orderId: String, travelerDetails: TravelerDto?
    ): Order {
        return DataMapperUtils.mapCreateOrder(
            customerHash,
            orderType,
            response,
            orderId,
            travelerDetails
        )
    }

    suspend fun deleteOrder(orderId: String): String {
        log.info("enter into delete order service")
        val order = orderRepository.findOrderByOrderId(orderId)
        log.info("getting order by id ${order.json}")
        orderRepository.deleteOrderById(order)
        return SUCCESS
    }
    suspend fun getOrder(orderId: String?, emailId:String?): Order {
        log.info("entered into get order service with order id =$orderId and email id =$emailId")
        if (orderId.isNullOrEmpty() || emailId.isNullOrEmpty()) {
            throw HttpResponseException(MISSING_ORDER_ID_AND_EMAIL_ERROR_MSG, HttpStatusCode.BadRequest)
        }
        val order = orderRepository.getOrderByOrderIdAndEmailId(orderId, emailId)
        log.info("returning the Order found with order id " + order.orderId)
        return order
    }

    suspend fun createOrderForGiftCard(customerHash: String, request:GCCreateOrderRequest,referer :String?, accessToken: String, refreshToken: String): CreateOrderGCResponse {
        val getCartURL = prop.cartServiceHost.plus(GET_CART_URL)
        val orderId = orderRepository.orderIdGeneration(prop.orderIdTEGC)
        log.info("orderId for hotel booking $orderId")
        val cartResponse = client.get(getCartURL) {
            contentType(ContentType.Application.Json)
            headers {
                append(CUSTOMERHASH, customerHash)
                append(CATEGORY_TYPE, GIFT_CARD_PURCHASE)
            }
        }
        log.info("response body ${cartResponse.bodyAsText()}")
        val response = cartResponse.body<GiftCardCartResponse>()
        val orderType = OrderType.GIFT_CARD_PURCHASE
        val order = DataMapperUtils.giftCardCreateOrder(customerHash, response, orderType, orderId, request)
        setBrandName(order,referer)
        createRedisEntry(order, accessToken, refreshToken)
        orderRepository.saveOrder(order)
        return CreateOrderGCResponse(
            orderId
        )
    }

    private suspend fun createBooking(
        orderLineItem: OrderLineItems,
        travelerDetails: TravelerDto
    ): CreateBookingConfirmation {
        var promoAccessKey: String? = null
        var travelIndustryId: String? = null
        var promoType: String? = null
        val promoTypeString = orderLineItem.cartDetails.items?.first()?.hotel?.first()?.promoType.toString()
        val promoTypeFirstPart = promoTypeString.substringBefore("-")

        if (promoTypeFirstPart.equals(CORPORATE, ignoreCase = true) || promoTypeFirstPart.equals(PROMOTION, ignoreCase = true)) {
            promoAccessKey = orderLineItem.cartDetails.items?.first()?.hotel?.first()?.promoCode
            promoType = promoTypeFirstPart
        } else if (promoTypeFirstPart.equals(IATA, ignoreCase = true)) {
            travelIndustryId = orderLineItem.cartDetails.items?.first()?.hotel?.first()?.promoCode
        }
        val room = orderLineItem.cartDetails.items?.get(0)?.hotel?.get(0)!!.room
        var itineraryNumber = ""
        val confirmationNumber = arrayListOf<String?>()
        val notAvailableRooms = mutableListOf<Int>()

        for (i in 0 until room.size) {
            val createBookingRequest =
                DataMapperUtils.mapCreateBookingRequest(orderLineItem, i, travelerDetails, itineraryNumber, promoAccessKey, travelIndustryId, promoType)
            log.info("create booking request ${createBookingRequest.json}")
            val createBookingResponse = hudiniCreteBooking(createBookingRequest)
            log.info("Booking list ${createBookingResponse.json}")
            if(createBookingResponse.data.createHotelBooking.reservations == null){
                val inValidRooms = orderLineItem.cartDetails.items.first().hotel.first().room[i]
                log.info("room number ${inValidRooms.roomNumber} not available, roomType = ${inValidRooms.roomType}, roomCode= ${inValidRooms.rateCode} ")
                if(itineraryNumber.isEmpty()) {
                    itineraryNumber = ""
                }
                confirmationNumber.add(null)
                notAvailableRooms.add(inValidRooms.roomNumber)
            }else{
                itineraryNumber =
                    createBookingResponse.data.createHotelBooking.reservations.first().itineraryNumber.toString()
                confirmationNumber.add(createBookingResponse.data.createHotelBooking.reservations.first().crsConfirmationNumber)
            }
            log.info("list of not available rooms = ${notAvailableRooms.json}")
        }

        return CreateBookingConfirmation(
            reservations = confirmationNumber.map {
                RoomReservationDetails(
                    itineraryNumber = itineraryNumber,
                    crsConfirmationNumber = it,
                    notAvailableRooms = notAvailableRooms
                )
            }
        )
    }

    private suspend fun hudiniCreteBooking(createBookingRequest: CreateBookingRequest): CreateBookingResponse {
        val createBookingURL = prop.hudiniServiceHost.plus(CREATE_BOOKING)
        val response = client.post(createBookingURL) {
            log.info("create booking API Request ${createBookingRequest.json}")
            headers {
                append(HttpHeaders.ContentType, ContentType.Application.Json)
            }
            setBody(createBookingRequest)
        }
        log.info("Create booking response body ${response.bodyAsText()}")
        val createBookingResponse = response.body() as CreateBookingResponse
        if (response.status == HttpStatusCode.OK || response.status == HttpStatusCode.Created) {
            return createBookingResponse
        } else {
            log.error("Create booking failed due to ${response.status} with reason of ${response.bodyAsText()}")
            throw HttpResponseException(createBookingResponse.data.createHotelBooking.message, HttpStatusCode.NotFound)
        }
    }

    suspend fun confirmPayment(confirmOrder: ConfirmOrder, jwtToken: String?, customerHash: String?): Any?{
        val orderId = confirmOrder.orderId
        if(orderId.isNullOrEmpty() || customerHash.isNullOrEmpty()){
            throw HttpResponseException(ORDER_ID_AND_CUSTOMER_HASH_ERR_MSG, HttpStatusCode.BadRequest)
        }
        val order: Order = orderRepository.getOrderByOrderId(orderId, customerHash)
        log.info("order details from DB${order.json}")
        if((order.orderType == OrderType.HOTEL_BOOKING || order.orderType == OrderType.HOLIDAYS) &&
            ((order.paymentMethod.equals(CONFIRM_BOOKING, ignoreCase = true))) &&
            (!(Constants.GAGCO.split(",").toList().any { t -> order.orderLineItems.first().hotel?.rooms!!.any { it.roomCode.toString()== t } }))){
            throw HttpResponseException(CONFIRM_BOOKING_ERR_MSG, HttpStatusCode.NotAcceptable)
        }

        return if(order.orderStatus.equals(ORDER_STATUS_SUCCESS, ignoreCase = true) || order.orderStatus.equals(PROCESSING, ignoreCase = true) ||
            order.orderStatus.equals(ORDER_STATUS_FAILED, ignoreCase = true) ){
            orderStatusSuccessResponse(order)
        }else {
            orderStatusPaymentResponse(order, confirmOrder, jwtToken, false)
        }
    }

    private fun orderStatusSuccessResponse(order: Order): Any{
        return when (order.orderType) {
            OrderType.GIFT_CARD_PURCHASE -> {
                log.info("Payment CHARGED and card detail persist in DB ${DataMapperUtils.qcConfirmOrderSuccessMapping(order,
                    paymentTransactionId(order.orderType, order.paymentDetails?.transaction_1?.toList()))}")
                DataMapperUtils.qcConfirmOrderSuccessMapping(order, paymentTransactionId(order.orderType, order.paymentDetails?.transaction_1?.toList()))
            }
            OrderType.HOTEL_BOOKING, OrderType.HOLIDAYS -> {
                DataMapperUtils.mapConfirmHotelBookingResponse(order, paymentTransactionId(order.orderType, order.paymentDetails?.transaction_1?.toList()))
            }
            OrderType.MEMBERSHIP_PURCHASE -> {
                log.info(
                    "Member Ship  and amount persist in DB ${
                        qcConfirmOrderSuccessMappingForLoyalty(
                            order, paymentTransactionId(order.orderType, order.paymentDetails?.transaction_1?.toList())
                        )
                    }"
                )
                qcConfirmOrderSuccessMappingForLoyalty(order, paymentTransactionId(order.orderType, order.paymentDetails?.transaction_1?.toList()))
            }
            else -> {
                return DataMapperUtils.qcConfirmOrderSuccessMappingForReload(order,
                    paymentTransactionId(order.orderType, order.paymentDetails?.transaction_1?.toList())
                )
            }
        }
    }

    private suspend fun orderStatusPaymentResponse(order: Order, confirmOrder: ConfirmOrder, jwtToken: String?, isCronJob: Boolean): Any? {
        val orderId = confirmOrder.orderId
        var updatedOrder: Order
        when {
            isJusPayPendingAndNotBankUrl(order) -> {
                  val response: HttpResponse = client.post(prop.paymentServiceHost.plus(PAYMENT_URL)) {
                      contentType(ContentType.Application.Json)
                      setBody(confirmOrder)
                  }
                  log.info("response received from jusPay, order status ${response.bodyAsText()}")
                  return confirmOrderResponse(response, order, jwtToken, isCronJob)
            }
           /* isJusPayCharged(order)  -> {
                return jusPaySuccessUpdate(order, orderStatus, jwtToken, isCronJob)
            }*/
            order.payableAmount == 0.0 ->{
                when{
                order.orderType == OrderType.GIFT_CARD_PURCHASE -> {
                        return processGiftCardPurchase(order, jwtToken, isCronJob)
                }
                (order.orderLineItems.first().loyalty?.isBankUrl == false) && order.orderType == OrderType.MEMBERSHIP_PURCHASE -> {
                        log.info("orderType is ${OrderType.MEMBERSHIP_PURCHASE} with payable amount 0.0")
                        updatedOrder = giftCardRedeemImp(order, jwtToken, isCronJob)
                        updatedOrder = updatePaymentStatus(updatedOrder)
                        orderRepository.findOneAndUpdateOrder(orderId, updatedOrder)
                        emptyCart(order.customerHash, MEMBERSHIP_PURCHASE)
                        voucherRedemption(updatedOrder)
                        return enrollMemberAndFetchMemberProfile(orderId.toString(), paymentTransactionId(order.orderType, updatedOrder.paymentDetails?.transaction_1?.toList()))

                }
                (order.orderType == OrderType.HOTEL_BOOKING || order.orderType == OrderType.HOLIDAYS) && order.paymentMethod == PAY_ONLINE -> {
                        log.info("hotel booking journey for ${order.paymentMethod} and payable amount 0.0")
                        return processHotelBookingConfirmation(order, jwtToken, isCronJob)
                }
                order.orderType == OrderType.MEMBERSHIP_PURCHASE && order.orderLineItems.first().loyalty?.isBankUrl == true -> {
                        log.info("orderType is ${OrderType.MEMBERSHIP_PURCHASE} for bank URL")
                        order.paymentStatus = PaymentStatus.CHARGED.toString()
                        orderRepository.findOneAndUpdateOrder(orderId.toString(), order)
                        emptyCart(order.customerHash, MEMBERSHIP_PURCHASE)
                        voucherRedemption(order)
                        return enrollMemberAndFetchMemberProfile(orderId.toString(), paymentTransactionId(order.orderType, order.paymentDetails?.transaction_1?.toList()))

                    }
            }

            }

            (order.orderType == OrderType.HOTEL_BOOKING || order.orderType == OrderType.HOLIDAYS) && (order.paymentMethod == CONFIRM_BOOKING || order.paymentMethod == PAY_AT_HOTEL) -> {
                log.info("hotel booking journey for ${order.paymentMethod}")
                updatedOrder = updatePaymentStatus(order)
                orderRepository.findOneAndUpdateOrder(orderId, updatedOrder)
                voucherRedemptionAvailImp(order.orderId)
                updatedOrder = orderRepository.findOrderByOrderId(orderId)
                voucherRedeem(updatedOrder)
                if(!isCronJob) {
                    emptyCart(order.customerHash, HOTEL_BOOKING)
                }
                if(updatedOrder.modifyBookingCount == 0 && updatedOrder.paymentStatus.equals(CHARGED, ignoreCase = true)) {
                    val orderData = DataMapperUtils.orderData(order)
                    log.info("orderId: ${order.orderId} apportioned order data request::${orderData.json}")
                    val orders = ApportionOrder()
                    apportionOrderService.getApportionedOrder(orders, orderData)
                    apportionOrderService.createRooms(orders)
                }

                val roomSize = updatedOrder.orderLineItems.first().hotel?.rooms?.size

                for (i in 0 until roomSize!!) {
                    updatedOrder.orderLineItems.first().hotel?.rooms?.get(i)?.paidAmount = 0.0
                }
                updatedOrder.orderLineItems.first().hotel?.amountPaid = 0.0
                updatedOrder.orderLineItems.first().hotel?.isDepositPaid = updatedOrder.orderLineItems.first().hotel?.isDepositAmount

                updatedOrder = updateBookingImp(updatedOrder, CONFIRMED)
                updatedOrder = transactionStatus(updatedOrder)
                orderRepository.findOneAndUpdateOrder(orderId.toString(), updatedOrder)
                // If it is SEB, calling sebBookings API to post employee details.
                if (updatedOrder.orderLineItems[0].hotel?.isSeb == true) {
                    log.info("Its a SEB order :${order.orderLineItems[0].hotel?.rooms?.json}")
                    val confirmationIds = mutableListOf<String?>()
                    updatedOrder.orderLineItems.first().hotel?.rooms?.forEach {
                        if(it.status.equals(CONFIRMED, ignoreCase = true)){
                            confirmationIds.add(it.confirmationId)
                        }
                    }
                    log.info("confirmation ID's $confirmationIds")
                    if(confirmationIds.isNotEmpty()) {
                        callSebBookings(order, confirmationIds)
                    }
                }
                deleteCacheCollection(order)
                return DataMapperUtils.mapConfirmHotelBookingResponse(updatedOrder,  paymentTransactionId(order.orderType, updatedOrder.paymentDetails?.transaction_1?.toList()))
            }
        }
        return null
    }

    private fun isJusPayPendingAndNotBankUrl(order: Order): Boolean {
        return order.paymentDetails?.transaction_1
            ?.any { p -> (p.paymentType == JUS_PAY)}
                ?: false && (order.orderLineItems.first().loyalty?.isBankUrl == null || order.orderLineItems.first().loyalty?.isBankUrl == false)
    }
    private fun isJusPayCharged(order: Order): Boolean {
        return order.paymentDetails?.transaction_1
            ?.any { p -> p.paymentType == JUS_PAY && p.txnStatus == CHARGED }
            ?: false
    }

    private suspend fun processGiftCardPurchase(order: Order, jwtToken: String?, isCronJob: Boolean): Any{
        var updatedOrder = giftCardRedeemImp(order, jwtToken, isCronJob)
        updatedOrder = updatePaymentStatus(updatedOrder)
        orderRepository.findOneAndUpdateOrder(updatedOrder.orderId, updatedOrder)
        emptyCart(order.customerHash, GIFT_CARD_PURCHASE)
        val paymentInfo = order.paymentDetails?.transaction_1
        val paymentReference = paymentReferenceNumber(paymentInfo).toString()
        val paymentStatus = paymentStatusMapping(paymentInfo)
        return buyGiftCard(DataMapperUtils.qcSuccessOrderStatus(updatedOrder, paymentReference, paymentStatus), updatedOrder,
            paymentTransactionId(order.orderType, updatedOrder.paymentDetails?.transaction_1?.toList()))
    }

    private suspend fun processHotelBookingConfirmation(order: Order, jwtToken: String?, isCronJob: Boolean): ConfirmHotelResponse?{
        var updatedOrder: Order?
        val orderId = order.orderId

        if(order.orderType == OrderType.HOTEL_BOOKING || order.orderType == OrderType.HOLIDAYS){
            log.info("orderType is ${order.orderType} using Tenders")
            updatedOrder = giftCardRedeemImp(order, jwtToken, isCronJob)
            updatedOrder = updatePaymentStatus(updatedOrder)
            orderRepository.findOneAndUpdateOrder(orderId, updatedOrder)
            if(!isCronJob) {
                emptyCart(order.customerHash, HOTEL_BOOKING)
            }
            voucherRedemptionAvailImp(order.orderId)
            updatedOrder = orderRepository.findOrderByOrderId(orderId)
            voucherRedeem(updatedOrder)
            if(updatedOrder.modifyBookingCount == 0 && updatedOrder.paymentStatus.equals(CHARGED, ignoreCase = true)) {
                updatedOrder = calculateApportionedValues(updatedOrder)
            }
            updatedOrder = updateBookingImp(updatedOrder, CONFIRMED)
            updatedOrder = transactionStatus(updatedOrder)
            orderRepository.findOneAndUpdateOrder(orderId, updatedOrder)
            deleteCacheCollection(updatedOrder)
            return DataMapperUtils.mapConfirmHotelBookingResponse(updatedOrder, paymentTransactionId(order.orderType, updatedOrder.paymentDetails?.transaction_1?.toList()))
        }
       return null
    }
    private suspend fun getDates(order: Order):Order{
        log.info("enter into get dates")
        val dateFormat = SimpleDateFormat(DATE_FORMAT)

        val rooms = order.orderLineItems.first().hotel?.rooms

        val checkInList = arrayListOf<String?>()
        val checkOutList = arrayListOf<String?>()

        rooms?.forEach {
            if(it.status != ROOM_STATUS_CANCELLED) {
                if (it.modifyBooking != null) {
                    checkInList.add(it.modifyBooking.checkIn)
                    checkOutList.add(it.modifyBooking.checkOut)
                } else {
                    checkInList.add(it.checkIn)
                    checkOutList.add(it.checkOut)
                }
            }
        }
        log.info("checkIn dates $checkInList")
        val checkInDates = checkInList.map { dateFormat.parse(it) }
        val minCheckInDate = checkInDates.minByOrNull { it.time }
        log.info("min checkIn date ${dateFormat.format(minCheckInDate)}")
        order.orderLineItems.first().hotel?.checkIn = dateFormat.format(minCheckInDate)

        log.info("checkOut dates $checkOutList")
        val checkOutDates = checkOutList.map { dateFormat.parse(it) }
        val maxCheckoutDate = checkOutDates.maxByOrNull { it.time }
        log.info("max checkOut date ${dateFormat.format(maxCheckoutDate)}")
        order.orderLineItems.first().hotel?.checkOut = dateFormat.format(maxCheckoutDate)

        orderRepository.findOneAndUpdateOrder(order.orderId, order)
        return order

    }

    private fun  updatePaymentStatus(order: Order): Order {
        var chargedCount = 0
        if
                ((order.orderType == OrderType.
            HOLIDAYS
                    || order.orderType == OrderType.
            HOTEL_BOOKING
                    ) &&
            (!(order.orderLineItems.
            first
                ().hotel?.country.
            equals
                (
                COUNTRY
                ,
                ignoreCase =
                true
            ) )|| (order.paymentMethod.
            equals
                (
                CONFIRM_BOOKING
                ,
                ignoreCase =
                true
            )))){
            order.paymentMethod = PAY_AT_HOTEL
            order.paymentStatus = PaymentStatus.CHARGED.toString()
        }else {
            order.paymentDetails?.transaction_1?.forEach {
                if (it.txnStatus.equals(CHARGED, ignoreCase = true)) {
                    chargedCount++
                }
            }
            when {
                order.paymentDetails?.transaction_1?.size == chargedCount -> {
                    order.paymentStatus = PaymentStatus.CHARGED.toString()

                }
                order.paymentDetails?.transaction_1?.any { p -> (p.txnStatus.equals(PaymentStatus.PENDING.toString())) } == true -> {
                    order.paymentStatus = PaymentStatus.PENDING.toString()

                }
                order.paymentDetails?.transaction_1?.any { p -> (p.txnStatus.equals(PaymentStatus.FAILED.toString())) } == true -> {
                    order.paymentStatus = PaymentStatus.FAILED.toString()
                }
            }
        }
        log.info("update booking details latest order ${order.paymentStatus}")
        return order

    }

    private suspend fun buyGiftCard(qcCreateBooking: QCCreateBooking, order: Order, paymentTransactionId: String?): Any {
        val updatedOrder: Order?
        if(!order.paymentStatus.equals(CHARGED, ignoreCase = true)) {
            throw HttpResponseException("payment not done", HttpStatusCode.BadRequest)
        }
        try {
            val loyaltyURL = prop.loyaltyServiceHost.plus(LOYOLTY_GIFTCARD_URL)

            log.info("buy gift card ${qcCreateBooking.json}")

            val response = client.post(loyaltyURL) {
                headers {
                    append(HttpHeaders.ContentType, ContentType.Application.Json)
                }
                setBody(qcCreateBooking)
                timeout {
                    requestTimeoutMillis = REQUEST_TIME_OUT.toLong()
                }
            }
            log.info("response status ${response.status}")
            log.info("create booking response received as ${response.bodyAsText()}")

           return when (response.status) {
                HttpStatusCode.OK -> {
                    log.info("order gift card success")
                    val giftCardResponse = response.body() as BuyingGCSuccess
                     when (giftCardResponse.status) {
                        GiftCardStatus.COMPLETE.toString() -> {
                            val giftCardDetails = order.orderLineItems[0].giftCard?.giftCardDetails
                            for (i in 0 until giftCardResponse.cards?.size!!) {
                                giftCardDetails?.get(i)?.cardNumber = giftCardResponse.cards[i]?.cardNumber?.let { encrypt(it) }
                                giftCardDetails?.get(i)?.cardPin = giftCardResponse.cards[i]?.cardPin?.let { encrypt(it) }
                                giftCardDetails?.get(i)?.cardId = giftCardResponse.cards[i]?.cardId.toString()
                                giftCardDetails?.get(i)?.validity = giftCardResponse.cards[i]?.validity
                                giftCardDetails?.get(i)?.orderId = giftCardResponse.orderId
                            }
                            updatedOrder = transactionStatus(order)
                            orderRepository.findOneAndUpdateOrder(updatedOrder.orderId, updatedOrder)
                            DataMapperUtils.qcConfirmOrderSuccessMapping(updatedOrder, paymentTransactionId)
                        }

                        GiftCardStatus.PROCESSING.toString() -> {
                            log.info("physical gift card ${giftCardResponse.json}")
                            giftCardProcessing(order, giftCardResponse.orderId, paymentTransactionId)
                        }

                        else -> {
                            order.orderStatus = PENDING
                            orderRepository.findOneAndUpdateOrder(order.orderId, order)
                            throw HttpResponseException(BUY_GIFT_CARD_FAILED_ERR_MSG, HttpStatusCode.NotAcceptable)
                        }
                    }
                }

                HttpStatusCode.Accepted -> {
                    val processingResponse = response.body() as ProcessingResponse
                         giftCardProcessing(order, processingResponse.orderId, paymentTransactionId)
                }

                else -> {
                    order.orderStatus = PENDING
                    orderRepository.findOneAndUpdateOrder(order.orderId, order)
                    throw HttpResponseException(BUY_GIFT_CARD_FAILED_ERR_MSG, HttpStatusCode.NotAcceptable)
                }
            }
        }catch (e: Exception){
            log.error("Buy GC failed with an exception ${e.cause} and ${e.message}")
            order.orderStatus = PENDING
            orderRepository.findOneAndUpdateOrder(order.orderId, order)
            throw HttpResponseException(BUY_GIFT_CARD_FAILED_ERR_MSG, HttpStatusCode.NotAcceptable)
        }
    }
    private suspend fun giftCardProcessing(order: Order, orderId: String?, paymentTransactionId: String?): Any{
        for (i in 0 until order.orderLineItems[0].giftCard?.giftCardDetails!!.size) {
            order.orderLineItems[0].giftCard?.giftCardDetails?.get(i)?.orderId =
                orderId
        }
        var updatedOrder = transactionStatus(order)
        updatedOrder.orderStatus = PROCESSING
        orderRepository.findOneAndUpdateOrder(updatedOrder.orderId, updatedOrder)
        return DataMapperUtils.qcConfirmOrderSuccessMapping(updatedOrder, paymentTransactionId)
    }

    suspend fun createOrderForReloadGiftCard(
        customerHash: String, reloadRequest: ReloadBalRequest,referer :String?
    ): ReloadOrderResponse {
        val balEnqRes = balanceEnquiry(reloadRequest.cardNumber)
        val giftCardValues: GiftCardValues? = orderRepository.getGiftCardValues(CPG_ID)
        if (balEnqRes.ResponseCode == "0") {
            giftCardValues?.giftCardValues?.withIndex()?.find { (_, type) ->
                type.cardType.equals(balEnqRes.Cards?.first()?.CardType, ignoreCase = true)}?.let {
                if (reloadRequest.amount >= it.value.minReloadLimit){
                    if (reloadRequest.amount <=it.value.maxReloadLimit){
                        val reloadAmount=  reloadRequest.amount.plus(balEnqRes.Cards?.first()!!.Balance)
                        if(reloadAmount <= it.value.maxReload){
                            val orderId = orderRepository.orderIdGeneration(prop.orderIdTEGC)
                            val orderType = OrderType.RELOAD_BALANCE
                            val order = DataMapperUtils.mapReloadGiftCard(
                                customerHash,
                                reloadRequest,
                                orderType,
                                orderId
                            )
                            setBrandName(order,referer)
                            orderRepository.saveOrder(order)
                            return ReloadOrderResponse(
                                orderId = orderId,
                                cardNumber = reloadRequest.cardNumber,
                                amount = reloadRequest.amount,
                                customerHash = customerHash)
                        }else throw HttpResponseException(TOTAL_BALANCE, HttpStatusCode.BadRequest)
                    }else throw HttpResponseException(MAX_RELOAD_LIMIT, HttpStatusCode.BadRequest)
                }else throw HttpResponseException(MIN_RELOAD_LIMIT, HttpStatusCode.BadRequest)
            }?:run{ throw HttpResponseException(CARD_TYPE_ERROR_MESSAGE, HttpStatusCode.BadRequest) }
        }
        throw HttpResponseException(
            balEnqRes.Cards?.get(0)?.ResponseMessage.toString(),
            HttpStatusCode.NotAcceptable
        )
    }

    private suspend fun balanceEnquiry(cardNumber: String): BalanceEnquiryResponse {
        log.info("Enter into balance enquiry")
        val balEnquiryReq = DataMapperUtils.mapBalanceEnquiryRequest(cardNumber)
        val balEnqURL = prop.loyaltyServiceHost.plus(BALANCE_ENQUIRY)
        val response = client.post(balEnqURL) {
            headers {
                append(HttpHeaders.ContentType, CONTENT_TYPE)
            }
            setBody(balEnquiryReq)
            timeout {
                requestTimeoutMillis = REQUEST_TIME_OUT.toLong()
            }
        }
        log.info("response received from balance enquiry of cardNo $cardNumber and ${response.bodyAsText().json}")
        return when (response.status) {
            HttpStatusCode.OK, HttpStatusCode.Created, HttpStatusCode.Accepted -> {
                response.body() as BalanceEnquiryResponse
            }

            HttpStatusCode.BadRequest -> {
                response.body() as BalanceEnquiryResponse
            }

            else -> response.body() as BalanceEnquiryResponse
        }

    }
    private suspend fun balanceEnquiryForVoucher(cardNumber: String?,cardPin:String?): BalanceEnquiryResponse {
        log.info("Enter into balance enquiry")
            val balEnquiryReq = DataMapperUtils.mapBalanceEnquiry(cardNumber, cardPin)
            val balEnqURL = prop.loyaltyServiceHost.plus(BALANCE_ENQUIRY)
            val response = client.post(balEnqURL) {
                headers {
                    append(HttpHeaders.ContentType, CONTENT_TYPE)
                }
                setBody(balEnquiryReq)
                timeout {
                    requestTimeoutMillis = REQUEST_TIME_OUT.toLong()
                }
            }
            log.info("response received from balance enquiry of cardNo $cardNumber and ${response.bodyAsText().json}")
            return when (response.status) {
                HttpStatusCode.OK, HttpStatusCode.Created, HttpStatusCode.Accepted -> {
                    response.body() as BalanceEnquiryResponse
                }

                HttpStatusCode.BadRequest -> {
                    response.body() as BalanceEnquiryResponse
                }

                else -> response.body() as BalanceEnquiryResponse
            }
    }

    private suspend fun reloadAPIImp(reloadRequest: LoyaltyReloadRequest):ReloadBalResponse?{
        val reloadGiftCardURL = prop.loyaltyServiceHost.plus(RELOAD_GIFT_CARD_URL)
        val response = client.post(reloadGiftCardURL) {
            log.info("url $reloadGiftCardURL and reload gift card ${reloadRequest.json}")
            headers {
                append(HttpHeaders.ContentType, CONTENT_TYPE)
            }
            setBody(reloadRequest)
            timeout {
                requestTimeoutMillis = REQUEST_TIME_OUT.toLong()
            }
        }
        log.info("response received from reload is ${response.bodyAsText()}")
        val reloadResponse = response.body() as ReloadBalResponse
        return if (response.status == HttpStatusCode.OK) {
            return if (reloadResponse.responseCode == 0) {
                reloadResponse
            } else   null
        } else {
            null
        }
    }

    private suspend fun reloadBalance(reloadRequest: LoyaltyReloadRequest, order: Order, paymentTransactionId: String): Any {
        val reloadBalResponse = reloadAPIImp(reloadRequest)
        if(reloadBalResponse != null) {
            order.orderLineItems[0].giftCard?.giftCardDetails?.forEach {
                it.amount = reloadBalResponse.Cards?.get(0)?.balance
                it.validity = reloadBalResponse.Cards?.get(0)?.expiryDate
            }

            val latestOrder = transactionStatus(order)
            orderRepository.findOneAndUpdateOrder(latestOrder.orderId, latestOrder)
            return DataMapperUtils.qcConfirmOrderSuccessMappingForReload(latestOrder, paymentTransactionId)
        }else{
            order.orderStatus = PENDING
            orderRepository.findOneAndUpdateOrder(order.orderId, order)
            throw HttpResponseException("Reload balance failed", HttpStatusCode.NotFound)
        }

    }

    private suspend fun enrollMemberAndFetchMemberProfile(orderId: String, paymentTransactionId: String): Any? {
        //Getting order
        val order = orderRepository.findOrderByOrderId(orderId)

        val paymentInfo = order.paymentDetails?.transaction_1
        if(!order.paymentStatus.equals(CHARGED, ignoreCase = true)) {
            throw HttpResponseException("payment not done", HttpStatusCode.BadRequest)
        }
            val bankUrlValues = orderRepository.bankUrlValues(BANK_URL_ID)
            // Define the desired date format
            val (date, memberShipPlanStartDate) = memberShipPlanStartDate()

            var memberShipPlanName = ""
            var memberShipPlanCode = ""
            var gravityVoucherCode = ""
            if(order.orderLineItems.first().loyalty?.isBankUrl == true){
                gravityVoucherCode =  decrypt(order.orderLineItems.first().loyalty?.gravityVoucherCode.toString())
                bankUrlValues?.bankUrlValues?.forEach {
                    if (it.bankUrlName == order.orderLineItems.first().loyalty?.shareHolderDetails?.bankName) {
                        memberShipPlanCode = it.membershipPlanCode.toString()
                        memberShipPlanName = it.membershipPlanName.toString()
                    }
                }
            }else {
                memberShipPlanName =
                    "$EPICURE ${order.orderLineItems[0].loyalty?.memberCardDetails?.extra_data?.epicure_type}"
                memberShipPlanCode = "${order.orderLineItems[0].loyalty?.memberCardDetails?.extra_data?.epicure_type}"
            }
           if(order.orderLineItems.first().loyalty?.epicureFiestaOfferName.equals(EPICURE_FIESTA, ignoreCase = true)){
               gravityVoucherCode = order.orderLineItems.first().loyalty?.epicureFiestaOfferCode.toString()
           }
            val paymentReference = paymentReferenceNumber(paymentInfo)
            val paymentStatus = paymentStatusMapping(paymentInfo)

            val memberShipPlanBody = MemberShipPlanRequest(
                order.orderLineItems.first().loyalty?.membershipDetails?.memberId!!,
                memberShipPlanName.uppercase(),
                order.orderLineItems.first().loyalty?.memberShipPurchaseType?.uppercase().toString(),
                memberShipPlanCode.uppercase(),
                ACTIVE,
                memberShipPlanStartDate,
                "${LocalDateTime.now().plusYears(1)}",
                "${paymentInfo?.get(0)?.transactionId}",
                TAJ,
                order.basePrice,
                order.paymentStatus,
                OTHERS,
                OTHERS,
                gravityVoucherCode,
                order.refundAmount,
                order.taxAmount,
                "${memberShipPlanCode.uppercase()}${order.orderLineItems[0].loyalty?.membershipDetails?.memberId!!}${date}",
                "${order.gradTotal}",
                memberShipPlanStartDate,
                PAID_ONLINE,
                paymentStatus,
                "",
                paymentReference.toString(),
                WEB,
                IHCL,
                "${order.orderLineItems[0].loyalty?.memberCardDetails?.extra_data?.epicure_type?.uppercase()}"
            )
            log.info("Member Ship plan request body..${memberShipPlanBody.json}")
            try {
                val memberShipPlanUrl = prop.loyaltyServiceHost.plus(MEMBER_SHIP_PLAN)
                val response = client.post(memberShipPlanUrl) {
                    timeout {
                        requestTimeoutMillis = REQUEST_TIME_OUT.toLong()
                    }
                    setBody(memberShipPlanBody)
                    contentType(ContentType.Application.Json)
                }
                log.info("response received from Member Ship plan api is ${response.bodyAsText()} status code is ${response.status}")
                if (response.status == HttpStatusCode.Created) {
                    val primaryCardRequest = EpicurePrimaryCard(
                        memberShipPlanBody.memberId,
                        memberShipPlanBody.membershipPlanId,
                        memberShipPlanBody.membershipPlanStartDate,
                        memberShipPlanBody.membershipPlanEndDate,
                        memberShipPlanBody.membershipPlanStatus,
                        EPICURE_CARD_TYPE,
                        "${order.orderLineItems[0].loyalty?.membershipDetails?.user?.first_name} ${order.orderLineItems[0].loyalty?.membershipDetails?.user?.last_name}",
                        memberShipPlanBody.membershipPlanCode
                    )
                    log.info("Primary card request received..$primaryCardRequest")

                    try {
                        val primaryCardResponse = client.post(prop.loyaltyServiceHost.plus(LOYALTY_PRIMARY_CARD_URL)) {
                            contentType(ContentType.Application.Json)
                            setBody(primaryCardRequest)
                        }
                        log.info("Primary card for the member id ${memberShipPlanBody.memberId} is created $primaryCardResponse")
                    } catch (e: Exception) {
                        val errorMessage = "Error while creating Epicure Primary Card. Exception: ${e.message}"
                        log.error(errorMessage)
                        throw PrimaryCardCreationException(errorMessage)
                    }
                    val latestOrder = transactionStatus(order)
                    orderRepository.findOneAndUpdateOrder(orderId, latestOrder)
                    if (latestOrder.orderLineItems[0].loyalty?.membershipDetails?.addOnCardDetails?.obtainAddOnCard == true) {
                        val addOnCardsDate = SimpleDateFormat(DATE_MONTH_FORMAT)
                        val addOnCardSortedDate = SimpleDateFormat(DATE_FORMAT)
                        val inputDate =
                            addOnCardsDate.parse(latestOrder.orderLineItems[0].loyalty?.membershipDetails?.addOnCardDetails?.dateOfBirth)
                        val formattedDate = addOnCardSortedDate.format(inputDate)
                        val addOnCardBody = AddOnCardRequest(
                            latestOrder.orderLineItems[0].loyalty?.membershipDetails?.memberId!!,
                            memberShipPlanBody.membershipPlanId,
                            memberShipPlanStartDate,
                            "${LocalDateTime.now().plusYears(1)}",
                            CARD_STATUS,
                            "${latestOrder.orderLineItems[0].loyalty?.membershipDetails?.addOnCardDetails?.firstName} ${
                                latestOrder.orderLineItems[0].loyalty?.membershipDetails?.addOnCardDetails?.lastName
                            }",
                            RELATIONSHIP_TYPE,
                            "${order.orderLineItems[0].loyalty?.membershipDetails?.addOnCardDetails?.firstName} ${
                                order.orderLineItems[0].loyalty?.membershipDetails?.addOnCardDetails?.lastName
                            }",
                            order.orderLineItems[0].loyalty?.membershipDetails?.user?.gender,
                            formattedDate,
                            latestOrder.orderLineItems[0].loyalty?.membershipDetails?.addOnCardDetails?.email,
                            latestOrder.orderLineItems[0].loyalty?.memberCardDetails?.extra_data?.epicure_type,
                            latestOrder.orderLineItems[0].loyalty?.membershipDetails?.addOnCardDetails?.mobile,
                            null,
                            CARD_TYPE,
                            null,
                            FULFILMENT_STATUS
                        )
                        log.info("request body of addon-card api is ${addOnCardBody.json}")
                        addAddonCardAndUpdateOrder(latestOrder, addOnCardBody)
                        return qcConfirmOrderSuccessMappingForLoyalty(order, paymentTransactionId)
                    } else {
                        return qcConfirmOrderSuccessMappingForLoyalty(latestOrder, paymentTransactionId)
                    }

                } else if (response.status == HttpStatusCode.BadRequest) {
                    return response.body() as MemberShipBadRequestError
                }
            } catch (e: Exception) {
                log.error("Exception occurred while calling Member Ship plan api is ${e.message} due to ${e.cause}")
                throw Exception(e.message)
            }
        return null
    }

    private suspend fun addAddonCardAndUpdateOrder(order: Order, addOnCardRequest: AddOnCardRequest){
        try {
            val memberAddOnCardUrl = prop.loyaltyServiceHost.plus(MEMBER_ADD_ON_CARD)
            val response = client.post(memberAddOnCardUrl) {
                setBody(addOnCardRequest)
                contentType(ContentType.Application.Json)
            }
            log.info("response body of addon-card api is ${response.bodyAsText()}")
            val addOnCardRes = response.body<AddOnCardResponse>()
            orderRepository.updateAddOnCardDetails(addOnCardRes, order.orderId)
        } catch (e: Exception) {
            log.error("Exception occurred while calling api ${e.message} due to ${e.cause}")
            throw Exception(e.message)
        }
    }

    private fun memberShipPlanStartDate(): Pair<String, String>{
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

        // Get the current date and time
        val currentDate = Date()

        // Format the current date and time to the desired format
        val formattedDate = dateFormat.format(currentDate)
        log.info("formattedDate $formattedDate")
        val date = convertToDesiredFormat(formattedDate!!)
        val inputFormatter = DateTimeFormatter.ofPattern(DATE_MONTH_FORMAT)
        val outputFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT)
        val inputDate = LocalDate.parse(formattedDate.substring(0, 10), inputFormatter)
        return date to inputDate.format(outputFormatter)
    }

    private fun paymentReferenceNumber(paymentDetails:List<PaymentDetail>?): StringBuilder{
        var jusPayReference = ""
        var neuCoinsReference = ""
        var jusPayAmount = 0.0
        var neuCoinsAmount = 0.0
        val paymentReference = StringBuilder()
        if(paymentDetails != null) {
            if (paymentDetails.size > 1) {
                paymentDetails.forEach {
                    if (it.paymentType.equals(JUS_PAY, ignoreCase = true)) {
                        jusPayReference = it.ccAvenueTxnId.toString()
                        jusPayAmount = it.txnNetAmount!!
                    } else if (it.paymentType.equals(TATA_NEU, ignoreCase = true)) {
                        neuCoinsReference = it.redemptionId.toString()
                        neuCoinsAmount = it.txnNetAmount!!
                    }
                }
                paymentReference.append("$jusPayReference - INR $jusPayAmount | $neuCoinsReference - $neuCoinsAmount NeuCoins")
            } else {
                if (paymentDetails.first().paymentType.equals(JUS_PAY, ignoreCase = true)) {
                    jusPayReference = paymentDetails.first().ccAvenueTxnId.toString()
                    jusPayAmount = paymentDetails.first().txnNetAmount!!
                    paymentReference.append("$jusPayReference - INR $jusPayAmount")
                } else {
                    neuCoinsReference = paymentDetails.first().redemptionId.toString()
                    neuCoinsAmount = paymentDetails.first().txnNetAmount!!
                    paymentReference.append("$neuCoinsReference - $neuCoinsAmount NeuCoins")
                }
            }
        }
        return paymentReference
    }

    private suspend fun createLoyaltyOrder(loyaltyCartResponse: LoyaltyCartResponse, loyaltyRequest: LoyaltyCreateOrderRequest): Any? {
        // MemberLookup validate API...
        val epicureType = loyaltyCartResponse.items.epicureDetails.epicureType.uppercase()

        val lookupValidationUrl = prop.loyaltyServiceHost.plus(MEMBER_LOOKUP_VALIDATION)

        val validationResponse = client.get("$lookupValidationUrl"+"email=${loyaltyRequest.user?.email}&mobile=${loyaltyRequest.mobile}") {
            timeout {
                requestTimeoutMillis = REQUEST_TIME_OUT.toLong()
            }
        }
        val validateLookUp = validationResponse.body<List<ValidationLookupDTO>>()
        log.info("Response from ValidationApi..$validateLookUp and response is ${validateLookUp.isEmpty()}")
        if (validateLookUp.isEmpty()) {
            return memberEnrollment(loyaltyRequest, epicureType)
        } else {
            when (validationResponse.status) {
                HttpStatusCode.OK -> {
                    val memberId = validationResponse.body<List<MemberLookupDTO>>()
                    return memberId.first().member_id
                }
                HttpStatusCode.BadRequest -> {
                    return (validationResponse.body<MemberErrorDTO>()).error?.data?.memberId
                }
                HttpStatusCode.Conflict -> {
                    throw HttpResponseException(USER_EXISTS, HttpStatusCode.BadRequest)
                }
            }
        }
        return null
    }

    private suspend fun memberEnrollment(loyaltyRequest: LoyaltyCreateOrderRequest, epicureType: String): Any? {
        val inputDate = loyaltyRequest.dateOfBirth
        val inputFormat = SimpleDateFormat(DATE_MONTH_FORMAT)
        val outputFormat = SimpleDateFormat(DATE_FORMAT)
        val date = inputFormat.parse(inputDate)
        val formattedDate = outputFormat.format(date)
        log.info("Formatted Date..$formattedDate")
        val body = MemberEnrollmentRequest(
            loyaltyRequest.address, "",
            UserDetails(loyaltyRequest.user?.email, loyaltyRequest.user?.firstName, loyaltyRequest.user?.lastName),
            EPICURE_COUNTRY_ID, formattedDate,
            "",
            2,
            WEB, 1,
            ExtraData(
                loyaltyRequest.extraData?.countryCode,
                loyaltyRequest.extraData?.city,
                epicureType,
                loyaltyRequest.extraData?.state
            ),
            loyaltyRequest.gender,
            loyaltyRequest.mobile,
            loyaltyRequest.salutation,
            postalCode = loyaltyRequest.pinCode
        )
        log.info("Member enrolment request body ${body.json}")
        val memberEnrollmentUrl = prop.loyaltyServiceHost.plus(MEMBER_ENROLLMENT)
        val response = client.post(memberEnrollmentUrl) {
            contentType(ContentType.Application.Json)
            setBody(body)
        }
        return when (response.status) {
            HttpStatusCode.Created -> {
                val memberEnrollment = response.body() as MemberEnrollmentResponse
                memberEnrollment.member_id.toString()
            }

            HttpStatusCode.BadRequest -> {
                val badRequestResponse = response.body() as LoyaltyErrorResponses
                log.info("Bad request response from API $badRequestResponse")
                validateErrorResponse(badRequestResponse)
            }

            HttpStatusCode.Conflict -> {
                throw HttpResponseException(USER_EXISTS, HttpStatusCode.BadRequest)
            }

            else -> null
        }
    }

    private suspend fun confirmOrderResponse(
        response: HttpResponse,
        order: Order,
        jwtToken: String?,
         isCronJob: Boolean
    ): Any? {
        log.info("jusPay response status:: ${response.status}")
         if (response.status == HttpStatusCode.BadRequest || response.status == HttpStatusCode.NotFound) {
             return pendingOrderUpdate(order, response, isCronJob)
        } else{
            log.info(" orderId: ${order.orderId} JusPay Order status success ${response.body() as OrderStatusResponse}")
            val orderStatus:OrderStatusResponse? = response.body()
             when (orderStatus?.status) {
                 PaymentStatus.CHARGED.toString() -> {
                   return  jusPaySuccessUpdate(order, orderStatus, jwtToken, isCronJob)
                 }
                 PaymentStatus.AUTHORIZATION_FAILED.toString() -> {
                     return failedOrderUpdate(order,orderStatus,isCronJob)
                 }
                 else -> {
                     return pendingOrderUpdate(order, response, isCronJob)
                 }
             }
        }
    }

    private suspend fun jusPaySuccessUpdate(order: Order,orderStatus: OrderStatusResponse?, jwtToken: String?, isCronJob: Boolean ): Any?{
       val orderId = order.orderId
        var updatedOrder = updateJusPayDetails(order, orderStatus)

         when (updatedOrder.orderType) {
            OrderType.RELOAD_BALANCE -> {
                log.info("orderType is ${OrderType.RELOAD_BALANCE}")
                updatedOrder.paymentStatus = PaymentStatus.CHARGED.toString()
                orderRepository.findOneAndUpdateOrder(orderId, updatedOrder)
                return reloadBalance(DataMapperUtils.mapReloadGiftCard(updatedOrder), updatedOrder, paymentTransactionId(updatedOrder.orderType, updatedOrder.paymentDetails?.transaction_1?.toList()))
            }

            OrderType.GIFT_CARD_PURCHASE -> {
                log.info("orderType is ${OrderType.GIFT_CARD_PURCHASE} using Tenders")
                updatedOrder = giftCardRedeemImp(updatedOrder, jwtToken, isCronJob)
                updatedOrder = updatePaymentStatus(updatedOrder)
                orderRepository.findOneAndUpdateOrder(orderId, updatedOrder)
                val paymentInfo = updatedOrder.paymentDetails?.transaction_1
                val paymentReference = paymentReferenceNumber(paymentInfo).toString()
                val paymentStatus = paymentStatusMapping(paymentInfo)
                emptyCart(updatedOrder.customerHash, GIFT_CARD_PURCHASE)
                return buyGiftCard(DataMapperUtils.qcSuccessOrderStatus(updatedOrder, paymentReference, paymentStatus), updatedOrder,
                    paymentTransactionId(updatedOrder.orderType, updatedOrder.paymentDetails?.transaction_1?.toList()))

            }
            OrderType.MEMBERSHIP_PURCHASE -> {
                log.info("orderType is ${OrderType.MEMBERSHIP_PURCHASE} using Tenders")
                updatedOrder = giftCardRedeemImp(updatedOrder, jwtToken, isCronJob)
                updatedOrder = updatePaymentStatus(updatedOrder)
                orderRepository.findOneAndUpdateOrder(orderId, updatedOrder)
                emptyCart(updatedOrder.customerHash, MEMBERSHIP_PURCHASE)
                voucherRedemption(updatedOrder)
                return enrollMemberAndFetchMemberProfile(orderId, paymentTransactionId(order.orderType, updatedOrder.paymentDetails?.transaction_1?.toList()))
            }
            else -> {
                return processHotelBookingConfirmation(order, jwtToken, isCronJob)
                 }
        }
    }

    private suspend fun failedOrderUpdate(order: Order, orderStatus: OrderStatusResponse?, isCronJob: Boolean): Any{
        val updatedOrder: Order?
        if(order.orderType == OrderType.HOTEL_BOOKING || order.orderType == OrderType.HOLIDAYS) {
            updatedOrder = bookingFailedUpdate(order, orderStatus, isCronJob)
            return DataMapperUtils.mapConfirmHotelBookingResponse(updatedOrder, paymentTransactionId(updatedOrder.orderType, updatedOrder.paymentDetails?.transaction_1?.toList()))
        }else {
            updatedOrder = jusPayFailedUpdate(order, PaymentStatus.FAILED.toString(), PaymentStatus.FAILED.toString(), ORDER_STATUS_FAILED)
            throw  ResponseException(orderStatus?.status,updatedOrder, HttpStatusCode.BadRequest)
        }
    }

    private suspend fun pendingOrderUpdate(order: Order, response: HttpResponse, isCronJob: Boolean): Any{
        if(order.orderType == OrderType.HOTEL_BOOKING || order.orderType == OrderType.HOLIDAYS){
           val updatedOrder = jusPayFailedUpdate(order, PaymentStatus.PENDING.toString(), PaymentStatus.PENDING.toString(), INITIATED)
            updatedOrder.orderLineItems.first().hotel?.status = PENDING_CONFIRMATION
            orderRepository.findOneAndUpdateOrder(order.orderId, updatedOrder)
            if(!isCronJob) {
                emptyCart(order.customerHash, HOTEL_BOOKING)
            }
            deleteCacheCollection(updatedOrder)
            return DataMapperUtils.mapConfirmHotelBookingResponse(updatedOrder,  null)

        }else {
            val updatedOrder = jusPayFailedUpdate(order, PaymentStatus.PENDING.toString(), PaymentStatus.PENDING.toString(), INITIATED)
            throw HttpResponseException("Payment is in Pending", HttpStatusCode.BadRequest)
        }
    }
    private suspend fun jusPayFailedUpdate(order: Order, txnStatus: String, paymentStatus: String, orderStatus: String): Order{
        order.paymentDetails?.transaction_1?.withIndex()?.find { (_, payment) ->
            payment.paymentType == JUS_PAY
        }?.let {
            it.value.txnStatus = txnStatus
        }
        order.paymentStatus = paymentStatus
        order.orderStatus = orderStatus
        orderRepository.findOneAndUpdateOrder(order.orderId, order)
        return order
    }
    private suspend fun bookingFailedUpdate(order: Order, orderStatus: OrderStatusResponse?, isCronJob: Boolean):Order{
        order.paymentDetails?.transaction_1?.withIndex()?.find { (_, payment) ->
            payment.paymentType == JUS_PAY
        }?.let {
            it.value.txnStatus = PaymentStatus.FAILED.toString()
            it.value.ccAvenueTxnId = orderStatus?.payment_gateway_response?.epg_txn_id
        }
        order.paymentStatus = PaymentStatus.FAILED.toString()
        order.orderLineItems.first().hotel?.rooms?.forEach {
            it.status = FAILED
        }
        order.orderLineItems.first().hotel?.status = FAILED
        order.orderStatus = ORDER_STATUS_FAILED
        orderRepository.findOneAndUpdateOrder(order.orderId, order)
        deleteCacheCollection(order)
        if(!isCronJob) {
            emptyCart(order.customerHash, HOTEL_BOOKING)
        }
        return order
    }
    private suspend fun calculateApportionedValues(updatedOrder: Order): Order{
            val orderData = DataMapperUtils.orderData(updatedOrder)
            log.info("orderId: ${updatedOrder.orderId} apportioned order data request::${orderData.json}")
            val orders = ApportionOrder()
            apportionOrderService.getApportionedOrder(orders, orderData)
            apportionOrderService.createRooms(orders)
            val paidAmountList = arrayListOf<Double>()
            var amountPaid = 0.0
            var paidAmount = 0.0
            val roomSize = updatedOrder.orderLineItems.first().hotel?.rooms?.size
            log.info("apportioned order started ${orders.json}")

            orders.rooms.forEach { it ->
                it.apportionValues.forEach {
                    if(it.txnStatus.equals(CHARGED, ignoreCase = true)) {
                        paidAmount += it.txnNetAmount!!
                    }
                }
                paidAmountList.add(paidAmount)
                paidAmount = 0.0
            }
            orders.paymentDetails.forEach {
                if(it.txnStatus.equals(CHARGED, ignoreCase = true)) {
                    amountPaid += it.txnNetAmount!!
                }
            }
            for(i in 0 until roomSize!!){
                updatedOrder.orderLineItems.first().hotel?.rooms?.get(i)?.paidAmount =
                    paidAmountList[i]
            }
            updatedOrder.orderLineItems.first().hotel?.amountPaid = paidAmountList.sum().toBigDecimal().setScale(
                0,
                RoundingMode.HALF_UP
            ).toDouble()
            updatedOrder.orderLineItems.first().hotel?.isDepositPaid = updatedOrder.orderLineItems.first().hotel?.isDepositAmount
            val totalDepositAmount = updatedOrder.orderLineItems.first().hotel?.totalDepositAmount
            updatedOrder.orderLineItems.first().hotel?.isDepositPaid = updatedOrder.orderLineItems.first().hotel?.isDepositAmount
            if(updatedOrder.orderLineItems.first().hotel?.isDepositAmount == true) {
                if ((totalDepositAmount!! > 0.0) && ((updatedOrder.orderLineItems.first().hotel?.grandTotal == totalDepositAmount) ||
                            (amountPaid > totalDepositAmount))
                ) {
                    updatedOrder.orderLineItems.first().hotel?.isDepositFull = true
                    updatedOrder.orderLineItems.first().hotel?.isDepositPaid = false
                }else{
                    updatedOrder.orderLineItems.first().hotel?.isDepositFull = false
                    updatedOrder.orderLineItems.first().hotel?.isDepositPaid = true
                }
            }

        return updatedOrder
    }

    private suspend fun updateJusPayDetails(order: Order, orderStatus:OrderStatusResponse?): Order{
        val paymentDetails = order.paymentDetails?.transaction_1
        paymentDetails?.forEach {paymentDetail ->
            if(paymentDetail.paymentType== JUS_PAY){
                paymentDetail.paymentMethod = orderStatus?.payment_method
                paymentDetail.txnGateway = orderStatus?.gateway_id
                paymentDetail.txnId = orderStatus?.txn_id
                paymentDetail.txnUUID = orderStatus?.txn_uuid
                paymentDetail.txnStatus = orderStatus?.status
                paymentDetail.ccAvenueTxnId = orderStatus?.payment_gateway_response?.epg_txn_id
                paymentDetail.transactionDateAndTime = orderStatus?.payment_gateway_response?.gateway_response?.trans_date

                /*Masking card number except first and last 2 characters*/

                var maskFirstTwo = orderStatus?.card?.card_isin.toString()
                var maskLastTwo = orderStatus?.card?.last_four_digits.toString()
                maskFirstTwo = maskFirstTwo.take(2) + MASK.repeat(maskFirstTwo.length - 2)
                maskLastTwo = MASK.repeat(maskLastTwo.length - 4) + maskLastTwo.takeLast(4)

                if (orderStatus?.txn_detail?.txn_flow_type == CARD_TRANSACTION) {
                    paymentDetail.cardNo = cardFormat(
                        maskFirstTwo.plus(HASH_FORMAT).plus(maskLastTwo)
                    )
                    paymentDetail.paymentMethodType =
                        orderStatus.card?.card_type.plus(" ").plus(orderStatus.payment_method_type)
                    paymentDetail.nameOnCard = orderStatus.card?.name_on_card
                    paymentDetail.expiryDate = orderStatus.card?.expiry_month.plus(EXPIRY_DATE).plus(orderStatus.card?.expiry_year)

                } else if (orderStatus?.txn_detail?.txn_flow_type == NET_BANKING) {
                    paymentDetail.cardNo = "0"
                    paymentDetail.paymentMethodType = orderStatus.payment_method_type
                    paymentDetail.nameOnCard = ""
                    paymentDetail.expiryDate = ""

                }
            }
            orderRepository.findOneAndUpdateOrder(order.orderId, order)
            log.info("order payment details ${order.paymentDetails?.transaction_1}")
        }
        return order
    }

    private fun transactionStatus(order: Order): Order{
        val paymentDetails = order.paymentDetails?.transaction_1
        log.info("orderStatus ${order.orderStatus} and paymentMethod ${order.paymentMethod}")
        if(order.orderType == OrderType.HOTEL_BOOKING || order.orderType == OrderType.HOLIDAYS) {
            if(order.paymentStatus.equals(FAILED, ignoreCase = true)){
                order.orderLineItems.first().hotel?.rooms?.forEach {
                    it.status = ROOM_FAILED_STATUS
                }
                order.orderLineItems.first().hotel?.status = FAILED
            }else {
                if (order.orderLineItems.first().hotel?.rooms?.size!! > 1) {
                    if ((order.orderLineItems.first().hotel?.rooms?.any { p ->
                            p.status.equals(
                                PENDING, ignoreCase = true
                            )
                        } == true)) {

                        if ((order.orderLineItems.first().hotel?.rooms?.any { p ->
                                p.status.equals(CONFIRMED, ignoreCase = true)
                            } == true)) {

                            order.orderLineItems.first().hotel?.status = PARTIALLY_CONFIRMATION
                        } else {
                            order.orderLineItems.first().hotel?.status = PENDING_CONFIRMATION
                        }
                    } else if (order.orderLineItems.first().hotel?.rooms?.any { p ->
                            p.status.equals(FAILED, ignoreCase = true)
                        } == true) {

                        if (order.orderLineItems.first().hotel?.rooms?.any { p ->
                                p.status.equals(CONFIRMED, ignoreCase = true)
                            } == true) {
                            order.orderLineItems.first().hotel?.status = STATUS_CONFIRMED
                        } else {
                            order.orderLineItems.first().hotel?.status = PENDING_CONFIRMATION
                        }
                    } else {
                        order.orderLineItems.first().hotel?.status = STATUS_CONFIRMED
                    }
                } else {
                    if ((order.orderLineItems.first().hotel?.rooms?.any { p ->
                            p.status.equals(
                                PENDING, ignoreCase = true
                            )
                        } == true)) {
                        order.orderLineItems.first().hotel?.status = PENDING_CONFIRMATION
                    } else if ((order.orderLineItems.first().hotel?.rooms?.any { p ->
                            p.status.equals(
                                FAILED, ignoreCase = true
                            )
                        } == true)) {
                        order.orderLineItems.first().hotel?.status = FAILED
                    } else {
                        order.orderLineItems.first().hotel?.status = STATUS_CONFIRMED
                    }
                }
            }
            log.info("latest order ${order.orderLineItems.first().hotel?.status}")
        }
        val paymentStatus = when {
            order.paymentStatus == CHARGED && order.paymentMethod == PAY_AT_HOTEL -> {
                PAY_AT_HOTEL
            }
            order.orderType == OrderType.MEMBERSHIP_PURCHASE && order.orderLineItems.first().loyalty?.isBankUrl == true -> {
                ORDER_STATUS_SUCCESS
            }
            (order.paymentStatus == CHARGED || order.paymentStatus == PARTIALLY_CHARGED) && order.paymentMethod == PAY_ONLINE -> {
                paymentStatusMapping(paymentDetails)
            }
            else -> {
                if(order.paymentStatus == PENDING && order.orderType == OrderType.HOTEL_BOOKING){
                    PAYMENT_PENDING
                }else if(order.paymentStatus == PENDING){
                    PAYMENT_PENDING
                }else{
                    FAILED
                }
            }
        }
        log.info(" updated the transaction status$paymentStatus")
        order.transactionStatus = paymentStatus
        log.info("hotel status ${order.orderLineItems.first().hotel?.status} and transaction status ${order.transactionStatus}")
        return orderStatusUpdate(order)
    }

    private fun paymentStatusMapping(paymentDetails: List<PaymentDetail>?): String{
            var paymentStatus = ""
            val paymentTypeList = paymentDetails?.map { it.paymentType.toString() }
            val paymentStatusList = paymentDetails?.map { it.txnStatus.toString() }

            val paymentStatusMap = paymentTypeList?.groupingBy { it }?.eachCount()

            if (paymentStatusMap != null) {
                if (paymentStatusMap.size > 1) {
                    paymentStatus = if (paymentStatusList?.any { it.equals(PENDING, ignoreCase = true) || it.equals(FAILED, ignoreCase = true) } == true) {
                        PAID_PARTIALLY
                    } else {
                        PAID_BY_MULTIPLE_TENDERS
                    }
                } else {
                    paymentStatusMap.entries.forEach { (key, value) ->
                        paymentStatus = when {
                            key == GIFT_CARD && value > 1 -> PAID_BY_GIFT_CARDS
                            key == GIFT_CARD -> PAID_BY_GIFT_CARD
                            key == TATA_NEU -> PAID_BY_NEU_COINS
                            else -> PAID_BY + SEPERATOR + paymentDetails.first().paymentMethodType.toString().uppercase()
                        }
                    }
                }
            }
            return paymentStatus

    }

    private fun orderStatusUpdate(order: Order): Order{
        if(order.orderType == OrderType.HOLIDAYS || order.orderType == OrderType.HOTEL_BOOKING){
            when {
                order.orderLineItems.first().hotel?.status.equals(PENDING, ignoreCase = true) || order.orderLineItems.first().hotel?.status.equals(PARTIALLY_CONFIRMATION, ignoreCase = true) || order.orderLineItems.first().hotel?.status.equals(PENDING_CONFIRMATION, ignoreCase = true) -> {
                    order.orderStatus = PENDING
                }
                order.orderLineItems.first().hotel?.status.equals(FAILED, ignoreCase = true) -> {
                    order.orderStatus = FAILED
                }
                order.orderLineItems.first().hotel?.status.equals(CONFIRMED, ignoreCase = true) -> {
                    order.orderStatus = ORDER_STATUS_SUCCESS
                }
            }
        }else {
            when (order.paymentStatus) {
                PENDING -> {
                    order.orderStatus = PENDING
                }
                FAILED -> {
                    order.orderStatus = ORDER_STATUS_FAILED
                }
                else -> {
                    order.orderStatus = ORDER_STATUS_SUCCESS
                }
            }
        }
        return order
    }
    private fun cardFormat(input: String): String {
        val regex = "(.{4})(?=.)".toRegex()
        return input.replace(regex, "$1-")
    }

    private suspend fun updateBookingImp(order: Order, status: String): Order {
        val paymentStringList = paymentString(order)
        if(order.paymentStatus.equals(CHARGED, ignoreCase = true)) {
            val confirmation = bookingConfirmation(order, status, paymentStringList)
            order.orderLineItems[0].hotel?.rooms?.forEach { room ->
                for ((key ) in confirmation){
                    if(key == room.roomNumber){
                        if(confirmation[key] == room.confirmationId){
                            room.status = CONFIRMED
                        }else if(confirmation[key].equals(FAILED, ignoreCase = true)){
                            room.status = FAILED
                        }
                    }
                }
            }
            val adultCount = getCustomerCount(order.orderLineItems[0].hotel?.rooms!!,order.orderLineItems[0].hotel?.roomCount,Constants.CUSTOMER_ADULT)
            order.orderLineItems[0].hotel?.adultCount = adultCount
            val childCount = getCustomerCount(order.orderLineItems[0].hotel?.rooms!!,order.orderLineItems[0].hotel?.roomCount,Constants.CUSTOMER_CHILD)
            order.orderLineItems[0].hotel?.childrens = childCount
        }
        return order
    }
    private suspend fun bookingConfirmation(order: Order, status: String, paymentStringList: ArrayList<String>): Map<Int?, String>{
        val rooms = order.orderLineItems[0].hotel!!.rooms!!
        val roomNumberList = ArrayList<Int?>()
        val confirmationIdList = ArrayList<String>()
        val expiryDate = order.paymentDetails?.transaction_1?.first()?.expiryDate
        val cardNumber = order.paymentDetails?.transaction_1?.first()?.cardNo
        val nameOnCard = order.paymentDetails?.transaction_1?.first()?.nameOnCard
        val isConfirmBooking = (Constants.GAGCO.split(",").toList().any { t -> order.orderLineItems.first().hotel?.rooms!!.any { it.roomCode.toString()== t } })
        val isInternationalHotel = (!order.orderLineItems.first().hotel?.country.equals(COUNTRY, ignoreCase = true) && (!expiryDate.isNullOrEmpty() || !cardNumber.isNullOrEmpty() || !nameOnCard.isNullOrEmpty()))
        if(order.orderLineItems.first().hotel?.country.equals(COUNTRY, ignoreCase = true) ||
            isConfirmBooking  || order.orderLineItems.first().hotel?.isSeb == true || isInternationalHotel){
            for (room in 0 until rooms.size) {
                if (order.orderLineItems[0].hotel!!.rooms?.get(room)?.status == PENDING) {
                    val updateBookingRequest =
                        DataMapperUtils.mapUpdateBookingRequest(
                            order,
                            room,
                            status,
                            paymentStringList[room]
                        )
                    roomNumberList.add(order.orderLineItems.first().hotel?.rooms?.get(room)?.roomNumber)
                    confirmationIdList.add(hudiniUpdateBookingImp(updateBookingRequest))
                }
            }
        }else if(!order.orderLineItems.first().hotel?.country.equals(COUNTRY, ignoreCase = true) && (expiryDate.isNullOrEmpty() || cardNumber.isNullOrEmpty() || nameOnCard.isNullOrEmpty())){
            confirmationIdList.add(PENDING)
        }
        return roomNumberList.zip(confirmationIdList).toMap()
    }

    private suspend fun paymentString(order: Order): ArrayList<String>{
        val paymentStringList = ArrayList<String>()
        val roomSize = order.orderLineItems[0].hotel?.rooms?.size!!
        if(order.paymentDetails?.transaction_1?.any { p -> (p.txnStatus.equals(PaymentStatus.CHARGED.toString())) } == true){
            val response: ApportionOrder = order.orderId.let { it1 -> apportionOrderService.getUpdatedOrder(it1) }
            processPaymentString(order, response, paymentStringList)

        }else{
            for(room in 0 until roomSize){
                if (order.orderLineItems[0].hotel?.rooms?.get(room)?.roomCode.toString()
                        .equals("GTA", ignoreCase = true)
                ) {
                    paymentStringList.add(getPaymentStringAwaiting(GTA_PAYMENT_STRING).toString())
                } else if (order.orderLineItems[0].hotel?.rooms?.get(room)?.roomCode.toString()
                        .equals("GCO", ignoreCase = true)
                ) {
                    paymentStringList.add(getPaymentStringAwaiting(GCO_PAYMENT_STRING).toString())
                } else {
                    paymentStringList.add(getPaymentStringAwaiting(PAYMENT_AWAITING).toString())
                }
            }
        }
        return paymentStringList
    }

    private fun processPaymentString(
        order: Order,
        response: ApportionOrder,
        paymentStringList: ArrayList<String>,
    ) {
        if (order.paymentMethod.equals("PAY ONLINE", ignoreCase = true)) {
            for (room in response.rooms) {
                log.info("payment String.." + getPaymentString(room, order))
                paymentStringList.add(getPaymentString(room, order).toString())
            }
        } else {
            log.info("pay at hotel.." + response.rooms.size)
            for (room in order.orderLineItems.first().hotel?.rooms!!) {
                log.info("payment String.." + getPayAtHotelPaymentString(room, order))
                paymentStringList.add(getPayAtHotelPaymentString(room, order).toString())
            }
        }
    }

    private fun getPaymentStringAwaiting(paymentString: String): StringBuilder{
        val builder = StringBuilder()
        return builder.append(paymentString)
    }

    private suspend fun hudiniUpdateBookingImp(updateBookingRequest: UpdateBookingRequest):String{
        try {
            val updateBookingURL = prop.hudiniServiceHost.plus(UPDATE_BOOKING)
            val response = client.post(updateBookingURL) {
                log.info("update booking API Request ${updateBookingRequest.json}")
                timeout {
                    requestTimeoutMillis = REQUEST_TIME_OUT.toLong()
                }
                headers {
                    append(HttpHeaders.ContentType, ContentType.Application.Json)
                }
                setBody(updateBookingRequest)
            }
            log.info("Update booking response body ${response.bodyAsText()} and status ${response.status}")
            val updateBookingResponse = response.body() as UpdateBookingResponse

            return if (response.status == HttpStatusCode.OK || response.status == HttpStatusCode.Created) {
                return if (updateBookingResponse.data.updateHotelBooking.reservations.isNullOrEmpty()) {
                    PENDING
                } else {
                    updateBookingResponse.data.updateHotelBooking.reservations.first().crsConfirmationNumber
                }
            } else {
                PENDING
            }
        }catch (e: Exception){
            log.info("update booking API failed")
            return PENDING
        }
    }

    private suspend fun emptyCart(customerHash: String, type: String) {
        val emptyCartUrl = prop.cartServiceHost.plus(EMPTY_CART_URL)
        val emptyCartResponse: HttpResponse = client.delete(emptyCartUrl) {
            timeout {
                requestTimeoutMillis = REQUEST_TIME_OUT.toLong()
            }
            headers {
                append(CUSTOMERHASH, customerHash)
                append(CATEGORY_TYPE, type)
            }
            contentType(ContentType.Application.Json)
        }
        log.info("removing items from cart $emptyCartResponse")
    }

    private suspend fun callRedeemNeuCoins(transactionNumber: String, order: Order, amount: String, jwtToken: String?): Order{
        val paymentDetails = order.paymentDetails?.transaction_1
        if (jwtToken.isNullOrEmpty()) {
            throw HttpResponseException("Invalid Token", HttpStatusCode.BadRequest)
        }
        log.info("Received transactionNumber=$transactionNumber and jwtToken=$jwtToken")
        val externalReferenceNumber = getExternalReferenceNumber(order.orderId)
        val storeId = when (order.orderType) {
            OrderType.HOTEL_BOOKING, OrderType.HOLIDAYS -> order.orderLineItems.first().hotel?.storeId.toString().substring(3)
            OrderType.GIFT_CARD_PURCHASE -> STORE_ID_GC
            else -> STORE_ID_EPICURE

        }
        try {
            val redeemNeuCoinsUrl = prop.loyaltyServiceHost.plus(LOYALTY_REDEEM_NEU_COINS)
            val body =  createRedemptionRequest( externalReferenceNumber, amount, transactionNumber,order)
            log.info("Request prepared to call redeemNeuCoin api is $body")
            val response = client.post(redeemNeuCoinsUrl) {
                headers {
                    append(NEUCOINAUTHORIZATION, jwtToken)
                    append(STORE_ID, storeId)
                }
                contentType(ContentType.Application.Json)
                setBody(body)
            }
            log.info("response received from redeemNeuCoins api is ${response.bodyAsText()}")
            val res = response.body<RedeemNeuCoinsResponse>()
            if (res.response?.status?.message == SUCCESS) {
                paymentDetails!!.forEach {
                    if (it.paymentType == TenderMode.TATA_NEU.toString()) {
                        it.txnStatus = PaymentStatus.CHARGED.toString()
                        it.userId = res.response.responses?.points?.user_id
                        it.externalId = res.response.responses?.points?.external_id
                        it.redemptionId = res.response.responses?.points?.redemption_id
                        it.pointsRedemptionsSummaryId =
                            res.response.responses?.points?.side_effects?.effect?.get(0)?.points_redemption_summary_id
                    }
                }
                log.info("updated payment status of neucoins redemption to success $order")
                log.info("NeuCoins Redeemed Successfully ${res.json}")
                deleteRedisEntry(order.orderId)
            } else {
                paymentDetails?.forEach {
                    if (it.paymentType == TenderMode.TATA_NEU.toString()) {
                        it.txnStatus = PaymentStatus.FAILED.toString()
                    }
                }
                log.info("Bad Request $res")
            }
        }catch (e: Exception){
            paymentDetails?.forEach {
                if (it.paymentType == TenderMode.TATA_NEU.toString()) {
                    it.txnStatus = PaymentStatus.FAILED.toString()
                }
            }
        }

        orderRepository.findOneAndUpdateOrder(order.orderId, order)
        return order

    }

    private fun getExternalReferenceNumber(input: String?): String {
        val parts = input?.split("_") // Split the input string based on "_"
        return parts?.lastOrNull() ?: "" // Return the last part after "_"
    }

    suspend fun getOrdersByCustomerHash(customerHash: String): List<Order> {
        return orderRepository.findOrderByCustomerHash(customerHash)
    }

    private suspend fun cancelBookingRes(
        confirmationId: String,
        cancelBookingRequest: CancelBookingRequest
    ): String {
        try {
            val cancelBookingReq = DataMapperUtils.cancelBookingReq(confirmationId, cancelBookingRequest)
            log.info("cancel booking req ${cancelBookingReq.json}")
            val cancelBooking = prop.hudiniServiceHost.plus(CANCEL_BOOKING)
            val response = client.post(cancelBooking) {
                contentType(ContentType.Application.Json)
                setBody(cancelBookingReq)
            }
            log.info("response for cancel ${response.bodyAsText()} ")
            val cancelBookingResponse = response.body() as CancelBookingResponse
            return if (response.status == HttpStatusCode.OK || response.status == HttpStatusCode.Created) {
                cancelBookingResponse.data.cancelHotelBooking.cancellationNumber.ifEmpty {
                    PENDING
                }
            } else {
                PENDING
            }

        }catch (e: Exception){
            return PENDING
        }
    }

/*
    private suspend fun cancelBookingImp(cancelBookingRequest: CancelBookingRequest): Order {
        log.info("enter into cancel booking imp")
        var cancelRoomAmount = 0.0
        val order = orderRepository.findOrderByOrderId(cancelBookingRequest.orderId!!)
        val confirmationIdList = ArrayList<String?>()
        val cancellationNumberList = ArrayList<String>()
        cancelBookingRequest.room.forEach { confirmationDetails ->
            order.orderLineItems.first().hotel?.rooms?.forEach { room ->
                if (room.roomNumber == confirmationDetails.roomNumber) {
                    val confirmationId = if(room.modifyBooking!= null){
                        room.modifyBooking.confirmationId
                    }else room.confirmationId
                    cancelRoomAmount += if(order.orderLineItems[0].hotel?.voucherRedemption?.isComplementary == true){
                        if(room.modifyBooking != null && room.modifyBooking.status == CONFIRMED){
                            room.modifyBooking.taxAmount!!
                        }else{
                            room.taxAmount!!
                        }
                    }else{
                        if(room.modifyBooking != null && room.modifyBooking.status == CONFIRMED){
                            room.modifyBooking.grandTotal
                        }else{
                            room.grandTotal
                        }
                    }
                    log.info("cancel room price $cancelRoomAmount")
                    confirmationIdList.add(confirmationId)
                    cancellationNumberList.add(cancelBookingRes(confirmationId!!, cancelBookingRequest))
                }
            }
        }
        order.orderLineItems[0].hotel?.revisedPrice = order.orderLineItems[0].hotel?.revisedPrice?.minus(cancelRoomAmount)
        order.orderLineItems[0].hotel?.grandTotal = order.orderLineItems[0].hotel?.grandTotal?.minus(cancelRoomAmount)
        val confirmation = confirmationIdList.zip(cancellationNumberList).toMap()
        order.orderLineItems[0].hotel?.rooms?.forEach { room ->
            for ((key ) in confirmation){
                if(key == room.confirmationId && confirmation[key] != PENDING) {
                    room.status = ROOM_STATUS_CANCELLED
                    room.modifyBooking?.status = ROOM_STATUS_CANCELLED
                    room.cancellationTime = getCurrentDateTime()
                    room.modifyBooking?.cancellationTime = getCurrentDateTime()
                    log.info("current time ${getCurrentDateTime()}")
                }
            }
        }
        return cancelOrderInHotelBooking(order)
    }
*/

    private suspend fun cancelOrderInHotelBooking(order: Order): Order {
        log.info("cancel order in hotel booking")
        val roomCancelList = ArrayList<Int>()
        order.orderLineItems.first().hotel?.rooms?.forEach {
            if (it.status.equals(ROOM_STATUS_CANCELLED, ignoreCase = true)) {
                roomCancelList.add(it.roomNumber)
            }
        }
        if (order.orderLineItems.first().hotel?.rooms?.size == roomCancelList.size) {
            order.orderLineItems[0].hotel?.status = BookingStatus.CANCELLED.toString()
            order.orderStatus = CANCELLED
        } else {
            order.orderLineItems.first().hotel?.status = PARTIALLY_CANCELLED
            order.orderStatus = PARTIALLY_CANCELLED
        }
        orderRepository.findOneAndUpdateOrder(order.orderId, order)
        log.info("order details ${order.json}")
        return order
    }

    fun generatingGuestUser(): String {
        return ValidatorUtils.getUUID()
    }


    fun checkCancelBooking(cancelBookingRequest: CancelBookingRequest, neuCoinToken: String?): CancelHotelResponse? {
        val cancelHotelResponse:CancelHotelResponse? = null
        return cancelHotelResponse
    }

/*
    suspend fun cancelBooking(cancelBookingRequest: CancelBookingRequest, neuCoinToken: String?,customerHash:String?): ConfirmHotelResponse {

        log.info("enter into cancel booking")

        val remarksList = arrayListOf<String>()

        log.info("request body of cancel booking is ${cancelBookingRequest.json}")

        var order = orderRepository.findOrderByOrderId(cancelBookingRequest.orderId!!)

        if (cancelBookingRequest.cancelType == "R") {
            val fetchBookingDetailsRequest = DataMapperUtils.mapFetchBookingDetailsRequest(order)
            val getBookingDetailsRes = getBookingDetailsByItinerary(fetchBookingDetailsRequest)
            if (getBookingDetailsRes?.data?.getHotelBookingDetails?.reservations != null) {
                order.orderLineItems.first().hotel?.rooms?.forEach { room ->
                    getBookingDetailsRes.data.getHotelBookingDetails.reservations.forEach { reservation ->

                        if (room.modifyBooking != null && room.modifyBooking.status.equals(CONFIRMED, ignoreCase = true) && room.modifyBooking.confirmationId == reservation?.crsConfirmationNumber) {
                            room.penaltyAmount = reservation?.bookingDues?.cancelPenalty?.amount
                            room.penaltyDeadLine = reservation?.bookingDues?.cancelPenalty?.deadline
                            room.modifyBooking.penaltyAmount = reservation?.bookingDues?.cancelPenalty?.amount
                            room.modifyBooking.penaltyDeadLine = reservation?.bookingDues?.cancelPenalty?.deadline
                        } else if (room.confirmationId == reservation?.crsConfirmationNumber) {
                            room.penaltyAmount = reservation?.bookingDues?.cancelPenalty?.amount
                            room.penaltyDeadLine = reservation?.bookingDues?.cancelPenalty?.deadline

                        }
                    }
                }

            } else {
                throw HttpResponseException(
                    "Cancellation not allowed due to penalty amount and deadline are missing",
                    HttpStatusCode.BadRequest
                )
            }

            orderRepository.findOneAndUpdateOrder(cancelBookingRequest.orderId, order)
        }
        order = orderRepository.findOrderByOrderId(cancelBookingRequest.orderId)

        var partialOrder: Order? = null

        val requestedRooms:ArrayList<Room> = arrayListOf()

        val unmodifiedRooms:ArrayList<Room> = arrayListOf()

        for(room in order.orderLineItems[0].hotel?.rooms!!){

            for(requestRoom in cancelBookingRequest.room) {

                if (requestRoom.roomNumber == room.roomNumber) {

                    requestedRooms.add(room)

                }

            }

        }

        for(room in order.orderLineItems[0].hotel?.rooms!!){

            if(!requestedRooms.contains(room)){

                unmodifiedRooms.add(room)

            }

        }

        order.orderLineItems[0].hotel?.rooms = arrayListOf()

        order.orderLineItems[0].hotel?.rooms = requestedRooms.sortedBy { room -> room.roomNumber }

        if (cancelBookingRequest.isFullCancellation) {

            log.info("full cancellation")

            if (order.paymentMethod == PAY_ONLINE) {

                log.info("neuCoin token $neuCoinToken")

                var totalCancelPayableAmount = 0.0

                var totalCancelRefundableAmount = 0.0

                var totalPenaltyAmount = 0.0

                var totalPaidAmount = 0.0

                order.orderLineItems.first().hotel?.rooms?.forEach {

                    if (cancelBookingRequest.room.stream().filter { r -> r.roomNumber == it.roomNumber }

                            .findFirst().isPresent) {

                        it.cancellationTime = getCurrentDateTime()

                        log.info("dates ${it.penaltyDeadLine} and cancellationTime ${it.cancellationTime}")

                        log.info("penalty applicable ${compareDates(it.penaltyDeadLine!!, it.cancellationTime!!)}")

                        var penaltyApplicable = compareDates(it.penaltyDeadLine!!, it.cancellationTime!!)

                        if(it.modifyBooking != null && it.modifyBooking.status.equals(CONFIRMED, ignoreCase = true)){

                            penaltyApplicable = compareDates(it.modifyBooking.penaltyDeadLine!!, it.cancellationTime!!)
                            it.penaltyDeadLine=it.modifyBooking.penaltyDeadLine
                            if(penaltyApplicable) {
                                it.penaltyAmount = it.modifyBooking.penaltyAmount?.toBigDecimal()?.setScale(
                                    2,
                                    RoundingMode.UP
                                )?.toDouble()
                            }

                        }


                        if (penaltyApplicable) {

                            totalPenaltyAmount += it.penaltyAmount!!.toBigDecimal().setScale(
                                2,
                                RoundingMode.UP
                            ).toDouble()

                        }
                        it.cancelRemark = CANCEL_REMARK_PAY_NOW_FULL_CANCELLATION_FULL

                        totalPaidAmount += it.paidAmount!!

                        it.cancelPayableAmount = 0.0
                        it.cancelRefundableAmount = 0.0
                        if (it.paidAmount?.minus(
                                it.penaltyAmount!!.toBigDecimal().setScale(
                                    2,
                                    RoundingMode.UP
                                ).toDouble()
                            )!! >= 0
                        ) {

                            if (penaltyApplicable) {
                                it.cancelRefundableAmount = it.paidAmount?.minus(
                                    it.penaltyAmount!!.toBigDecimal().setScale(
                                        2,
                                        RoundingMode.UP
                                    ).toDouble()
                                )!!
                                it.cancelRemark = CANCEL_REMARK_PAY_NOW_PENALTY_APPLIED_AND_PARTIAL_REFUND

                            } else {
                                it.cancelRefundableAmount = it.paidAmount!!

                            }
                            if (it.cancelRefundableAmount == 0.0) {
                                it.cancelRemark = ""

                            }

                        } else {

                            if (penaltyApplicable) {
                                it.cancelPayableAmount = it.penaltyAmount!!.toBigDecimal().setScale(
                                    2,
                                    RoundingMode.UP
                                ).toDouble().minus(it.paidAmount!!)

                            } else {
                                it.cancelPayableAmount = it.paidAmount!!

                            }
                            it.cancelRemark = Constants.CANCEL_REMARK_PAY_NOW_CANCEL_AMOUNT_MORE_THAN_PAID_AMOUNT

                        }

                        totalCancelPayableAmount += it.cancelPayableAmount!!

                        totalCancelRefundableAmount += it.cancelRefundableAmount!!



                        it.penaltyApplicable = penaltyApplicable


                    }

                    if(it.cancelRemark!!.isNotBlank() && it.cancelRemark!!.isNotEmpty())

                        remarksList.add(it.cancelRemark!!)
                }

                if(totalCancelPayableAmount > totalCancelRefundableAmount){

                    order.orderLineItems[0].hotel?.totalCancelPayableAmount = (totalCancelPayableAmount - totalCancelRefundableAmount).toBigDecimal().setScale(
                        0,
                        RoundingMode.HALF_UP
                    ).toDouble()

                    order.orderLineItems[0].hotel?.totalCancelRefundableAmount = 0.0

                }else{

                    order.orderLineItems[0].hotel?.totalCancelPayableAmount = 0.0

                    order.orderLineItems[0].hotel?.totalCancelRefundableAmount = (totalCancelRefundableAmount - totalCancelPayableAmount).toBigDecimal().setScale(
                        0,
                        RoundingMode.HALF_UP
                    ).toDouble()

                }

                order.orderLineItems[0].hotel?.totalCancellationPaidAmount =

                    totalPaidAmount.toBigDecimal().setScale(
                        0,
                        RoundingMode.HALF_UP
                    ).toDouble()

                order.orderLineItems[0].hotel?.totalCancellationPenaltyAmount =

                    totalPenaltyAmount.toBigDecimal().setScale(
                        0,
                        RoundingMode.HALF_UP
                    ).toDouble()

                if (cancelBookingRequest.cancelType == "C") {

                    voucherCancelRedemption(order)
                }

                if (cancelBookingRequest.cancelType == "C") {

                    partialOrder = order

                    val rooms: MutableList<Room> = mutableListOf()

                    rooms.addAll(order.orderLineItems[0].hotel?.rooms!!)

                    rooms.addAll(unmodifiedRooms)

                    order.orderLineItems[0].hotel?.rooms = rooms.sortedBy { room -> room.roomNumber }

                    if(!cancelBookingRequest.isFullCancellation) {
                        val adultCount = getCustomerCount(
                            order.orderLineItems[0].hotel?.rooms!!,
                            order.orderLineItems[0].hotel?.roomCount,
                            Constants.CUSTOMER_ADULT
                        )
                        order.orderLineItems[0].hotel?.adultCount = adultCount
                        val childCount = getCustomerCount(
                            order.orderLineItems[0].hotel?.rooms!!,
                            order.orderLineItems[0].hotel?.roomCount,
                            Constants.CUSTOMER_CHILD
                        )
                        order.orderLineItems[0].hotel?.childrens = childCount
                        val maxNoOfNights = getMaxNoOfNights(
                            order.orderLineItems[0].hotel?.rooms!!,
                            order.orderLineItems[0].hotel?.roomCount
                        )
                        order.orderLineItems[0].hotel?.bookingNoOfNights = maxNoOfNights
                    }
                    orderRepository.findOneAndUpdateOrder(order.orderId, order)

                }

            }else {

                var totalCancelPayableAmount = 0.0

                var totalCancelRefundableAmount = 0.0

                var totalPenaltyAmount = 0.0

                val totalPaidAmount = 0.0

                order.orderLineItems.first().hotel?.rooms?.forEach {

                    if (cancelBookingRequest.room.stream().filter { r -> r.roomNumber == it.roomNumber }

                            .findFirst().isPresent) {

                        it.cancellationTime = getCurrentDateTime()

                        log.info("dates ${it.penaltyDeadLine} and cancellationTime ${it.cancellationTime}")

                        var penaltyApplicable = compareDates(it.penaltyDeadLine!!, it.cancellationTime!!)

                        log.info("penaltyApplicable $penaltyApplicable")

                        if(it.modifyBooking != null && it.modifyBooking.status.equals(CONFIRMED, ignoreCase = true)){

                            penaltyApplicable = compareDates(it.modifyBooking.penaltyDeadLine!!, it.cancellationTime!!)
                            it.penaltyDeadLine=it.modifyBooking.penaltyDeadLine
                            if(penaltyApplicable) {
                                it.penaltyAmount = it.modifyBooking.penaltyAmount?.toBigDecimal()?.setScale(
                                    2,
                                    RoundingMode.UP
                                )?.toDouble()
                            }

                        }

                        it.penaltyApplicable = penaltyApplicable

                        log.info("penaltyApplicable ${it.penaltyApplicable}")

                        if (it.paidAmount == null) {
                            it.paidAmount = 0.0

                        }

                        if (penaltyApplicable) {

                            totalPenaltyAmount += it.penaltyAmount!!.toBigDecimal().setScale(
                                2,
                                RoundingMode.UP
                            ).toDouble()

                        }

                        if (penaltyApplicable) {
                            it.cancelRemark = CANCEL_REMARK_PAY_AT_HOTEL_FULL_CANCELLATION

                        } else {
                            it.cancelRemark = CANCEL_REMARK_EMPTY

                        }

                        if (it.paidAmount != null) {
                            it.cancelPayableAmount = 0.0
                            it.cancelRefundableAmount = 0.0

                            if (it.paidAmount?.minus(it.penaltyAmount!!.toBigDecimal().setScale(
                                    2,
                                    RoundingMode.UP
                                ).toDouble())!! >= 0) {

                                if (penaltyApplicable) {
                                    it.cancelRefundableAmount = it.paidAmount?.minus(it.penaltyAmount!!.toBigDecimal().setScale(
                                        2,
                                        RoundingMode.UP
                                    ).toDouble())!!

                                } else {
                                    it.cancelRefundableAmount = it.paidAmount!!

                                }


                            } else {

                                if (penaltyApplicable) {
                                    it.cancelPayableAmount = it.penaltyAmount!!.toBigDecimal().setScale(
                                        2,
                                        RoundingMode.UP
                                    ).toDouble().minus(it.paidAmount!!)
                                    it.cancelRemark = CANCEL_REMARK_PAY_AT_HOTEL_FULL_CANCELLATION

                                } else {
                                    it.cancelPayableAmount = it.paidAmount!!

                                }

                            }

                            if(it.paidAmount==0.0 && it.cancelPayableAmount==0.0 && it.cancelRefundableAmount==0.0){
                                it.cancelRemark=CANCEL_REMARK_EMPTY

                            }

                            totalCancelPayableAmount += it.cancelPayableAmount!!

                            totalCancelRefundableAmount += it.cancelRefundableAmount!!

                        }
                    }
                    remarksList.add(it.cancelRemark!!)
                }

                if(totalCancelPayableAmount > totalCancelRefundableAmount){

                    order.orderLineItems[0].hotel?.totalCancelPayableAmount = (totalCancelPayableAmount - totalCancelRefundableAmount).toBigDecimal().setScale(
                        0,
                        RoundingMode.HALF_UP
                    ).toDouble()

                    order.orderLineItems[0].hotel?.totalCancelRefundableAmount = 0.0

                }else{
                    order.orderLineItems[0].hotel?.totalCancelPayableAmount = 0.0

                    order.orderLineItems[0].hotel?.totalCancelRefundableAmount = (totalCancelRefundableAmount - totalCancelPayableAmount).toBigDecimal().setScale(
                        0,
                        RoundingMode.HALF_UP
                    ).toDouble()

                }


                order.orderLineItems[0].hotel?.totalCancellationPaidAmount =

                    totalPaidAmount.toBigDecimal().setScale(
                        0,
                        RoundingMode.HALF_UP
                    ).toDouble()


                order.orderLineItems[0].hotel?.totalCancellationPenaltyAmount =

                    totalPenaltyAmount.toBigDecimal().setScale(
                        0,
                        RoundingMode.HALF_UP
                    ).toDouble()

                if (cancelBookingRequest.cancelType == "C") {

                    partialOrder = order

                    val rooms: MutableList<Room> = mutableListOf()

                    rooms.addAll(order.orderLineItems[0].hotel?.rooms!!)

                    rooms.addAll(unmodifiedRooms)

                    order.orderLineItems[0].hotel?.rooms = rooms.sortedBy { room -> room.roomNumber }
                    voucherCancelRedemption(order)
                    if(!cancelBookingRequest.isFullCancellation) {
                        val adultCount = getCustomerCount(
                            order.orderLineItems[0].hotel?.rooms!!,
                            order.orderLineItems[0].hotel?.roomCount,
                            Constants.CUSTOMER_ADULT
                        )
                        order.orderLineItems[0].hotel?.adultCount = adultCount
                        val childCount = getCustomerCount(
                            order.orderLineItems[0].hotel?.rooms!!,
                            order.orderLineItems[0].hotel?.roomCount,
                            Constants.CUSTOMER_CHILD
                        )
                        order.orderLineItems[0].hotel?.childrens = childCount
                        val maxNoOfNights = getMaxNoOfNights(
                            order.orderLineItems[0].hotel?.rooms!!,
                            order.orderLineItems[0].hotel?.roomCount
                        )
                        order.orderLineItems[0].hotel?.bookingNoOfNights = maxNoOfNights
                    }
                    orderRepository.findOneAndUpdateOrder(order.orderId, order)

                }

            }

        }
        else{

            log.info("partial cancellation")

            var totalCancelPayableAmount = 0.0

            var totalCancelRefundableAmount = 0.0

            var totalPenaltyAmount = 0.0

            var totalPaidAmount = 0.0

            order.orderLineItems.first().hotel?.rooms?.forEach {
                if (cancelBookingRequest.room.stream().filter { r -> r.roomNumber == it.roomNumber }
                        .findFirst().isPresent) {
                    it.cancellationTime = getCurrentDateTime()
                    log.info("dates ${it.penaltyDeadLine} and cancellationTime ${it.cancellationTime}")
                    var penaltyApplicable = compareDates(it.penaltyDeadLine!!, it.cancellationTime!!)

                    log.info("penalty applicable $penaltyApplicable")

                    if(it.modifyBooking != null && it.modifyBooking.status.equals(CONFIRMED, ignoreCase = true)){

                        penaltyApplicable = compareDates(it.modifyBooking.penaltyDeadLine!!, it.cancellationTime!!)
                        it.penaltyDeadLine=it.modifyBooking.penaltyDeadLine
                        if(penaltyApplicable) {
                            it.penaltyAmount = it.modifyBooking.penaltyAmount?.toBigDecimal()?.setScale(
                                2,
                                RoundingMode.UP
                            )?.toDouble()
                        }

                    }

                    if (order.paymentMethod == PAY_ONLINE) {
                        it.cancelRemark = CANCEL_REMARK_PAY_NOW_PARTIAL_CANCELLATION_FULL_AND_PARTIAL_REFUND

                    } else {
                        it.paidAmount = 0.0

                        if (penaltyApplicable) {
                            it.cancelRemark = CANCEL_REMARK_PAY_AT_HOTEL_CANCELLATION_FEE_APPLIED

                        } else {
                            it.cancelRemark = CANCEL_REMARK_EMPTY

                        }

                    }

                    if (penaltyApplicable) {

                        totalPenaltyAmount += it.penaltyAmount!!.toBigDecimal().setScale(
                            2,
                            RoundingMode.UP
                        ).toDouble()

                    }

                    if (it.paidAmount != null) {
                        it.cancelPayableAmount = 0.0
                        it.cancelRefundableAmount = 0.0

                        if (it.paidAmount?.minus(it.penaltyAmount!!.toBigDecimal().setScale(
                                2,
                                RoundingMode.UP
                            ).toDouble())!! >= 0) {

                            if (penaltyApplicable) {
                                it.cancelRefundableAmount = it.paidAmount?.minus(it.penaltyAmount!!.toBigDecimal().setScale(
                                    2,
                                    RoundingMode.UP
                                ).toDouble())!!

                            } else {
                                it.cancelRefundableAmount = it.paidAmount!!

                            }

                        } else {

                            if (penaltyApplicable) {
                                it.cancelPayableAmount = it.penaltyAmount!!.toBigDecimal().setScale(
                                    2,
                                    RoundingMode.UP
                                ).toDouble().minus(it.paidAmount!!)

                            } else {
                                it.cancelPayableAmount = it.paidAmount!!

                            }
                            it.cancelRemark = Constants.CANCEL_REMARK_PAY_NOW_CANCEL_AMOUNT_MORE_THAN_PAID_AMOUNT

                        }

                        totalCancelPayableAmount += it.cancelPayableAmount!!

                        totalCancelRefundableAmount += it.cancelRefundableAmount!!

                        totalPaidAmount +=it.paidAmount!!

                    }

                    if(it.cancelPayableAmount==0.0 && it.cancelRefundableAmount==0.0){
                        it.cancelRemark=CANCEL_REMARK_EMPTY

                    }
                    it.penaltyApplicable = penaltyApplicable
                }

                remarksList.add(it.cancelRemark!!)
            }

            if(totalCancelPayableAmount > totalCancelRefundableAmount){

                order.orderLineItems[0].hotel?.totalCancelPayableAmount = totalCancelPayableAmount - totalCancelRefundableAmount

                order.orderLineItems[0].hotel?.totalCancelRefundableAmount = 0.0

            }else{

                order.orderLineItems[0].hotel?.totalCancelPayableAmount = 0.0

                order.orderLineItems[0].hotel?.totalCancelRefundableAmount = totalCancelRefundableAmount - totalCancelPayableAmount

            }

            order.orderLineItems[0].hotel?.totalCancellationPaidAmount =

                totalPaidAmount


            order.orderLineItems[0].hotel?.totalCancellationPenaltyAmount =

                totalPenaltyAmount

            if (cancelBookingRequest.cancelType == "C") {

                partialOrder = order

                val rooms: MutableList<Room> = mutableListOf()

                rooms.addAll(order.orderLineItems[0].hotel?.rooms!!)

                rooms.addAll(unmodifiedRooms)

                order.orderLineItems[0].hotel?.rooms = rooms.sortedBy { room -> room.roomNumber }

                val adultCount = getCustomerCount(order.orderLineItems[0].hotel?.rooms!!,order.orderLineItems[0].hotel?.roomCount,Constants.CUSTOMER_ADULT)
                order.orderLineItems[0].hotel?.adultCount = adultCount
                val childCount = getCustomerCount(order.orderLineItems[0].hotel?.rooms!!,order.orderLineItems[0].hotel?.roomCount,Constants.CUSTOMER_CHILD)
                order.orderLineItems[0].hotel?.childrens = childCount
                val maxNoOfNights = getMaxNoOfNights(order.orderLineItems[0].hotel?.rooms!!,order.orderLineItems[0].hotel?.roomCount)
                order.orderLineItems[0].hotel?.bookingNoOfNights = maxNoOfNights

                orderRepository.findOneAndUpdateOrder(order.orderId, order)

            }

        }
        val updatedOrder: Order = partialOrder ?: order
        updatedOrder.bookingCancelRemarks=""

        if(remarksList.isNotEmpty()){

            if(remarksList.contains(CANCEL_REMARK_PAY_AT_HOTEL_FULL_CANCELLATION)){

                updatedOrder.bookingCancelRemarks = CANCEL_REMARK_PAY_AT_HOTEL_FULL_CANCELLATION

            }else{

                val remarksSize = remarksList.stream().distinct().collect(Collectors.toList()).size

                if(remarksSize==1){

                    updatedOrder.bookingCancelRemarks = remarksList[0]

                }else{

                    updatedOrder.bookingCancelRemarks = CANCEL_REMARK_PAY_NOW_PENALTY_APPLIED_AND_PARTIAL_REFUND

                }

            }

        }
        if(cancelBookingRequest.cancelType=="C") {
            order = cancelBookingImp(cancelBookingRequest)

            if(order.orderLineItems.first().hotel?.status == PARTIALLY_CANCELLED) {
                order = getDates(order)
            }
            deleteCacheCollection(order)
        }

        return DataMapperUtils.mapConfirmHotelBookingResponse(order, paymentTransactionId(order.orderType, order.paymentDetails?.transaction_1?.toList()))

    }
*/
    private fun compareDates(penaltyTime:String, cancellationTime:String):Boolean{
        val dateFormat = SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.ENGLISH)

        val penalty = dateFormat.parse(penaltyTime)
        val cancel = dateFormat.parse(cancellationTime)
        log.info("date format penalty and cancel: $penalty and $cancel")

        val result = penalty.compareTo(cancel)
        log.info("result $result")

        /*penalty time is grater than the cancellation time,
        return false if it is less than cancellation time return true
        and if same date and time return false */

        return when{
            result > 0 -> false
            result < 0 -> true
            else -> false
        }

    }

    private fun getPaymentString(room: Rooms, order: Order): StringBuilder {
        val builder = StringBuilder()
        if(room.totalAmount!! > 0.0) {
            val bookingNumber = order.orderLineItems.first().hotel?.bookingNumber
            builder.append("PayOnlineInfo:Itinerary " + bookingNumber + "| CRS Ref "+room.confirmationId+"|TT - INR " + room.totalAmount?.toBigDecimal()?.setScale(
                0,
                RoundingMode.HALF_UP
            )?.toDouble() + ",")
            if (room.apportionValues.any { r -> r.paymentMethod.equals("giftCard", ignoreCase = true) }) {
                builder.append(getGiftCardPaymentString(room.apportionValues.filter { r ->
                    r.paymentMethod.equals(
                        "giftCard",
                        ignoreCase = true
                    )
                }.toList(), order))
            }
            if (room.apportionValues.any { r -> r.paymentMethod.equals("neuCoins", ignoreCase = true) }) {
                builder.append(getNeuCoinsPaymentString(room.apportionValues.filter { r ->
                    r.paymentMethod.equals(
                        "neuCoins",
                        ignoreCase = true
                    )
                }.toList().first()))
            }

            for (payment in room.apportionValues) {
                if (!payment.paymentMethod.equals(
                        "neuCoins",
                        ignoreCase = true
                    ) && !payment.paymentMethod.equals("giftCard", ignoreCase = true)
                ) {
                    builder.append(getCardPaymentString(payment))
                }
            }
            if (builder.isNotEmpty() && builder.toString().substring(builder.length - 1) == "|")
                builder.deleteCharAt(builder.length - 1)
        }
        return builder
    }

    private fun getPayAtHotelPaymentString(room: Room, order: Order): StringBuilder {
        val paymentDetails = order.paymentDetails?.transaction_1
        log.info("Pay At Hotel")
        val builder = StringBuilder()
        val txnId = paymentDetails?.first()?.ccAvenueTxnId
        val paymentMethodType = paymentDetails?.first()?.paymentMethodType
        val bookingNumber = order.orderLineItems.first().hotel?.bookingNumber
        builder.append(
            "Pay At Hotel: Itinerary " + bookingNumber + " | CRS Ref " + room.confirmationId + "| CC Avenue " +
                    paymentMethodType + " SI Reference No:" + txnId +
                    " - SI_Amount: " + room.grandTotal
        )
        log.info(builder.toString())
        return builder
    }

    private fun getGiftCardPaymentString(paymentList: List<PaymentDetails>, order: Order): String {
        log.info("gift card size.." + paymentList.size)
        val builder = StringBuilder()
        var cardExists = false;
        val paymentDetails = order.paymentDetails?.transaction_1
        for(pay in paymentList){
            if(paymentDetails?.any { orderPay ->
                    orderPay.paymentMethod.equals("giftCard", ignoreCase = true) && decrypt(orderPay.cardNumber.toString()) == decrypt(pay.cardNumber.toString()) && orderPay.txnStatus.equals(PaymentStatus.CHARGED.toString())
                } == true) {
                cardExists = true
                val p =paymentDetails.first { payDetails -> payDetails.paymentMethod.equals("giftCard", ignoreCase = true) && decrypt(payDetails.cardNumber.toString()) == decrypt(pay.cardNumber.toString()) }
                builder.append("TEGC: "+ decrypt( p.cardNumber.toString()).takeLast(TEGC_LAST_DIGITS)+" - INR "+pay.txnNetAmount?.toBigDecimal()?.setScale(
                    2,
                    RoundingMode.UP
                )?.toDouble()+" | ")
            }
        }
        val modifiedGiftCardPaymentString = StringBuilder()
        if (builder.toString().isNotEmpty()) {
            val temp = builder.toString().substring(0, builder.toString().lastIndexOf("|"))
            modifiedGiftCardPaymentString.append(temp)
        }
        if (cardExists) {
            modifiedGiftCardPaymentString.append("| ")
        }
        return modifiedGiftCardPaymentString.toString().trim()

    }

    private fun getNeuCoinsPaymentString(payment: PaymentDetails): Any {
        val builder = StringBuilder()
        val membershipId = payment.userId
        if(payment.txnStatus == PaymentStatus.CHARGED.toString()) {
            builder.append(" NeuCoins:"+payment.txnNetAmount?.toInt()+" Coins: Mem " + membershipId + " : RedemptionId " + payment.redemptionId +"| ")
        }
        return builder
    }

    private fun getCardPaymentString(payment: PaymentDetails): StringBuilder {
        val transactionId: String
        val builder = StringBuilder()
        if(payment.txnStatus == PaymentStatus.CHARGED.toString()){
            transactionId = payment.txnId.toString()
            builder.append("Online: Juspay Ref ")
            builder.append(transactionId)
            builder.append(" | ")
            builder.append("CC Avenue Ref: ")
            builder.append(payment.ccAvenueTxnId)
            builder.append(" - ")
            builder.append("INR " + payment.txnNetAmount?.toBigDecimal()?.setScale(
                2,
                RoundingMode.UP
            )?.toDouble())
        }
        return builder
    }

    private fun validateErrorResponse(errorResponse: LoyaltyErrorResponses) {
        val list=ArrayList<String>()
        val errorFields = errorResponse.error?.javaClass?.declaredFields
        var s = ""
        for (field in errorFields!!) {
            field.isAccessible = true
            val fieldValue = field[errorResponse.error]
            if (fieldValue != null && fieldValue is List<*>) {
                for (item in fieldValue) {
                    if (item is ErrorObject) {
                        s = "${field.name} ${item.message}..".replace("['", "").replace("']", "")
                        list.add(s)
                    }
                }
            }
        }
        log.info("list after modification..$list")

        throw HttpResponseException(s, HttpStatusCode.BadRequest)
    }

    /*suspend fun modifyBooking(modifyBookingRequest: ModifyBookingRequest,jwtToken:String?,hash: String?): Order {
        val orderId = modifyBookingRequest.orderId
        var order = orderRepository.findOrderByOrderId(orderId)
        val getCartURL = prop.cartServiceHost.plus(GET_CART_URL)
        if (order.orderLineItems.isEmpty()) {
            throw HttpResponseException(ORDER_NOT_FOUND_FAILED_ERR_MSG, HttpStatusCode.NotFound)
        }
        else {
            val customerHash = order.customerHash
            val cartResponse = client.get(getCartURL) {
                contentType(ContentType.Application.Json)
                headers {
                    append(CUSTOMERHASH, customerHash)
                    append(CATEGORY_TYPE, HOTEL_BOOKING)
                }
            }
            log.info("modified cart response body ${cartResponse.bodyAsText()}")
            val response = cartResponse.body<OrderLineItems>()
            if(response.cartDetails.items.isNullOrEmpty()){
                throw HttpResponseException(EMPTY_CART_ERR_MSG, HttpStatusCode.BadRequest)
            }
            val orderType = order.orderType
            val travelerDetails = DataMapperUtils.mapTravelerDetails(order)
            val modifyBookingCount = order.modifyBookingCount.plus(MODIFY_BOOKING_FIRST)

            order = DataMapperUtils.mapModifiedOrder(
                customerHash,
                orderType,
                response,
                orderId,
                travelerDetails,
                order, modifyBookingCount
            )
            val confirmation = modifyUpdateBookingImp(order, CONFIRMED)

            order.orderLineItems[0].hotel?.rooms?.forEach { room ->
                if(room.modifyBooking != null && room.modifyBooking.status == PENDING) {
                    for ((key) in confirmation) {
                        if (key == room.roomNumber) {
                            if (confirmation[key] == room.modifyBooking.confirmationId) {
                                room.modifyBooking.status = CONFIRMED
                            } else if (confirmation[key] == "FAILED") {
                                room.modifyBooking.status = FAILED
                            }
                        }
                    }
                }
            }
            if(confirmation.any{ k -> (k.key?.equals(FAILED) == true) || (k.key?.equals(PENDING) == true) }){
                order.orderStatus = ORDER_STATUS_PENDING
            }else{
                order.orderStatus = ORDER_STATUS_SUCCESS
            }

            log.info("modified order: ${order.json}")
            orderRepository.findOneAndUpdateOrder(orderId, order)
            order = modifyGetBookingDetails(order)
            emptyCart(order.customerHash, HOTEL_BOOKING)
            deleteCacheCollection(order)
            return order
        }
    }*/

    private suspend fun giftCardRedeemImp(order: Order, jwtToken: String?, isCronJob: Boolean): Order{
        var transactionNumber: String
        val id = order.orderId
        val split = id.split("_")
        val orderId = split[1].toLong()
        val confirmationIdList = ArrayList<String>()
        log.info("Redeem gift card Implementation")
        var updateOrder = order
        val paymentDetails = order.paymentDetails?.transaction_1
        for (i in 0 until paymentDetails!!.size) {
            updateOrder = orderRepository.findOrderByOrderId(order.orderId)
            if (paymentDetails[i].paymentType == GIFT_CARD && paymentDetails[i].txnStatus == PENDING) {
                val hotelName = updateOrder.orderLineItems.first().hotel?.name
                val bookingNumber = updateOrder.orderLineItems.first().hotel?.bookingNumber
                updateOrder.orderLineItems.first().hotel?.rooms?.forEach {
                    confirmationIdList.add(it.confirmationId.toString())
                }
                val billAmount = updateOrder.orderLineItems.first().hotel?.grandTotal!!
                val confirmString = confirmationIdList.joinToString(",")
                val propertyName = hotelName.plus("|").plus(confirmString)
                log.info("propertyName $propertyName")
                val redeemGCReq = DataMapperUtils.mapRedeemGiftCardRequest(paymentDetails,i, billAmount, propertyName, bookingNumber.toString(), order.orderId.toString())
                log.info("OrderId ${updateOrder.orderId} redeem gift card request ${redeemGCReq.json}")
                updateOrder = redeemGiftCard(i,redeemGCReq, updateOrder)
            }  else if (paymentDetails[i].paymentType == TATA_NEU && paymentDetails[i].txnStatus == PENDING) {
                log.info("Initiating redemption of neuCoins, amount ${paymentDetails[i].txnNetAmount}")
                transactionNumber = when (updateOrder.orderType) {
                    OrderType.HOTEL_BOOKING, OrderType.HOLIDAYS -> {
                        updateOrder.orderLineItems.first().hotel?.bookingNumber.toString()
                    }

                    OrderType.GIFT_CARD_PURCHASE -> {
                        GC_NEU_COINS_PREFIX.plus(orderId)
                    }

                    else -> {
                        EPICURE_NEU_COINS_PREFIX.plus(orderId)
                    }
                }
                if(isCronJob){
                    val authToken = getAuthToken(order.orderId)
                    log.info("cronjob has picked order=${order.orderId} to retry neucoins redemption with jwtToken= $authToken")
                    if(!authToken.isNullOrEmpty()) {
                        updateOrder = callRedeemNeuCoins(transactionNumber,updateOrder,paymentDetails[i].txnNetAmount.toString(),authToken)
                    }
                }else {
                    updateOrder = callRedeemNeuCoins(transactionNumber,updateOrder,paymentDetails[i].txnNetAmount.toString(),jwtToken)
                }
            }
        }
        return updateOrder
    }

    private suspend fun redeemGiftCard(i: Int, redeemGCReq: RedeemGiftCardRequest, order: Order): Order{
        val paymentDetails = order.paymentDetails?.transaction_1
        log.info("Redeem gift card ${redeemGCReq.json}")
        try {
            val redeemGiftCardUrl = prop.loyaltyServiceHost.plus(REDEEM_GIFT_CARD)
            val response = client.post(redeemGiftCardUrl) {
                timeout {
                    requestTimeoutMillis = REQUEST_TIME_OUT.toLong()
                }
                contentType(ContentType.Application.Json)
                setBody(redeemGCReq)
            }
            log.info("Redeem gift card response details ${response.bodyAsText()} response status ${response.status.value}")

            if (response.status == HttpStatusCode.OK) {
                val redeemGiftCardResponse = response.body() as RedeemGiftCardResponse
                if (redeemGiftCardResponse.responseCode == 0) {
                    paymentDetails?.get(i)?.approvalCode = redeemGiftCardResponse.ApprovalCode
                    paymentDetails?.get(i)?.batchNumber = redeemGiftCardResponse.CurrentBatchNumber
                    paymentDetails?.get(i)?.transactionId = redeemGiftCardResponse.transactionId
                    paymentDetails?.get(i)?.txnStatus = PaymentStatus.CHARGED.toString()
                    log.info("payment details for GC ${paymentDetails?.get(i)?.txnStatus} ")
                }
            } else {
                paymentDetails?.get(i)?.txnStatus = PaymentStatus.PENDING.toString()
            }
        }catch (e: java.lang.Exception){
            log.info("Loyalty service for Gift card redemption is not available")
            paymentDetails?.get(i)?.txnStatus = PaymentStatus.PENDING.toString()
        }
        orderRepository.findOneAndUpdateOrder(order.orderId, order)
        log.info("order updated after GC redemption ${order.json}")
        return order
    }

    suspend fun updateOrder(type: String?,updateOrderRequest: UpdateOrderRequest): Any? {
        val order = orderRepository.findOrderByOrderId(updateOrderRequest.orderId)
        when {
            type.equals(GIFT_CARD_PURCHASE, ignoreCase = true) -> {
                order.paymentDetails?.transaction_1=updateOrderRequest.paymentDetails
                order.payableAmount=updateOrderRequest.payableAmount!!
                orderRepository.findOneAndUpdateOrder(updateOrderRequest.orderId, order)
                log.info("orderId of gift card${updateOrderRequest.orderId} with updated payment")
                return DataMapperUtils.mapUpdateOrderResponse(order)
            }
            type.equals(HOTEL_BOOKING, ignoreCase = true) -> {
                log.info("payment details before order update ${order.paymentDetails?.transaction_1?.json}")
                order.paymentDetails?.transaction_1 = updateOrderRequest.paymentDetails

                order.paymentMethod = updateOrderRequest.paymentMethod.toString()
                order.payableAmount = updateOrderRequest.payableAmount!!
                order.orderLineItems.first().hotel?.balancePayable = updateOrderRequest.balancePayable

                order.orderLineItems.first().hotel?.isDepositAmount = updateOrderRequest.isDepositAmount
                order.orderLineItems.first().hotel?.isDepositPaid = updateOrderRequest.isDepositAmount

                orderRepository.findOneAndUpdateOrder(order.orderId, order)
                log.info("orderId ${updateOrderRequest.orderId} with updated payment details ${order.json}")
                return DataMapperUtils.mapUpdateOrderResponse(order)
            }
            type.equals(MEMBERSHIP_PURCHASE, ignoreCase = true) -> {
                order.paymentDetails?.transaction_1=updateOrderRequest.paymentDetails
                order.payableAmount=updateOrderRequest.payableAmount!!
                orderRepository.findOneAndUpdateOrder(updateOrderRequest.orderId, order)
                log.info("orderId of loyalty${updateOrderRequest.orderId} with updated payment")
                return orderRepository.findOrderByOrderId(order.orderId)
            }
            else -> return null
        }
    }

    suspend fun updateOrderBooking(cart: OrderLineItems, order: Order, paymentMethod: String): OrderResponse {
        val confirmOrderList = ArrayList<String>()
        val bookingNumber = order.orderLineItems.first().hotel?.bookingNumber
        order.orderLineItems.first().hotel?.rooms?.forEach {
            if (!it.confirmationId.isNullOrEmpty()) {
                confirmOrderList.add(it.confirmationId.toString())
            }
        }
        val travelerDetails = order.orderLineItems[0].hotel?.rooms?.get(0)?.travellerDetails?.get(0)?.let {
            TravelerDto(
                salutation = it.salutation?.removeSuffix("."),
                countryCode = it.countryCode,
                email = it.email,
                firstName = it.firstName,
                gender = it.gender,
                lastName = it.lastName,
                agreedPrivacyPolicy = order.agreedPrivacyPolicy,
                agreedTnc = order.agreedTnc,
                GSTNumber = it.gstNumber,
                membershipNo = it.membershipNumber,
                memberShipType = "",
                paymentMethod = order.paymentMethod,
                phoneNo = it.mobile,
                specialRequest = "",
                title = "",
                voucherNumber = "",
                voucherPin = "",
                membershipProgramId = "",
                membershipId="",
                isCampaignOffer = it.isCampaignOffer,
                offerIdentifier=it.offerIdentifier
            )
        }
        val updatedOrder =
            mapCreateOrder(cart.cartDetails.cartId, order.orderType, cart, order.orderId, travelerDetails)
        for (i in 0 until updatedOrder.orderLineItems.first().hotel?.rooms?.size!!) {
            updatedOrder.orderLineItems.first().hotel?.rooms?.get(i)?.confirmationId = confirmOrderList[i]
        }
        updatedOrder.orderLineItems.first().hotel?.bookingNumber = bookingNumber
        updatedOrder.paymentMethod = paymentMethod
        orderRepository.findOneAndUpdateOrder(updatedOrder.orderId, updatedOrder)
        return DataMapperUtils.mapUpdateOrderResponse(order)
    }

    suspend fun updateGC() {
        val orders =
            orderRepository.findOrderByOrderTypeAndOrderStatus(OrderType.GIFT_CARD_PURCHASE, Constants.PROCESSING)
        log.info("orders received from DB are ${orders.json}")
        orders.forEach {
            log.info("order id ${it.orderId}")
            if (it.orderLineItems[0].giftCard?.giftCardDetails?.get(0)?.theme == Constants.PHYSICAL_GIFT_CARD) {
                it.orderStatus = OrderStatus.SUCCESS.toString()
                orderRepository.findOneAndUpdateOrder(it.orderId, it)
                log.info("Physical gift card ${it.orderId} updated to CONFIRMED")
            }
            val gcOrderStatusResponse = callLoyaltyOrderStatus(it)
            log.info("Gift card order status response ${gcOrderStatusResponse.status}")
            if (gcOrderStatusResponse.status == Constants.COMPLETE) {
                val activateResponse = callLoyaltyActivate(gcOrderStatusResponse)
                log.info("Activate GC response ${activateResponse.json}")
                val giftCardDetails = it.orderLineItems[0].giftCard?.giftCardDetails
                for (i in 0 until activateResponse.cards?.size!!) {
                    giftCardDetails?.get(i)?.cardNumber = activateResponse.cards[i]?.cardNumber?.let { encrypt(it) }
                    giftCardDetails?.get(i)?.cardPin = activateResponse.cards[i]?.cardPin?.let { encrypt(it) }
                    giftCardDetails?.get(i)?.cardId = activateResponse.cards[i]?.cardId.toString()
                    giftCardDetails?.get(i)?.validity = activateResponse.cards[i]?.validity
                }
                it.orderStatus = OrderStatus.SUCCESS.toString()
                orderRepository.findOneAndUpdateOrder(it.orderId, it)
                log.info("order ${it.orderId} updated successfully")
            }
        }
    }

    private suspend fun callLoyaltyOrderStatus(order: Order): GCOrderStatusResponse {
        val orderStatusUrl = prop.loyaltyServiceHost + Constants.LOYALTY_ORDER_STATUS
        val response = client.post(orderStatusUrl) {
            timeout {
                requestTimeoutMillis = REQUEST_TIME_OUT.toLong()
            }
            contentType(ContentType.Application.Json)
            setBody(GCOrderStatusRequest(order.orderId, null))
        }
        val successResponse = response.body<GCOrderStatusResponse>()


        if (response.status != HttpStatusCode.OK) {
            log.info("Order status API failed due to ${response.body<GCOrderStatusErrorResponse>().message}")
        }
        return successResponse
    }

    private suspend fun callLoyaltyActivate(order: GCOrderStatusResponse): ActivateGCResponse {
        val activateGCURL = prop.loyaltyServiceHost + Constants.LOYALTY_ACTIVATE_GC
        val response = client.post(activateGCURL) {
            timeout {
                requestTimeoutMillis = REQUEST_TIME_OUT.toLong()
            }
            contentType(ContentType.Application.Json)
            setBody(ActivateGCRequest(order.orderId!!))
        }
        val successResponse = response.body<ActivateGCResponse>()
        if (response.status != HttpStatusCode.OK) {
            log.info("Order status API failed due to ${response.body<GCOrderStatusErrorResponse>().message}")
        }
        return successResponse
    }

    suspend fun getBookingDetails(fetchBookingDetailsBody: FetchBookingDetailsRequest): GetBookingDetailsResponse {
        log.info("Fetch booking details request body : $fetchBookingDetailsBody")
        val fetchBookingDetailsURL = prop.hudiniServiceHost.plus(FETCH_BOOKING_DETAILS)
        val endDate=endDateOfFetchBooking(fetchBookingDetailsBody.arrivalDate)
        val arrivalDate="${fetchBookingDetailsBody.arrivalDate};${endDate}"
        val response = client.post(fetchBookingDetailsURL) {
            val body=FetchBookingDetailsByEmail(fetchBookingDetailsBody.emailId,arrivalDate)
            timeout {
                requestTimeoutMillis = REQUEST_TIME_OUT.toLong()
            }
            contentType(ContentType.Application.Json)
            setBody(body)
        }
        val bookingDetailsResponse = response.body<GetBookingDetailsResponse>()
        if (bookingDetailsResponse.data?.getHotelBookingDetails?.reservations.isNullOrEmpty()){
            val body=FetchBookingDetailsByMobile(fetchBookingDetailsBody.guestPhoneNumber,arrivalDate)
            val response = client.post(fetchBookingDetailsURL) {
                timeout {
                    requestTimeoutMillis = REQUEST_TIME_OUT.toLong()
                }
                contentType(ContentType.Application.Json)
                setBody(body)
            }
            log.info("Fetch booking details of response is{} ",response.bodyAsText())
            return response.body<GetBookingDetailsResponse>()
        }
        log.info("Fetch booking details response ${response.bodyAsText()} response status ${response.status}")
        log.info("Success casting  ${bookingDetailsResponse.data?.getHotelBookingDetails?.reservations?.get(0)?.crsConfirmationNumber}")
        return bookingDetailsResponse
    }

    private suspend fun getBookingDetailsImp(getBookingDetails: GetBookingDetailsRequest): GetBookingDetailsResponse? {
        log.info("Get booking details request body : $getBookingDetails")
        try {
            val fetchBookingDetailsURL = prop.hudiniServiceHost.plus(FETCH_BOOKING_DETAILS)
            val response = client.post(fetchBookingDetailsURL) {
                timeout {
                    requestTimeoutMillis = REQUEST_TIME_OUT.toLong()
                }
                contentType(ContentType.Application.Json)
                setBody(getBookingDetails)
            }
            log.info("Get booking details response ${response.bodyAsText()} response status ${response.status}")
            return if (response.status == HttpStatusCode.OK) {
                response.body<GetBookingDetailsResponse>()
            } else {
                null
            }
        }catch (e: Exception){
            log.info("Get booking details API Failed")
            return null
        }

    }
    suspend fun getVouchers(memberId:String):HttpResponse{
        val getPrivilegesURL = "${prop.loyaltyServiceHost.plus(LOYALTY_GET_PRIVILEGES)}/$memberId"
        val response = client.get(getPrivilegesURL)
        log.info("Privileges of the member with $memberId are $response")

        if (response.status==HttpStatusCode.OK || response.status==HttpStatusCode.BadRequest) {
            return response
        }
        else{
            throw GravtyVoucherNotFoundException(GRAVTY_VOUCHER_NOT_FOUND)
        }

    }

    private suspend fun voucherRedemptionAvailImp(orderId: String) {
        val order = orderRepository.findOrderByOrderId(orderId)
        if (order.orderLineItems.first().hotel?.voucherRedemption?.isComplementary == true &&
            (order.orderLineItems.first().hotel?.voucherRedemption?.memberId.isNullOrEmpty() ||
            order.orderLineItems.first().hotel?.voucherRedemption?.bitDate.isNullOrEmpty() ||
            order.orderLineItems.first().hotel?.voucherRedemption?.type.isNullOrEmpty())
        ) {
            order.orderStatus = FAILED
            orderRepository.findOneAndUpdateOrder(order.orderId, order)
            throw HttpResponseException("Voucher redemption failed", HttpStatusCode.BadRequest)
        } else if (order.paymentStatus.equals(
                CHARGED,
                ignoreCase = true
            ) && (order.orderLineItems.first().hotel?.voucherRedemption != null &&
                    !(order.orderLineItems.first().hotel?.voucherRedemption?.memberId.isNullOrEmpty()))
        ) {
            val voucherRedemptionReq = DataMapperUtils.mapVoucherRedemptionRequest(order)
            log.info("voucher redemption ${voucherRedemptionReq.json}")
            try {
                val voucherAvail = prop.loyaltyServiceHost.plus(LOYALTY_VOUCHER_AVAIL)
                val response = client.post(voucherAvail) {
                    timeout {
                        requestTimeoutMillis = REQUEST_TIME_OUT.toLong()
                    }
                    contentType(ContentType.Application.Json)
                    setBody(voucherRedemptionReq)
                }
                log.info("voucher redemption response ${response.bodyAsText()}")
                if (response.status == HttpStatusCode.OK || response.status == HttpStatusCode.Created) {
                    val voucherAvailResponse = response.body<ChamberVoucherAvailResponse>()
                    log.info("voucher redemption response ${voucherAvailResponse.json}")
                    order.orderLineItems.first().hotel?.voucherRedemption?.availBitId =
                        voucherAvailResponse.data.availed_privileges.first().availment_bit_id
                    order.orderLineItems.first().hotel?.voucherRedemption?.status =
                        voucherAvailResponse.data.availed_privileges.first().status
                    orderRepository.findOneAndUpdateOrder(order.orderId, order)
                } else {
                    order.orderStatus = PENDING
                    orderRepository.findOneAndUpdateOrder(order.orderId, order)
                    throw HttpResponseException("Voucher redemption failed ", HttpStatusCode.BadRequest)
                }
            } catch (e: Exception) {
                log.error("Voucher redemption failed ${e.cause} and ${e.message}")
                order.orderStatus = PENDING
                orderRepository.findOneAndUpdateOrder(order.orderId, order)
                throw HttpResponseException("Voucher redemption failed" , HttpStatusCode.InternalServerError)
            }
        }

    }

    private suspend fun voucherCancelRedemption(order: Order){
        if(order.orderLineItems.first().hotel?.voucherRedemption != null && !(order.orderLineItems.first().hotel?.voucherRedemption?.memberId.isNullOrEmpty())) {
            val voucherCancelRedemptionReq = DataMapperUtils.mapVoucherCancelRedemptionRequest(order)
            log.info("voucher cancel redemption  ${voucherCancelRedemptionReq.json}")

            val voucherAvail = prop.loyaltyServiceHost.plus(LOYALTY_VOUCHER_CANCEL_AVAIL)
            val response = client.post(voucherAvail) {
                timeout {
                    requestTimeoutMillis = REQUEST_TIME_OUT.toLong()
                }
                contentType(ContentType.Application.Json)
                setBody(voucherCancelRedemptionReq)
            }
            log.info("voucher cancel response ${response.bodyAsText()}")
            if (response.status == HttpStatusCode.OK || response.status == HttpStatusCode.Created) {
                val cancelResponse = response.body<BitCancellationVoucherReversalResponse>()
                order.orderLineItems.first().hotel?.voucherRedemption?.status =
                    cancelResponse.status
                order.orderLineItems.first().hotel?.voucherRedemption?.bitCategory = cancelResponse.originalBit.header.hBitCategory
                orderRepository.findOneAndUpdateOrder(order.orderId, order)
            } else return response.body()
        }
    }

    private fun convertToDesiredFormat(inputDateTime: String): String {
        val dateTimeParts = inputDateTime.split(" ", "/").toTypedArray()
        val day = dateTimeParts[0]
        val month = dateTimeParts[1]
        val year = dateTimeParts[2]
        val timeParts = dateTimeParts[3].split(":").toTypedArray()
        val hour = timeParts[0]
        val minute = timeParts[1]
        val second = timeParts[2]

        return "$year$month$day$hour$minute$second"
    }

    suspend fun addTenderModeImpl(request: TenderModeRequest):Any {
        val order = orderRepository.findOrderByOrderId(request.orderId)
        val transaction = order.paymentDetails?.transaction_1
        var amount = 0.0
        if (order.payableAmount != 0.0) {
            if (order.payableAmount > request.amount) {
                amount = order.payableAmount.minus(request.amount)
                order.payableAmount = amount
                transaction?.withIndex()?.find { (_, payment) ->
                    payment.paymentType == JUS_PAY
                }.apply {
                    this?.value?.txnNetAmount=amount
                }
                transaction?.withIndex()?.find { (_, payment) ->
                    payment.paymentType == NEU_COINS
                }.apply {
                    amount = request.amount
                    this?.value?.txnNetAmount = amount
                }
            } else if (order.payableAmount <= request.amount) {
                amount = order.payableAmount
                order.payableAmount = 0.0
                if (transaction?.get(0)?.paymentType == NEU_COINS) {
                    transaction[0].txnNetAmount = request.amount
                }else{
                    transaction?.get(0)?.txnNetAmount = amount
                }
                transaction?.withIndex()?.find { (_, payment) ->
                    payment.paymentType == JUS_PAY
                }.let {
                    it?.let { it1 -> order.paymentDetails?.transaction_1?.removeAt(it1.index) }
                }
            }
        }else{ order.paymentDetails?.transaction_1?.withIndex()?.find { (_, payment) ->
            payment.paymentType == JUS_PAY
        }.let {
            it?.let { it1 -> order.paymentDetails?.transaction_1?.removeAt(it1.index) }
        }
            throw HttpResponseException(TOTAL_PAYABLE_AMOUNT_ZERO,HttpStatusCode.ExpectationFailed)
        }
        val paymentDetails = DataMapperUtils.mapNeuCoinPaymentDetails(request,amount.toString())
        transaction?.add(paymentDetails)
        orderRepository.findOneAndUpdateOrder(request.orderId, order)
        return if (order.orderType==OrderType.MEMBERSHIP_PURCHASE){
            DataMapperUtils.mapTenderModeEpicureResponse(order)
        }else{
            DataMapperUtils.mapTenderModeResponse(order)
        }

    }

    suspend fun deleteTenderModeImpl(request: TenderModeRequest): GiftCardResponse {
        val order = orderRepository.findOrderByOrderId(request.orderId)
        val amount: Double?
        if (request.tenderMode == TATA_NEU) {
            amount = order.payableAmount.plus(request.amount)
            order.payableAmount = amount
            order.paymentDetails?.transaction_1?.withIndex()?.find { (_, payment) ->
                payment.paymentType == TATA_NEU
            }.let {
                it?.let { _ -> order.paymentDetails?.transaction_1?.removeLast() }
            }
            order.paymentDetails?.transaction_1?.withIndex()?.find { (_, payment) ->
                payment.paymentType == JUS_PAY
            }?.let {
                it.value.txnNetAmount = amount
            } ?: run {
                if (order.payableAmount != 0.0) {
                    order.paymentDetails?.transaction_1?.add(DataMapperUtils.mapJuspayPaymentDetails(amount.toString()))
                }
            }
        }
        orderRepository.findOneAndUpdateOrder(request.orderId, order)
        return DataMapperUtils.mapTenderModeResponse(order)
    }

    private suspend fun gravityVoucherRedeem(voucherCode: String?, voucherPin: String?, orderId: String, planId: String,billAmount:Double): Boolean {
        when {
            !voucherCode.isNullOrEmpty() && !voucherPin.isNullOrEmpty() -> {
                val voucherRedeemRequest = mapGravityVoucherRedeemRequest(voucherCode, voucherPin, orderId, planId,billAmount)
                log.info("request for gravity voucher redeem ${voucherRedeemRequest.json}")
                try {
                    val redeemGiftCardUrl = prop.loyaltyServiceHost.plus(REDEEM_GIFT_CARD)
                    val response = client.post(redeemGiftCardUrl) {
                        timeout {
                            requestTimeoutMillis = REQUEST_TIME_OUT.toLong()
                        }
                        contentType(ContentType.Application.Json)
                        setBody(voucherRedeemRequest)
                    }
                    log.info("Gift card voucher redemption response:: ${response.bodyAsText()}, Status:: ${response.status}")
                    val redeemGiftCardResponse = response.body() as RedeemGiftCardResponse
                    return when (response.status) {
                        HttpStatusCode.OK -> {
                            redeemGiftCardResponse.responseCode == 0
                        }
                        else -> false
                    }
                }catch (e: Exception){
                    log.error("gravityVoucherRedeem failed ${e.cause} and ${e.message}")
                    return false
                }
            }
            else -> return false
        }

    }

    suspend fun createOrderForLoyalty(hash: String, loyaltyRequest: LoyaltyCreateOrderRequest,referer :String?, accessToken: String, refreshToken: String): Any {

        val getCartURL = prop.cartServiceHost.plus(GET_CART_URL)
        val cartResponse = client.get(getCartURL) {
            contentType(ContentType.Application.Json)
            headers {
                append(CUSTOMERHASH, hash)
                append(CATEGORY_TYPE, MEMBERSHIP_PURCHASE)
            }
        }
        log.info("loyalty cart response body ${cartResponse.bodyAsText()}")
        val loyaltyCartResponse = cartResponse.body<LoyaltyCartResponse>()
        if(loyaltyRequest.extraData != null && (loyaltyCartResponse.items.epicureDetails.isTata ||
                    loyaltyCartResponse.items.epicureDetails.isBankUrl ||
                    loyaltyCartResponse.items.epicureDetails.isShareHolder)) {
            getMemberShipDetails(loyaltyCartResponse, loyaltyRequest)
        }
        val(memberShipPlanCode, memberShipPlanName) = extractMemberShipDetails(loyaltyCartResponse)
        if (loyaltyRequest.gstNumber.isNullOrEmpty()) {
            loyaltyRequest.gstNumber = "-"
        }
        val response = createLoyaltyOrder(loyaltyCartResponse, loyaltyRequest)
        log.info("create order for loyalty $response")
        val orderId = orderRepository.orderIdGeneration(prop.orderIdEpicure)
        if (response != null) {
            val order = DataMapperUtils.mapLoyaltyRequest(
                hash,
                loyaltyRequest,
                orderId,
                response.toString(),
                loyaltyCartResponse,
                memberShipPlanName,
                memberShipPlanCode
            )
            setBrandName(order,referer)
            createRedisEntry(order, accessToken, refreshToken)
            orderRepository.saveOrder(order)
            val loyaltyCreateOrderResponse = DataMapperUtils.mapLoyaltyCreateOrderResponse(order)
            log.info("Loyalty order created as $loyaltyCreateOrderResponse")
            return loyaltyCreateOrderResponse
        }
        return HttpResponseException("Failed to get the memberId", HttpStatusCode.BadRequest)
    }

    private suspend fun getMemberShipDetails(loyaltyCartResponse: LoyaltyCartResponse, loyaltyRequest: LoyaltyCreateOrderRequest) {
            if(loyaltyRequest.extraData != null) {
                ValidatorUtils.validateRequestBody(gravityVoucherValidation.validate(loyaltyRequest.extraData))
            }
            val balEnquiry = balanceEnquiryForVoucher(loyaltyRequest.extraData?.gravityVoucherCode.toString(),
                loyaltyRequest.extraData?.gravityVoucherPin.toString())
            val responseCode = balEnquiry.Cards?.first()?.ResponseCode
            val giftCardValues: GiftCardRestrictionValues? = orderRepository.getGiftCardRestrictionValues(CPG_ID)
            val value = if (loyaltyCartResponse.items.epicureDetails.isShareHolder || loyaltyCartResponse.items.epicureDetails.isTata) {
                SHAREHOLDER
            } else BANK
            log.info("value is $value")
        when{
            balEnquiry.ResponseCode == "0" ->{
                giftCardValues?.giftCardValues?.withIndex()?.find {
                    it.value.booking.contains(value, ignoreCase = true)
                }?.let {
                    if (it.value.cardType.equals(balEnquiry.Cards?.first()?.CardType, ignoreCase = true)) {
                        if(balEnquiry.Cards?.first()?.Balance == 1) {
                            log.info(
                                "Voucher code:: ${loyaltyRequest.extraData?.gravityVoucherCode.toString()} " +
                                        "and pin:: ${loyaltyRequest.extraData?.gravityVoucherPin.toString()} is valid"
                            )
                        }else if(balEnquiry.Cards?.first()?.Balance == 0){
                            throw HttpResponseException(VOUCHER_CHECK_BALANCE_ERROR_MESSAGE, HttpStatusCode.BadRequest)
                        }
                    }else{
                        throw HttpResponseException(WRONG_VOUCHER_CODE_AND_PIN_ERROR_MESSAGE, HttpStatusCode.BadRequest)
                    }
                }?:run {
                    throw HttpResponseException(VOUCHER_CODE_OR_VOUCHER_PIN_INCORRECT_ERROR_MESSAGE, HttpStatusCode.BadRequest)
                }
            }
            responseCode.equals(ERROR_CODE_EXPIRY_GC) || responseCode.equals(ERROR_CODE_MULTI_ATTEM_EXPIRY_GC)->{
                throw HttpResponseException(ERROR_RESPONSE_MESSAGE_OF_VOUCHER, HttpStatusCode.BadRequest)
            }
            responseCode.equals(ERROR_CODE_INACTIVE_GC) ->{
                throw HttpResponseException(VOUCHER_CODE_DE_ACTIVE_ERROR_MESSAGE, HttpStatusCode.BadRequest)
            }
            else -> {
            throw HttpResponseException(VOUCHER_CODE_OR_VOUCHER_PIN_INCORRECT_ERROR_MESSAGE, HttpStatusCode.BadRequest)
        }
        }
    }
    private suspend fun extractMemberShipDetails(loyaltyCartResponse: LoyaltyCartResponse): Pair<String?, String?>{
        if (loyaltyCartResponse.items.epicureDetails.isBankUrl) {
            val bankUrlValues = orderRepository.bankUrlValues(BANK_URL_ID)
            if (bankUrlValues?.bankUrlValues?.any { b -> b.bankUrlName.equals(loyaltyCartResponse.items.epicureDetails.bankName) } == false) {
                throw HttpResponseException("Bank name not found", HttpStatusCode.BadRequest)
            } else {
                val memberShipPlanCode = bankUrlValues?.bankUrlValues?.first { b ->
                    b.bankUrlName.equals(loyaltyCartResponse.items.epicureDetails.bankName)
                }?.membershipPlanCode
                val  memberShipPlanName = bankUrlValues?.bankUrlValues?.first { b ->
                    b.bankUrlName.equals(loyaltyCartResponse.items.epicureDetails.bankName)
                }?.membershipPlanName
                return memberShipPlanCode to memberShipPlanName
            }
        }
        return null to null
    }

    private fun getCustomerCount(rooms: List<Room>, roomCount: Int?, customerType: String): Int {
        return when {
            roomCount == 1 -> {
                if (customerType.equals(Constants.CUSTOMER_ADULT, ignoreCase = true)) {
                    getAdultCount(rooms)
                } else {
                    getChildCount(rooms)
                }
            }
            else -> {
                val filteredRooms = rooms.filter { r -> !r.status.equals(Constants.CANCELLED, ignoreCase = true) }
                if (filteredRooms.size == roomCount) {
                    if (customerType.equals(Constants.CUSTOMER_ADULT, ignoreCase = true)) {
                        getAdultCount(rooms)
                    } else {
                        getChildCount(rooms)
                    }
                } else {
                    if (customerType.equals(Constants.CUSTOMER_ADULT, ignoreCase = true)) {
                        getAdultCount(filteredRooms)
                    } else {
                        getChildCount(filteredRooms)
                    }
                }
            }
        }
    }

    private fun getAdultCount(rooms: List<Room>): Int {
        var adultCount = 0
        for(room in rooms){
            adultCount += if(room.modifyBooking != null){
                room.modifyBooking.adult!!
            }else{
                room.adult!!
            }
        }
        return adultCount
    }

    private fun getChildCount(rooms: List<Room>): Int {
        var childCount: Int = 0
        for(room in rooms){
            childCount += if(room.modifyBooking != null){
                room.modifyBooking.children!!
            }else{
                room.children!!
            }
        }
        return childCount
    }

    private fun getMaxNoOfNights(rooms: List<Room>, roomCount: Int?): Int {
        var maxNoOfNights = 0
        if (roomCount == 1) {
            maxNoOfNights = rooms.first().noOfNights!!
        } else {
            val filteredRooms = rooms.filter { r -> !r.status.equals(CANCELLED, ignoreCase = true) }
            if (filteredRooms.isNotEmpty()) {
                maxNoOfNights = filteredRooms.maxOf { it.noOfNights!! }
            }
        }
        return maxNoOfNights
    }

    private fun endDateOfFetchBooking(arrivalDate: String?): String? {
        if (arrivalDate != null) {
            val formatter = DateTimeFormatter.ofPattern(DATE_PATTERN)
            val date = LocalDate.parse(arrivalDate, formatter)
            val newDate = date.plusMonths(prop.upcomingBookings.toLong())
            return newDate.format(formatter)
        }
        return null
    }
    suspend fun getCustomerDetails(authorization: String?): HttpResponse {
        val getCustomerDetailsUrl = prop.ssoServiceHost + GET_CUSTOMER
        try {
            val response = client.get(getCustomerDetailsUrl) {
                timeout {
                    requestTimeoutMillis = REQUEST_TIME_OUT.toLong()
                }
                headers { append(AUTHORIZATION,"$authorization") }
                contentType(ContentType.Application.Json)
            }
            log.info("response body of sso api:${response.bodyAsText()}")
            return response
        } catch (e: Exception) {
            throw Exception("Exception occurred in sso api: ${e.message} and ${e.cause}")
        }
    }
    suspend fun getUpcomingBookings(request: CustomerDetailsResponse?, mobileNumber: String?):BookingDetail{
        val reservations =LinkedHashSet<String?>()
        val  listOfEmails =LinkedHashSet<String?>()
        var upcomingBookings =LinkedHashSet<GetBookingDetailsResponse>()
        try {
            if (request?.primaryEmailId==null || mobileNumber == null) {
                throw HttpResponseException(PARAMETERS_ERROR_MESSAGE, HttpStatusCode.BadRequest)
            }else{
                val  orders = orderRepository.getListOfBookings(FetchBookingReq(request.primaryEmailId, null, null),true)
                orders.map {listOfEmails.add(it.customerEmail)}
                val coroutineJob=   CoroutineScope(Dispatchers.IO).launch {
                    val result = if (listOfEmails.isNotEmpty()) {
                        val deferredResults = listOfEmails.map { email ->
                            async {
                                makeApiRequest(FetchBookingDetailsByEmail(email, getFormattedArrivalDate()))
                            }
                        }
                        deferredResults.awaitAll()
                    } else {
                        listOf(makeApiRequest(FetchBookingDetailsByEmail(request.primaryEmailId, getFormattedArrivalDate())))
                    }
                    result.forEach { response ->
                        upcomingBookings.addAll(response)
                    }
                }
                coroutineJob.join()
                // If no bookings found with email, try with the provided mobile number
                if (upcomingBookings.all { it.data?.getHotelBookingDetails?.reservations.isNullOrEmpty() }) {
                    log.info("upcoming bookings with mobile")
                    upcomingBookings = getBookingsWithMobile(mobileNumber,listOfEmails)
                }
                upcomingBookings.forEach {
                    it.data?.getHotelBookingDetails?.reservations?.filter {itenaryNumber->reservations.add(itenaryNumber?.itineraryNumber)}
                }

                val bookings = orderRepository.getBookingDetailsByItineraryNumber(reservations,orders)
                if (bookings.isEmpty()){
                    throw HttpResponseException(ORDER_NOT_FOUND_ERROR_MESSAGE, HttpStatusCode.NotFound)
                }
                val sortOrders=bookings.sortedBy {orderRepository.convertToDesiredFormat(it.orderLineItems.first().hotel?.checkIn)}
                return BookingDetail(TotalBookingsResponse(0,LinkedHashSet(sortOrders),0, linkedSetOf()))
            }
        }catch (e: HttpResponseException) {
            throw e
        } catch (e: Exception) {
            log.error("Exception occurred while calling fetch booking API${e.message} and ${e.cause}")
            throw QCInternalServerException(e.message)
        }
    }
    private suspend fun fetchBookingByEmailAsync(email: String?, arrivalDate: String): List<GetBookingDetailsResponse> {
        return makeApiRequest(FetchBookingDetailsByEmail(email, arrivalDate))
    }
    private suspend fun fetchBookingByMobileAsync(mobileNumber: String?, arrivalDate: String): List<GetBookingDetailsResponse> {
        return makeApiRequest(FetchBookingDetailsByMobile(mobileNumber, arrivalDate))
    }
    suspend fun getUpcomingBooking(request: CustomerDetailsResponse?, mobileNumber: String?): List<BookingsResponse> {
        val upcomingBookingsByEmail = arrayListOf<Reservation?>()
        val upcomingBookingsByMobile = arrayListOf<Reservation?>()
        try {
            if (request?.primaryEmailId.isNullOrEmpty() && mobileNumber.isNullOrEmpty()) {
                throw HttpResponseException(PARAMETERS_ERROR_MESSAGE, HttpStatusCode.BadRequest)
            }
            else {
                coroutineScope {
                    val getBookingsWithEmailDeferred = async {
                        request?.primaryEmailId?.takeIf { it.isNotEmpty() }?.let {
                            fetchBookingByEmailAsync(it, getFormattedArrivalDate())
                        }
                    }
                    val getBookingsWithMobileDeferred = async {
                        mobileNumber?.takeIf { it.isNotEmpty() }?.let {
                            fetchBookingByMobileAsync(it, getFormattedArrivalDate())
                        }
                    }
                    val getBookingsWithEmail = getBookingsWithEmailDeferred.await()
                    val getBookingsWithMobile = getBookingsWithMobileDeferred.await()
                    getBookingsWithEmail?.first()?.data?.getHotelBookingDetails?.reservations?.let {
                        upcomingBookingsByEmail.addAll(it)
                    }
                    getBookingsWithMobile?.first()?.data?.getHotelBookingDetails?.reservations?.let {
                        upcomingBookingsByMobile.addAll(it)
                    }
                }
            }
            val emailReservations: List<Reservation> = upcomingBookingsByEmail.filterNotNull()
            val mobileReservations: List<Reservation> = upcomingBookingsByMobile.filterNotNull()
            var bookingList =  emailReservations + mobileReservations
            if (bookingList.isNullOrEmpty()){
                throw HttpResponseException(ORDER_NOT_FOUND_ERROR_MESSAGE, HttpStatusCode.NotFound)
            }
            bookingList = bookingList.filter { it.channels?.primaryChannel?.code.equals(CHANNEL_WEB, ignoreCase = true) && it.brand?.name.equals(BRAND_TAJ, ignoreCase = true) }
            bookingList = bookingList.filter { it.status != IGNORED_STATUS && it.status != BOOKED_STATUS }
            val uniqueBookingList = bookingList.distinctBy { it.itineraryNumber + it.crsConfirmationNumber }
            val uniqueBookingListGroup = uniqueBookingList.groupBy { it.itineraryNumber }
            val uniqueReservations = uniqueBookingListGroup.values.map { it }
            val bookings = DataMapperUtils.mapUpComingBookings(uniqueReservations)
            val upcomingBookings=bookings.distinctBy { it.orderLineItems.first().hotel.bookingNumber }
            return upcomingBookings.sortedBy {orderRepository.convertToDesiredFormat(it.orderLineItems.first().hotel.checkIn)}

        } catch (e: HttpResponseException) {
            log.error("Exception occurred while calling fetch booking API${e.message} and ${e.cause}")
            throw  HttpResponseException(ORDER_NOT_FOUND_ERROR_MESSAGE, HttpStatusCode.NotFound)
        }
    }
    suspend fun getPastBookings(request: CustomerDetailsResponse?, mobileNumber: String?): List<BookingsResponse> {
        val upcomingBookingsByEmail = arrayListOf<Reservation?>()
        val upcomingBookingsByMobile = arrayListOf<Reservation?>()
        try {
            if (request?.primaryEmailId.isNullOrEmpty()&&mobileNumber.isNullOrEmpty()) {
                throw HttpResponseException(PARAMETERS_ERROR_MESSAGE, HttpStatusCode.BadRequest)
            } else {
                coroutineScope {
                    val getBookingsWithEmailDeferred = async {
                        request?.primaryEmailId?.takeIf { it.isNotEmpty() }?.let {
                            fetchBookingByEmailAsync(it,getPastFormattedArrivalDate())
                        }
                    }

                    val getBookingsWithMobileDeferred = async {
                        mobileNumber?.takeIf { it.isNotEmpty() }?.let {
                            fetchBookingByMobileAsync(it,getPastFormattedArrivalDate())
                        }
                    }

                    val getBookingsWithEmail = getBookingsWithEmailDeferred.await()
                    val getBookingsWithMobile = getBookingsWithMobileDeferred.await()

                    getBookingsWithEmail?.first()?.data?.getHotelBookingDetails?.reservations?.let {
                        upcomingBookingsByEmail.addAll(it)
                    }
                    getBookingsWithMobile?.first()?.data?.getHotelBookingDetails?.reservations?.let {
                        upcomingBookingsByMobile.addAll(it)
                    }
                }
            }
            val emailReservations: List<Reservation> = upcomingBookingsByEmail.filterNotNull()
            val mobileReservations: List<Reservation> = upcomingBookingsByMobile.filterNotNull()
            val bookingList =  emailReservations + mobileReservations
            if (bookingList.isEmpty()){
                throw HttpResponseException(ORDER_NOT_FOUND_ERROR_MESSAGE, HttpStatusCode.NotFound)
            }
            val uniqueBookingList = bookingList.distinctBy { it.itineraryNumber + it.crsConfirmationNumber }
            val uniqueBookingListGroup = uniqueBookingList.groupBy { it.itineraryNumber }
            val uniqueReservations = uniqueBookingListGroup.values.map { it }
            val bookings = DataMapperUtils.mapUpComingBookings(uniqueReservations)
            val upcomingBookings=bookings.distinctBy { it.orderLineItems.first().hotel.bookingNumber }
            return upcomingBookings.sortedBy {orderRepository.convertToDesiredFormat(it.orderLineItems.first().hotel.checkIn)}

        } catch (e: HttpResponseException) {
            log.error("Exception occurred while calling fetch booking API${e.message} and ${e.cause}")
            throw  HttpResponseException(ORDER_NOT_FOUND_ERROR_MESSAGE, HttpStatusCode.NotFound)
        }
    }
    suspend fun getPastBooking(request: CustomerDetailsResponse?):BookingDetail{
        val mobileNumber = request?.primaryMobile?.isdCode + " " + request?.primaryMobile?.phoneNumber
        val pastBookings=  if (!request?.primaryEmailId.isNullOrEmpty()) {
            orderRepository.pastBookingRecords(FetchBookingReq(request?.primaryEmailId, "", ""),type = true)
        } else{
            orderRepository.pastBookingRecords(FetchBookingReq("", mobileNumber, ""),type = false)

        }
        val sortPastBookings=pastBookings.sortedBy {
            orderRepository.convertToDesiredFormat(it.orderLineItems.first().hotel?.checkIn)
        }
        return BookingDetail(TotalBookingsResponse(0, linkedSetOf(), pastBookings.size,LinkedHashSet(sortPastBookings)))
    }
    private suspend fun getBookingsWithMobile(mobileNumber:String?,listOfEmails:LinkedHashSet<String?>): LinkedHashSet<GetBookingDetailsResponse> {
        log.info("mobileNumber:$mobileNumber")
        val upcomingBookingRes =LinkedHashSet<GetBookingDetailsResponse>()
        val coroutineJob=  CoroutineScope(Dispatchers.IO).launch {
            val result = if (listOfEmails.isNotEmpty()) {
                val deferredResults = listOfEmails.map { email ->
                    async {
                        makeApiRequest(FetchBookingDetailsByEmail(email, getFormattedArrivalDate()))
                    }
                }
                deferredResults.awaitAll()
            } else {
                listOf(makeApiRequest(FetchBookingDetailsByEmail(mobileNumber, getFormattedArrivalDate())))
            }
            result.forEach { response ->
                upcomingBookingRes.addAll(response)
            }
        }
        coroutineJob.join()
        if (upcomingBookingRes.all { it.data?.getHotelBookingDetails?.reservations.isNullOrEmpty() }) {
            throw HttpResponseException(ORDER_NOT_FOUND_ERROR_MESSAGE, HttpStatusCode.NotFound)
        }
        return upcomingBookingRes
    }
    private fun getFormattedArrivalDate(): String {
        val currentLocalDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern(DATE_PATTERN)
        val endDate = currentLocalDate.plusYears(prop.upcomingBookings.toLong())
        log.info("upcoming bookings arrival date:${currentLocalDate.format(formatter)};${endDate.format(formatter)}")
        return "${currentLocalDate.format(formatter)};${endDate.format(formatter)}"
    }
    private fun getPastFormattedArrivalDate(): String {
        val currentLocalDate = LocalDate.now()
        val pastDate=currentLocalDate.minusYears(prop.pastBookings.toLong())
        val formatter = DateTimeFormatter.ofPattern(DATE_PATTERN)
        log.info("past bookings arrival date:${pastDate.format(formatter)};${currentLocalDate.format(formatter)}")
        return "${pastDate.format(formatter)};${currentLocalDate.format(formatter)}"
    }

    fun limitBookings(bookings: List<Order>?, limit: Int?): List<Order>? {
        return if (limit != null && limit < (bookings?.size ?: 0)) {
            bookings?.take(limit)
        } else {
            bookings
        }
    }
    fun limitBooking(bookings: List<BookingsResponse>, limit: Int): List<BookingsResponse> {
        val limit= if (limit < (bookings.size)) {
            bookings.take(limit)
        } else {
            bookings
        }
        return limit
    }
    private suspend fun makeApiRequest(body: Any): ArrayList<GetBookingDetailsResponse> {
        val fetchBookingDetailsURL = prop.hudiniServiceHost.plus(FETCH_BOOKING_DETAILS)
        try {
            val response = client.post(fetchBookingDetailsURL) {
                timeout {
                    requestTimeoutMillis = REQUEST_TIME_OUT.toLong()
                }
                contentType(ContentType.Application.Json)
                setBody(body)
            }
            log.info("response body of fetch booking details:${response.bodyAsText()}")
            val upcomingBookingResponse=response.body<GetBookingDetailsResponse>()
            return  arrayListOf(upcomingBookingResponse)
        }catch (e:Exception){
            log.error("Exception occurred while calling hudini Api${e.message} and ${e.cause}")
            throw QCInternalServerException(e.message)
        }
    }
    fun bookingCount(response:List<Order>?): Int {
        log.info("upcomingCount response ${response?.json}")
        var count = 0
        response?.forEach { upComingBooking ->
            if (upComingBooking.channel != "NON-WEB") {
                upComingBooking.orderLineItems.forEach { orderLineItem ->
                    if (orderLineItem.hotel?.status.equals(HOTEL_BOOKING_CONFIRMED_STATUS,ignoreCase = true)||orderLineItem.hotel?.status.equals(PARTIALLY_CANCELLED,ignoreCase = true)) {
                        count++
                    }
                }
            }
        }
        return count
    }

    suspend fun getBookingsWithItineraryNumber(req:ItineraryNumber): Any {
        val bookingsDetails=HashSet<GetBookingDetailsResponse>()
        val uniqueBookings=HashSet<Order>()
        val reservations = ArrayList<ItineraryNumber>()
        val bookingDetailsResponses=makeApiRequest(ItineraryNumber(req.itineraryNumber))
        bookingsDetails.addAll(bookingDetailsResponses)
        bookingsDetails.forEach {
            if ( it.data?.getHotelBookingDetails?.reservations.isNullOrEmpty()){
                throw HttpResponseException(ORDER_NOT_FOUND_FAILED_ERR_MSG, HttpStatusCode.NotFound)
            }
            it.data?.getHotelBookingDetails?.reservations?.map {
                reservations.add(ItineraryNumber(it?.itineraryNumber))
            }
        }
        val bookings=orderRepository.getBookingDetailsByItineraryNumber(reservations)
        if (bookings.isEmpty()) {
            return DataMapperUtils.mapGetBookingDetails(bookingsDetails)
        }
        for (records in bookings){
            uniqueBookings.add(records)
        }
        return  uniqueBookings
    }

    private suspend fun modifyUpdateBookingImp(order: Order, status: String): Map<Int?, String> {
        val roomNumberList = ArrayList<Int?>()
        val confirmationIdList = ArrayList<String>()
        val paymentStringList = paymentString(order)
        val roomSize = order.orderLineItems[0].hotel!!.rooms!!.size
        for (room in 0 until roomSize) {
            if (order.orderLineItems.first().hotel?.rooms?.get(room)?.status != ROOM_STATUS_CANCELLED &&
                (order.orderLineItems.first().hotel?.rooms?.get(room)?.modifyBooking != null &&
                        order.orderLineItems.first().hotel?.rooms?.get(room)?.modifyBooking?.status == PENDING)
            ) {
                val updateBookingRequest =
                    DataMapperUtils.mapModifiedUpdateBookingRequest(order, room, status, paymentStringList[room])
                roomNumberList.add(order.orderLineItems.first().hotel?.rooms?.get(room)?.roomNumber)
                confirmationIdList.add(hudiniUpdateBookingImp(updateBookingRequest))
            }
        }
        val confirmation = roomNumberList.zip(confirmationIdList).toMap()
        return confirmation
    }

    private suspend fun modifyGetBookingDetails(order: Order): Order {
        val adultCount = getCustomerCount(order.orderLineItems[0].hotel?.rooms!!,order.orderLineItems[0].hotel?.roomCount,Constants.CUSTOMER_ADULT)
        order.orderLineItems[0].hotel?.adultCount = adultCount
        val childCount = getCustomerCount(order.orderLineItems[0].hotel?.rooms!!,order.orderLineItems[0].hotel?.roomCount,Constants.CUSTOMER_CHILD)
        order.orderLineItems[0].hotel?.childrens = childCount

        orderRepository.findOneAndUpdateOrder(order.orderId, order)
        return getDates(order)
    }

    private fun deleteCacheCollection(order: Order){
        val mobileNumber= order.orderLineItems.first().hotel?.rooms?.first()?.travellerDetails?.first()?.mobile
        val emailId= order.orderLineItems.first().hotel?.rooms?.first()?.travellerDetails?.first()?.email
        val redisKey= "$emailId||$mobileNumber"
        log.info("redis key is$redisKey")
        RedisConfig.deleteFromRedis(RedisDto(redisKey))
    }
    suspend fun updateConfirmOrderForBooking(){
        val bookingOrders =  orderRepository.findHotelBookingOrders(PENDING, PAY_ONLINE, listOf(OrderType.HOTEL_BOOKING, OrderType.HOLIDAYS))
        bookingOrders.forEach {
            log.info("orders received from DB: HOTEL_BOOKING ${it.orderId}")
            orderRetry(it)
        }
    }

    private suspend fun orderRetry(order: Order){
        try {
            orderStatusPaymentResponse(order, ConfirmOrder(order.orderId), null, true)
            val updatedOrder = orderRepository.findOrderByOrderId(order.orderId)
            updatedOrder.retryCount += 1
            orderRepository.findOneAndUpdateOrder(updatedOrder.orderId, updatedOrder)
            bookingAndPaymentUpdate(updatedOrder)
        }catch (e: Exception){
            log.info("Exception ${e.message} and ${e.cause}")
            order.retryCount += 1
            orderRepository.findOneAndUpdateOrder(order.orderId, order)
        }
    }

    private suspend fun bookingAndPaymentUpdate(order: Order){
        if(order.orderType == OrderType.HOTEL_BOOKING || order.orderType == OrderType.HOLIDAYS) {
            val rooms = order.orderLineItems.first().hotel?.rooms
            if (!rooms.isNullOrEmpty()) {
                if (order.retryCount == prop.bookingRetryCount) {
                    if (!rooms.any { s -> s.status.equals(CONFIRMED, ignoreCase = true) }) {
                        rooms.forEach {
                            it.status = FAILED
                        }
                        order.orderLineItems.first().hotel?.status = FAILED
                        order.orderStatus = FAILED
                    }
                    val updatedOrder = orderRepository.findOrderByOrderId(order.orderId)
                    orderRepository.findOneAndUpdateOrder(order.orderId, updatedOrder)
                }
            }
        }
    }

    private suspend fun voucherRedemption(order: Order): Boolean{
        var isVoucherRedeemed = false
        if(order.paymentStatus.equals(CHARGED, ignoreCase = true) && (order.orderLineItems.first().loyalty?.isShareHolder == true ||
                    order.orderLineItems.first().loyalty?.isTata == true ||
                    order.orderLineItems.first().loyalty?.isBankUrl == true)
        ) {
            val planId = order.orderLineItems.first().loyalty?.membershipDetails?.memberId.toString()
             isVoucherRedeemed = gravityVoucherRedeem(
                order.orderLineItems.first().loyalty?.gravityVoucherCode,
                order.orderLineItems.first().loyalty?.gravityVoucherPin, order.orderId, planId, 0.0
            )
            if (!isVoucherRedeemed) {
                order.orderStatus = PENDING
                orderRepository.findOneAndUpdateOrder(order.orderId, order)
                throw HttpResponseException(VOUCHER_REDEMPTION_FAILED_ERR_MSG, HttpStatusCode.NotAcceptable)

            }
        }
        return isVoucherRedeemed
    }
    private suspend fun voucherRedeem(order: Order): Any{
        var isVoucherRedeem = false
        log.info("voucher redeem orderId ${order.orderId}")
        val voucherNumber = order.orderLineItems.first().hotel?.voucherNumber
        val voucherPin = order.orderLineItems.first().hotel?.voucherPin
        log.info("voucher redeem number and pin ${voucherNumber}")
        if (order.paymentStatus.equals(CHARGED,ignoreCase = true) && !voucherNumber.isNullOrEmpty() && !voucherPin.isNullOrEmpty()){
             isVoucherRedeem = gravityVoucherRedeem(
                order.orderLineItems.first().hotel?.voucherNumber,
                order.orderLineItems.first().hotel?.voucherPin, order.orderId,order.orderLineItems.first().hotel?.bookingNumber.toString(),order.gradTotal
            )
            if (!isVoucherRedeem) {
                order.orderStatus = PENDING
                orderRepository.findOneAndUpdateOrder(order.orderId, order)
                return DataMapperUtils.mapConfirmHotelBookingResponse(order, paymentTransactionId(order.orderType, order.paymentDetails?.transaction_1?.toList()))

            }
        }
        return isVoucherRedeem
    }

    private suspend fun  voucherCheckBalance(cardNumber: String?,cardPin: String?) {
        val checkBalanceResponse = balanceEnquiryForVoucher(cardNumber,cardPin)
        log.info("voucher code type:${checkBalanceResponse.Cards?.first()?.CardType} and ${checkBalanceResponse.Cards?.first()?.ResponseCode}")
        when(checkBalanceResponse.Cards?.first()?.ResponseCode){
            VOUCHER_CODE_OR_VOUCHER_PIN_EXPIRED->{checkBalanceResponse.Cards.first().ResponseMessage=ERROR_RESPONSE_MESSAGE_OF_VOUCHER}
            VOUCHER_CODE_DE_ACTIVE_CODE->{checkBalanceResponse.Cards.first().ResponseMessage=VOUCHER_CODE_DE_ACTIVE_ERROR_MESSAGE}
            VOUCHER_CODE_INCORRECT->{checkBalanceResponse.Cards.first().ResponseMessage=VOUCHER_CODE_OR_VOUCHER_PIN_INCORRECT_ERROR_MESSAGE}
            VOUCHER_PIN_INCORRECT->{ checkBalanceResponse.Cards.first().ResponseMessage=VOUCHER_CODE_OR_VOUCHER_PIN_INCORRECT_ERROR_MESSAGE }
        }
        log.info("response of check balance$checkBalanceResponse")
        if (checkBalanceResponse.Cards?.first()?.ResponseCode!="0"){
            throw HttpResponseException(checkBalanceResponse.Cards?.get(0)?.ResponseMessage.toString(), HttpStatusCode.NotAcceptable)
        }
        val cardType = checkBalanceResponse.Cards?.first()?.CardType?: throw HttpResponseException(checkBalanceResponse.Cards?.first()?.ResponseMessage.toString(), HttpStatusCode.NotAcceptable)
        val giftCardDetails = orderRepository.getGiftCardDetails(CPG_ID)
        val matchingGiftCard = giftCardDetails.any { giftCardDetail ->
            giftCardDetail.giftCardValues?.any { it.cardType.equals(cardType, ignoreCase = true)&&it.booking==CUG_OFFER_TYPE} == true
        }
        if (!matchingGiftCard) {
            throw HttpResponseException(VOUCHER_CARDTYPE_ERROR_MESSAGE, HttpStatusCode.NotAcceptable)
        }
        if (checkBalanceResponse.Cards.first().Balance == VOUCHER_REDEEM) {
            log.info("Success response of check balance")
        } else {
            throw HttpResponseException(VOUCHER_CHECK_BALANCE_ERROR_MESSAGE, HttpStatusCode.NotAcceptable)
        }
    }

    private suspend fun getBookingDetailsByItinerary(fetchBookingDetails: FetchBookingDetailsRequest): GetBookingDetailsResponse? {
        log.info("Fetch booking details by itinerary : $fetchBookingDetails")
        try {
            val fetchBookingDetailsURL = prop.hudiniServiceHost.plus(FETCH_BOOKING_DETAILS)
            val response = client.post(fetchBookingDetailsURL) {
                timeout {
                    requestTimeoutMillis = REQUEST_TIME_OUT.toLong()
                }
                contentType(ContentType.Application.Json)
                setBody(fetchBookingDetails)
            }
            log.info("Get booking details response ${response.bodyAsText()} response status ${response.status}")
            return when (response.status) {
                HttpStatusCode.OK -> {
                    response.body<GetBookingDetailsResponse>()
                }
                else -> {
                    null
                }
            }
        }catch (e: Exception){
            log.info("Get booking details API Failed")
            return null
        }

    }
    private suspend fun callSebBookings(order:Order, confirmationIds:List<String?>){
        log.info("call seb bookings")
        val crsReferenceNumber: String = confirmationIds.joinToString(", ") + ", " + order.orderLineItems[0].hotel?.synxisId
        log.info("crsReferenceNumber to SEB: ${crsReferenceNumber}")
        val body = SEBRequest(
            myTajREQID = order.orderLineItems[0].hotel?.sebRequestId,
            startDate = order.orderLineItems[0].hotel?.checkIn,
            endDate = order.orderLineItems[0].hotel?.checkOut,
            approvedRooms = order.orderLineItems[0].hotel?.roomCount.toString(),
            crsReferenceNumber = crsReferenceNumber,
            crsTimeStamp = order.createdTimestamp.toString(),
            emailID = order.customerEmail,
            mobileNumber = order.customerMobile.substringAfter(" "),
            numberOfPerson = order.orderLineItems[0].hotel?.adultCount.toString(),
            crsRequestID = crsReferenceNumber,
            hotelName = order.orderLineItems[0].hotel?.name)
        log.info("SEB request ${body.json}")
        try {
            val response: HttpResponse =
                client.post(prop.hudiniServiceHost.plus(SEB_BOOKINGS)) {
                    timeout {
                        requestTimeoutMillis = REQUEST_TIME_OUT.toLong()
                    }
                    contentType(ContentType.Application.Json)
                    setBody(body)
                }
            log.info("SEB status : ${response.status} SEB response: ${response.bodyAsText()} ")
        } catch (e: Exception) {
            log.error("Exception occurred while calling SEB API is ${e.message} due to ${e.stackTrace} cause: ${e.cause}")
        }
    }
    fun bookingsCount(orders: List<BookingsResponse>): Int{
        val confirmedOrdersCount = orders.count { order ->
            order.orderLineItems.any { orderLineItem ->
                orderLineItem.hotel.rooms!!.any { room ->
                    room.status.equals( CONFIRMED, ignoreCase = true)
                }
            }
        }
        log.info("confirmedOrdersCount: $confirmedOrdersCount")

        return confirmedOrdersCount
    }

    private fun paymentTransactionId(orderType: OrderType, paymentDetails: List<PaymentDetail>?): String {
        val paymentTransaction = ArrayList<String>()

        if (!paymentDetails.isNullOrEmpty()) {
            when {
                paymentDetails.size > 1 -> processMultiplePayments(orderType, paymentDetails, paymentTransaction)
                else -> processSinglePayment(paymentDetails.first(), paymentTransaction)
            }
        }

        return paymentTransaction.joinToString(" | ")
    }

    private fun processMultiplePayments(orderType: OrderType, paymentDetails: List<PaymentDetail>, paymentTransaction: MutableList<String>) {
        when (orderType) {
            OrderType.HOTEL_BOOKING, OrderType.HOLIDAYS -> {
                val jusPayTransactionId = paymentDetails.find { it.paymentType.equals(JUS_PAY, ignoreCase = true) }?.ccAvenueTxnId
                if (!jusPayTransactionId.isNullOrEmpty()) {
                    paymentTransaction.add("PG:$jusPayTransactionId")
                }
                val gcTransactionIds = gcPaymentDetails(paymentDetails.filter { it.paymentType.equals(GIFT_CARD, ignoreCase = true) })
                if(!gcTransactionIds.isNullOrEmpty()) {
                    paymentTransaction.add("TEGC:$gcTransactionIds")
                }
                val neuCoinsTransactionId = paymentDetails.find { it.paymentType.equals(TATA_NEU, ignoreCase = true) }?.redemptionId
                if(!neuCoinsTransactionId.isNullOrEmpty()){
                    paymentTransaction.add("Neucoins:$neuCoinsTransactionId")
                }
            }
            else -> paymentDetails.forEach {
                when {
                    it.paymentType.equals(JUS_PAY, ignoreCase = true) -> paymentTransaction.add("PG:${it.ccAvenueTxnId}")
                    it.paymentType.equals(TATA_NEU, ignoreCase = true) -> paymentTransaction.add("Neucoins:${it.redemptionId}")
                }
            }
        }
    }

    private fun processSinglePayment(paymentDetail: PaymentDetail, paymentTransaction: MutableList<String>) {
        when (paymentDetail.paymentType) {
            JUS_PAY -> {
                if (!paymentDetail.ccAvenueTxnId.isNullOrEmpty()) {
                    paymentTransaction.add("PG: ${paymentDetail.ccAvenueTxnId}")
                }
            }
            CC_AVENUE -> {
                if (!paymentDetail.txnId.isNullOrEmpty()) {
                    paymentTransaction.add("PG: ${paymentDetail.txnId}")
                }
            }
            TATA_NEU -> paymentTransaction.add("Neucoins : ${paymentDetail.redemptionId}")
            else -> paymentTransaction.add("TEGC: ${paymentDetail.transactionId.toString()}")
        }
    }

    private fun gcPaymentDetails(paymentDetails: List<PaymentDetail>): String {
        return paymentDetails.joinToString(",") { it.transactionId.toString() }
    }
    private fun setBrandName(order: Order,brandName:String?){
        if (brandName != null && brandName.contains(Constants.BRAND_GATEWAY)) {
            order.brandName = Constants.BRAND_GATEWAY
        }
        else{
            order.brandName = BRAND_TAJ
        }
    }

    suspend fun updateConfirmOrderForTEGCEpicure(){
        val orders =  orderRepository.findOrderForTEGCEpicure(PENDING, listOf(OrderType.GIFT_CARD_PURCHASE, OrderType.RELOAD_BALANCE, OrderType.MEMBERSHIP_PURCHASE))
        orders.forEach {
            log.info("Orders picked in cron job is: ${it.orderId}")
            orderRetry(it)
        }
    }

    //Entry will be created in Redis Cache, 'accessToken' & 'RefreshToken' will be stored against the 'orderId' as Key
    private fun createRedisEntry(order: Order, accessToken: String, refreshToken: String){
        val orderId: String = order.orderId
        if(accessToken.isNotEmpty() && refreshToken.isNotEmpty()) {
            val redisValue = "$accessToken||$refreshToken"
            val expiryInSeconds: Long = when (order.orderType) {
                OrderType.GIFT_CARD_PURCHASE, OrderType.MEMBERSHIP_PURCHASE ->  176400 // 2 Days and 1 Hour in seconds
                else -> 2100 // 35 minutes in seconds
            }
            log.info("Storing the redis key as $orderId and value as $redisValue")
            RedisConfig.setKeyAndTTL(orderId, redisValue, expiryInSeconds)
        }else{
            log.info("For the order $orderId, accessToken-$accessToken and refreshToken-$refreshToken")
        }
    }

    //Deleting the entry from the Redis after successfully redeeming the neucoins.
    private fun deleteRedisEntry(orderId: String){
        if (RedisConfig.deleteFromRedis(RedisDto(orderId))) {
            log.info("successfully deleted the accessToken data For the order $orderId")
        }else{
            log.info("Failed to delete the accessToken data For the order $orderId")
        }
    }

    //Accepts 'orderId' and fetches the 'accessToken' and 'refreshToken' from the cache and returns the 'accessToken'(jwtToken).
    private suspend fun getAuthToken(orderId: String): String?{
        val tokens = RedisConfig.getKey(orderId)
        if(tokens.isNullOrEmpty()){
            log.info("Received tokens as $tokens from Redis Cache for the orderId = $orderId")
            return null
        }
        val tokenArray = tokens.split("||", limit = 2)
        val accessToken = tokenArray.getOrElse(0) { "" }
        val refreshToken = tokenArray.getOrElse(1) { "" }

        if(accessToken.isEmpty() || refreshToken.isEmpty()){
            log.info("Received access token = $accessToken and refresh token = $refreshToken from Redis Cache for the orderId = $orderId")
            return null
        }
        return callRefreshAuthToken(accessToken, refreshToken)
    }

    //This fun calls the SSO refresh-token API to activate the 'accessToken'. If API returns message as 'success' then 'accessToken' will be returned
    private suspend fun callRefreshAuthToken(accessToken: String, refreshToken: String): String?{
        try {
            val refreshTokenUrl = prop.ssoServiceHost.plus(REFRESH_SSO_TOKEN_URL)
            val response = client.get(refreshTokenUrl) {
                headers {
                    append(AUTHORIZATION, refreshToken)
                }
                contentType(ContentType.Application.Json)
            }
            log.info("response received from refreshToken api is ${response.bodyAsText()}")
            val refreshTokenResponse = response.body<RefreshTokenResponse>()
            if(response.status == HttpStatusCode.OK && refreshTokenResponse.message.equals("success")){
                return accessToken
            }
        }catch (e: Exception){
            log.error("Exception occurred while calling refreshToken API as ${e.message}: ${e.cause}")
        }
        return null
    }
    suspend fun validateOfferCondition(authorization: String?, offerIdentifier:String): Boolean {
        val fetchNeuCoins = prop.loyaltyServiceHost + FETCH_NEUCOINS
        val loyaltyResponse = client.post(fetchNeuCoins) {
            headers {
                append(AUTHORIZATION, authorization!!)
            }
        }
        log.info("Loyality response$loyaltyResponse")
        val sanityResponse = client.get(prop.sanityHost + PRE_QUERY + offerIdentifier + POST_QUERY)
        log.info("Sanity response$loyaltyResponse")
        if (loyaltyResponse.status == HttpStatusCode.OK && sanityResponse.status == HttpStatusCode.OK) {
                val loyaltyResult = loyaltyResponse.body() as LoyaltyProgram
                val sanityResult = sanityResponse.body() as OfferValidationResponse
                return if(sanityResult.result.isEmpty()){
                    throw HttpResponseException("Invalid Offer Identifier provided",HttpStatusCode.PreconditionFailed)
                }
                else if (loyaltyResult.groupLoyaltyProgramDetails[0].loyaltyPoints < (sanityResult.result[0].minNeucoinsCap
                        ?: 0.0)
                ) {
                    throw HttpResponseException(
                        "Insufficient NeuCoins To Redeem Offer ",
                        HttpStatusCode.PreconditionFailed
                    )
                } else
                    true
            } else {
                throw HttpResponseException("Confirm booking failed", HttpStatusCode.InternalServerError)
            }
    }

    //creating a request body to redeem neu-coins
    private fun createRedemptionRequest(externalReferenceNumber: String?, pointsRedeemed: String?, transactionNumber: String?, order: Order): RedeemNeuCoinsRequest{
        val sourceField = Field(name = CUSTOM_FIELD_SOURCE,value = CUSTOM_FIELD_SOURCE_VALUE)
        val customFieldList = mutableListOf(sourceField)
        when {
            order.orderType == OrderType.HOTEL_BOOKING || order.orderType == OrderType.HOLIDAYS -> {
                val productType = Field(name = CUSTOM_FIELD_PRODUCT_TYPE, value = PRODUCT_TYPE_HOTEL)
                val checkIn = order.orderLineItems[0].hotel?.checkIn
                val checkOut = order.orderLineItems[0].hotel?.checkOut
                if (!checkIn.isNullOrEmpty() && !checkOut.isNullOrEmpty()) {
                    val checkInDate = Field(CUSTOM_FIELD_CHECK_IN_DATE, checkIn)
                    val checkOutDate = Field(CUSTOM_FIELD_CHECK_OUT_DATE, checkOut)
                    customFieldList.addAll(listOf(checkInDate, checkOutDate))
                }
                customFieldList.add(productType)
            }

            order.orderType == OrderType.GIFT_CARD_PURCHASE -> {
                val productType = Field(name = CUSTOM_FIELD_PRODUCT_TYPE, value = PRODUCT_TYPE_GIFT_CARD)
                customFieldList.add(productType)
            }

            order.orderType == OrderType.MEMBERSHIP_PURCHASE -> {
                val productType = Field(name = CUSTOM_FIELD_PRODUCT_TYPE, value = PRODUCT_TYPE_MEMBERSHIP)
                customFieldList.add(productType)
            }
        }

        return RedeemNeuCoinsRequest(
            CustomFields(customFieldList),
            externalReferenceNumber,
            pointsRedeemed,
            transactionNumber
        )
    }

}
