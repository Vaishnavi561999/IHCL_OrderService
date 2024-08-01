package com.ihcl.order.utils

import com.google.gson.Gson
import com.ihcl.order.config.Configuration
import com.ihcl.order.config.RedisConfig
import com.ihcl.order.model.dto.response.*
import com.ihcl.order.model.exception.HttpResponseException
import com.ihcl.order.model.exception.OrderInternalServerException
import com.ihcl.order.model.exception.SSOErrorResponse
import com.ihcl.order.model.schema.*
import com.ihcl.order.repository.OrderRepository
import com.ihcl.order.service.v1.OrderService
import io.konform.validation.Constraint
import io.konform.validation.Invalid
import io.konform.validation.ValidationBuilder
import io.konform.validation.ValidationResult
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import org.koin.java.KoinJavaComponent
import org.litote.kmongo.json
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.UUID

object ValidatorUtils {
    private val orderService by KoinJavaComponent.inject<OrderService>(OrderService::class.java)
    private val orderRepository by KoinJavaComponent.inject<OrderRepository>(OrderRepository::class.java)
    private val log: Logger = LoggerFactory.getLogger(javaClass)
    val gson= Gson()
    private val prop= Configuration.env

    fun getUUID(): String{
        return UUID.randomUUID().toString()
    }
    fun ValidationBuilder<String>.notEmpty(): Constraint<String> {
        return addConstraint("Must not be empty.") { it.isNotEmpty() }
    }
    fun <T> validateRequestBody(validateResult: ValidationResult<T>) {
        if (validateResult is Invalid) {
            throw HttpResponseException(generateErrors(validateResult), HttpStatusCode.BadRequest)
        }
    }
    private fun <T> generateErrors(validationResult: ValidationResult<T>): Map<String,String> {
        val errors = mutableMapOf<String,String>()
        validationResult.errors.forEach { error ->
            errors.put(
                error.dataPath.substring(1), error.message
            )
        }
        return errors
    }

    fun validatePhoneNoFormat(phoneNumber: String):Boolean{
        val indianPhonePattern = Regex("""^\+91 ?\d{10}$""")
        return indianPhonePattern.matches(phoneNumber)
    }

