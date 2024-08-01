package com.ihcl.order.route.v1


import com.ihcl.order.config.RedisConfig.deleteFromRedis
import com.ihcl.order.model.dto.request.*
import com.ihcl.order.model.dto.response.*
import com.ihcl.order.model.exception.HttpResponseException
import com.ihcl.order.model.schema.*
import com.ihcl.order.repository.OrderRepository
import com.ihcl.order.service.v1.OrderService
import com.ihcl.order.utils.*
import com.ihcl.order.utils.Constants.COUNTRY_NAME
import com.ihcl.order.utils.Constants.DEFAULT_LIMIT
import com.ihcl.order.utils.Constants.INVALID_TYPE_ERROR_MESSAGE
import com.ihcl.order.utils.Constants.INVALID_TOKEN
import com.ihcl.order.utils.Constants.LIMIT
import com.ihcl.order.utils.Constants.STATE
import com.ihcl.order.utils.ValidatorUtils.validateDeleteBooking
import com.ihcl.order.utils.ValidatorUtils.validateFetchBooking
import com.ihcl.order.utils.ValidatorUtils.validateOldOrderResponse
import com.ihcl.order.utils.ValidatorUtils.validateOrderTypeResponse
import com.ihcl.order.utils.ValidatorUtils.validateOverviewResponse
import com.ihcl.order.utils.ValidatorUtils.validatePastBooking
import com.ihcl.order.utils.ValidatorUtils.validateRequestBody
import com.ihcl.order.utils.ValidatorUtils.validateUserInput
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.java.KoinJavaComponent
import org.slf4j.Logger
import org.slf4j.LoggerFactory