    fun validateUserInput(username: String?, email: String?) {

        if(username.isNullOrEmpty() ||  email.isNullOrEmpty() || !email.contains("@")){
            throw HttpResponseException(MISSING_ORDER_ID_AND_EMAIL_ERROR_MSG, HttpStatusCode.BadRequest)
        }
    }
    suspend fun validateOverviewResponse(call: ApplicationCall,res: HttpResponse,limit:String){
        when (res.status) {
            HttpStatusCode.OK->{
                val customerDetailsResponse = res.body() as CustomerDetailsResponse
                val mobileNumber = extractMobileNumber(customerDetailsResponse)
                val response = orderService.getUpcomingBooking(customerDetailsResponse,mobileNumber)
                val bookingCount=orderService.bookingsCount(response)
                val  limitUpcomingBookings = orderService.limitBooking(response, limit.toInt())
                val bookingResponse= BookingDetailResponse(TotalBookingsResponseDto(bookingCount,response,0, listOf()))
                log.info("success response of overview API${bookingResponse.json}")
                call.respond(UpcomingBooking(HotelBookingResponse(bookingCount,limitUpcomingBookings)))
            }
            HttpStatusCode.Unauthorized->{
                call.respond(HttpStatusCode.Unauthorized,res.body() as SSOErrorResponse)
            }
            else-> {
                call.respond(res.bodyAsText())
            }
        }
    }
    suspend fun validateOrderTypeResponse(getOrders:List<Order>?,userOrderType:String?,limit: String,call: ApplicationCall){
        val orders = ArrayList<Any>()
        for (order in getOrders!!) {
            val orderType = order.orderType.toString()
            if (orderType == userOrderType) {
                orders.add(order)
            }
        }
        val totalOrders = orders.take(limit.toInt())
        if (totalOrders.isEmpty()) {
            throw OrderInternalServerException("No orders.....")
        }
        call.respond(MyOrders(totalOrders))
    }
    suspend fun validateOldOrderResponse(orders:List<Order>){
        orders.forEach {
            when {
                it.orderType.toString() == "HOTEL_BOOKING" -> {
                    if (it.orderLineItems[0].hotel?.checkIn.isNullOrEmpty() || it.orderLineItems[0].hotel?.checkOut.isNullOrEmpty()) {
                        log.info(".....${it.orderId}")
                        it.orderLineItems[0].hotel?.checkIn = "2023-07-18"
                        it.orderLineItems[0].hotel?.checkOut = "2023-07-19"
                        orderRepository.updateMultipleOrders(it)
                    }
                }
            }
        }
    }
    suspend fun validateDeleteBooking(deletionResult:Boolean,call: ApplicationCall,bookingDetails:RedisDto){
        if (deletionResult) {
            call.respond(HttpStatusCode.OK, "Data with key ${bookingDetails.key} deleted successfully.")
        } else {
            call.respond(HttpStatusCode.InternalServerError, "Failed to delete data with key ${bookingDetails.key}")
        }
    }
    private fun extractMobileNumber(customerDetailsResponse:CustomerDetailsResponse):String?{
        return if(!customerDetailsResponse.primaryMobile?.isdCode.isNullOrEmpty() && !customerDetailsResponse.primaryMobile?.phoneNumber.isNullOrEmpty()){
            customerDetailsResponse.primaryMobile?.isdCode + " " + customerDetailsResponse.primaryMobile?.phoneNumber
        }else {
           customerDetailsResponse.primaryMobile?.phoneNumber
        }
    }
    suspend fun validateFetchBooking(res: HttpResponse,call: ApplicationCall,limit: String){
        when (res.status) {
            HttpStatusCode.OK->{
                val customerDetailsResponse = res.body() as CustomerDetailsResponse
                val mobileNumber = extractMobileNumber(customerDetailsResponse)
                val response = orderService.getUpcomingBooking(customerDetailsResponse,mobileNumber)
                val  limitUpcomingBookings = orderService.limitBooking(response, limit.toInt())
                val bookingCount=orderService.bookingsCount(limitUpcomingBookings)
                val bookingResponse=BookingDetailResponse(TotalBookingsResponseDto(bookingCount,limitUpcomingBookings,0, listOf()))
                log.info("success response of upcoming bookings${bookingResponse.json}")
                call.respond(bookingResponse)
            }
            HttpStatusCode.Unauthorized->{
                call.respond(HttpStatusCode.Unauthorized,res.body() as SSOErrorResponse)
            }
            else-> {
                call.respond(res.bodyAsText())
            }
        }
    }
    suspend fun validatePastBooking(res: HttpResponse,call: ApplicationCall,limit: String){
        when (res.status) {
            HttpStatusCode.OK->{
                val customerDetailsResponse = res.body() as CustomerDetailsResponse
                val mobileNumber= extractMobileNumber(customerDetailsResponse)
                var redisKey = ""
                if (!mobileNumber.isNullOrEmpty() && !customerDetailsResponse.primaryEmailId.isNullOrEmpty()) {
                    redisKey = customerDetailsResponse.primaryEmailId + "||" + mobileNumber + "past"
                    log.info("redis key :$redisKey")
                    val redisBookingResponse = RedisConfig.getKey(redisKey)
                    if (!redisBookingResponse.isNullOrEmpty()) {
                        val bookingsResponse =gson.fromJson(redisBookingResponse,BookingDetailResponse::class.java)
                        call.respond(bookingsResponse)
                    }else{
                        getPastBookingResponse(call,customerDetailsResponse,limit,mobileNumber, redisKey)
                    }
                }else{
                        getPastBookingResponse(call,customerDetailsResponse,limit,mobileNumber, redisKey)
                }
            }
            HttpStatusCode.Unauthorized->{
                call.respond(HttpStatusCode.Unauthorized,res.body() as SSOErrorResponse)
            }
            else-> {
                call.respond(res.bodyAsText())
            }
        }
    }
     private suspend fun getPastBookingResponse(call: ApplicationCall, customerDetailsResponse: CustomerDetailsResponse, limit: String, mobileNumber:String?, redisKey:String){
         val response = orderService.getPastBookings(customerDetailsResponse,mobileNumber)
         val  limitPastBookings = orderService.limitBooking(response, limit.toInt())
         val bookingCount=orderService.bookingsCount(limitPastBookings)
         val bookingResponse=BookingDetailResponse(TotalBookingsResponseDto(0, listOf(),bookingCount,limitPastBookings))
         if (redisKey.isNotEmpty()) {
             RedisConfig.setKeyAndTTL(redisKey, bookingResponse.json, prop.timeSecondsOfTTL.toLong())
         }
         log.info("success response of past bookings$bookingResponse")
         call.respond(bookingResponse)
     }
}