fun Application.configureOrderRouting() {
    val orderService by KoinJavaComponent.inject<OrderService>(OrderService::class.java)
    val orderRepository by KoinJavaComponent.inject<OrderRepository>(OrderRepository::class.java)
    val log: Logger = LoggerFactory.getLogger(javaClass)



    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)
        allowHeader("CUSTOMERHASH")
        allowHeader("JWTTOKEN")
        exposeHeader("CUSTOMERHASH")
        exposeHeader("JWTTOKEN")
        allowHeader("ORDERID")
        exposeHeader("ORDERID")
        allowHeader("NEUCOINAUTHORIZATION")
        exposeHeader("NEUCOINAUTHORIZATION")
        allowHeader("authorization")
        exposeHeader("authorization")
        allowHeader("GUESTUSER")
        exposeHeader("GUESTUSER")
        allowHeader("GRAVTY_MEMBER_ID")
        exposeHeader("GRAVTY_MEMBER_ID")
        allowHeader("memberId")
        exposeHeader("memberId")
        allowHeader("Authorization")
        exposeHeader("Authorization")
        allowHeader("type")
        exposeHeader("type")
    }

    routing {
        route("/v1") {
            post("/orders/create-order/booking") {
                log.info("calling create order booking api")
                val customerHash = call.request.headers[CUSTOMERHASH]
                val accessToken = call.request.headers[AUTHORIZATION] ?: EMPTY_STRING
                val refreshToken = call.request.headers[REFRESH_TOKEN] ?: EMPTY_STRING
                var referer = call.request.headers[Constants.HEADER_ORIGIN]
                log.info("request received from $referer")
                if(referer.isNullOrEmpty()) {
                     referer = call.request.headers[Constants.HEADER_TENANT]
                }
                log.info("Received final referer is  $referer")
                customerHash?.let {
                    val travelerDetails = call.receive() as TravelerDto
                    log.info("Received request body as $travelerDetails")
                    validateRequestBody(validateTravelerDtoRequest.validate(travelerDetails))
                    val response = orderService.createOrder(customerHash, travelerDetails,referer, accessToken, refreshToken)
                    log.debug("response body of creating order for cart {} ", response)
                    call.respond(response)
                } ?: run {
                    call.fireHttpResponse(HttpStatusCode.UnprocessableEntity, CUSTOMERHASH_REQUIRED_ERR_MSG)
                }
            }
            delete("/orders/delete-order") {
                 val orderId = call.request.headers[ORDERID]
                 orderId?.let {
                    log.info("getting header $orderId")
                    val response = orderService.deleteOrder(orderId)
                    log.info("response body after deletion$response")
                    call.respond(response)
                } ?: run {
                    call.fireHttpResponse(HttpStatusCode.UnprocessableEntity, ORDER_ID_REQUIRED_ERR_MSG)
                }

            }

            get("/orders/fetch-order") {
                log.info("order details")
                val orderId = call.request.queryParameters[ORDERID]
                val emailId=call.request.queryParameters[EMAILID]
                validateUserInput(orderId,emailId)
                call.respond(orderService.getOrder(orderId,emailId))
            }

            post("/orders/create-order/gift-card") {
                log.info("gift card purchase")
                val customerHash = call.request.headers[CUSTOMERHASH]
                val accessToken = call.request.headers[AUTHORIZATION] ?: EMPTY_STRING
                val refreshToken = call.request.headers[REFRESH_TOKEN] ?: EMPTY_STRING
                var referer = call.request.headers[Constants.HEADER_ORIGIN]
                log.info("request received from $referer")
                if(referer.isNullOrEmpty()) {
                    referer = call.request.headers[Constants.HEADER_TENANT]
                }
                log.info("Received final referer is  $referer")
                customerHash?.let {
                    val response = orderService.createOrderForGiftCard(customerHash, call.receive<GCCreateOrderRequest>(),referer, accessToken, refreshToken)
                    call.respond(response)
                    } ?: run {
                        call.fireHttpResponse(HttpStatusCode.UnprocessableEntity, CUSTOMERHASH_REQUIRED_ERR_MSG)

                }
            }

            post("/orders/confirm-order") {
                log.info("Confirm order")
                val jwtToken = call.request.headers[JWTTOKEN]
                val customerHash=call.request.headers[CUSTOMERHASH]
                log.info("jwt token received is $jwtToken")
                jwtToken?.let {
                    val response = orderService.confirmPayment(call.receive() as ConfirmOrder, jwtToken, customerHash)
                    call.respond(response!!)
                } ?: run {
                    val response = orderService.confirmPayment(call.receive() as ConfirmOrder, null,customerHash)
                    call.respond(response!!)
                }


            }
            post("/orders/create-order/gc-reload") {
                log.info("gift card reload")
                val customerHash = call.request.headers[CUSTOMERHASH]
                var referer = call.request.headers[Constants.HEADER_ORIGIN]
                log.info("request received from $referer")
                if(referer.isNullOrEmpty()) {
                    referer = call.request.headers[Constants.HEADER_TENANT]
                }
                log.info("Received final referer is  $referer")
                suspend fun createOrderForReloadBal(hash: String?) {
                    val request=call.receive<ReloadBalRequest>()
                    validateRequestBody(validateReloadBalRequest.validate(request))
                    hash?.let {
                        val response =
                            orderService.createOrderForReloadGiftCard(hash,request,referer)
                        call.respond(response)
                    } ?: call.fireHttpResponse(HttpStatusCode.UnprocessableEntity, CUSTOMERHASH_REQUIRED_ERR_MSG)
                }
                if (customerHash.isNullOrEmpty()) {
                    val guestUserId = orderService.generatingGuestUser()
                    call.response.headers.append(GUESTUSER, guestUserId)
                    createOrderForReloadBal(guestUserId)

                    } else createOrderForReloadBal(customerHash)
            }
            post("/orders/create-order/loyalty") {
                val customerHash = call.request.headers[CUSTOMERHASH]
                var referer = call.request.headers[Constants.HEADER_ORIGIN]
                val accessToken = call.request.headers[AUTHORIZATION] ?: EMPTY_STRING
                val refreshToken = call.request.headers[REFRESH_TOKEN] ?: EMPTY_STRING
                log.info("request received from $referer")
                if(referer.isNullOrEmpty()) {
                    referer = call.request.headers[Constants.HEADER_TENANT]
                }
                log.info("Received final referer is  $referer")
                val loyaltyRequest = call.receive() as LoyaltyCreateOrderRequest
                validateRequestBody(validateCreateOrder.validate(loyaltyRequest))
                customerHash?.let {
                    val response = orderService.createOrderForLoyalty(customerHash, loyaltyRequest,referer, accessToken, refreshToken)
                    call.respond(response)
                }?: run {
                    call.fireHttpResponse(HttpStatusCode.UnprocessableEntity, CUSTOMERHASH_REQUIRED_ERR_MSG)
                }
            }
            get("/my-accounts/overview") {
                val limit = call.request.queryParameters[LIMIT] ?: DEFAULT_LIMIT
                val authorization = call.request.headers[AUTHORIZATION] ?: throw HttpResponseException(INVALID_TOKEN, HttpStatusCode.BadRequest)
                val res = orderService.getCustomerDetails(authorization)
                validateOverviewResponse(call,res,limit)
            }
            get("/orders/type") {
                val customerHash = call.request.headers[CUSTOMERHASH]
                val userOrderType = call.request.queryParameters["orderType"]
                val limit = call.request.queryParameters["limit"] ?: DEFAULT_LIMIT
                val getOrders = customerHash?.let { it1 -> orderService.getOrdersByCustomerHash(it1) }
                    ?.sortedByDescending { it.createdTimestamp }
                log.info("getting list of orders from db.....$getOrders")
                validateOrderTypeResponse(getOrders,userOrderType,limit,call)
            }
            post("/orders/update-order") {
                val type=call.request.headers[CATEGORY_TYPE]
                 type?.let {
                    val response = orderService.updateOrder(type,call.receive() as UpdateOrderRequest)
                    call.respond(response as Any)
                }?:run {throw  HttpResponseException(INVALID_TYPE_ERROR_MESSAGE, HttpStatusCode.BadRequest)}
            }
            put("/orders/update-processing-gc"){
                orderService.updateGC()
                call.respond("UpdateGC process completed successfully")
            }
            post ("/orders/save-gc-values"){
                val req=call.receive<GiftCardValues>()
                orderRepository.saveGiftCardValueDetails(req)
                call.respond("successfully inserted")
            }
            post("/orders/fetch-order-details") {
                val response=  orderRepository.getBookingDetails(call.receive() as FetchBookingDetailsDto)
                call.respond(response)
            }
            post ("/orders/update-old-orders"){
                val request = call.receive<Order>()
                val orders = orderRepository.findMultipleOrders(request.orderId)
                validateOldOrderResponse(orders)
                call.respond("Orders updated successfully")
            }
            post ("/orders/add-tender-mode"){
                val response=orderService.addTenderModeImpl(call.receive() as TenderModeRequest)
                call.respond(response)
            }
            post("/orders/delete-tender-mode") {
                val response=orderService.deleteTenderModeImpl(call.receive() as TenderModeRequest)
                 call.respond(response)
            }
            post("/orders/save-bank-url-values") {
                orderRepository.saveBankUrlValueDetails(call.receive() as BankUrlValuesRequest)
                call.respond("successfully inserted")
            }
            post("/orders/update-order/booking"){
                val cart = call.receive<OrderLineItems>()
                val orderId = call.request.headers[ORDERID]
                val paymentMethod = call.request.headers[PAYMENTMETHOD]
                val order = orderRepository.findOrderByOrderId(orderId!!)
                call.respond(orderService.updateOrderBooking(cart,order,paymentMethod.toString()))
            }
            get("/countries") {
                  val countryName=call.request.queryParameters[COUNTRY_NAME]
                  val stateName=call.request.queryParameters[STATE]
                  val response=orderRepository.getCountryDetails(countryName,stateName)
                  log.info("response of country details API is$response")
                  call.respond(response as Any)
            }
            post("/orders/fetch-bookings-itinerary") {
                val request=call.receive<ItineraryNumber>()
                val response=orderService.getBookingsWithItineraryNumber(request)
                log.info("response body of bookings is$response")
                call.respond(response)
            }
            post("/orders/delete-bookings") {
                val bookingDetails = call.receive<RedisDto>()
                val deletionResult = deleteFromRedis(bookingDetails)
                validateDeleteBooking(deletionResult,call,bookingDetails)
            }
            put ("/orders/update-confirm-order"){
                orderService.updateConfirmOrderForBooking()
                call.respond("Booking orders payment retry completed successfully")
            }
            get("/orders/fetch-bookings") {
                val limit = call.request.queryParameters[LIMIT] ?: DEFAULT_LIMIT
                val authorization = call.request.headers[AUTHORIZATION] ?: throw HttpResponseException(INVALID_TOKEN, HttpStatusCode.BadRequest)
                val res = orderService.getCustomerDetails(authorization)
                validateFetchBooking(res,call,limit)
            }
            get("/orders/past-bookings") {
                val limit = call.request.queryParameters[LIMIT] ?: DEFAULT_LIMIT
                val authorization = call.request.headers[AUTHORIZATION] ?: throw HttpResponseException(INVALID_TOKEN, HttpStatusCode.BadRequest)
                val res = orderService.getCustomerDetails(authorization)
                validatePastBooking(res,call,limit)
            }
            post ("/orders/cron-tegc-epicure"){
                orderService.updateConfirmOrderForTEGCEpicure()
                call.respond("Orders payment retry completed successfully")
            }
        }
    }
}


