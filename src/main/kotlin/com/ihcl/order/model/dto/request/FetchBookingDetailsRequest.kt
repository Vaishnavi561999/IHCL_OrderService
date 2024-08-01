package com.ihcl.order.model.dto.request

data class FetchBookingDetailsRequest(
    val emailId:String?,
    val guestPhoneNumber:String?,
    val hotelId: String?,
    val itineraryNumber: String?,
    val arrivalDate:String?
)
data class FetchBookingDetailsByEmail(
    val emailId:String?,
    val arrivalDate:String?

)
data class FetchBookingDetailsByMobile(
    val guestPhoneNumber:String?,
    val arrivalDate:String?
)
data class FetchBookingDetailsByItenary(
    val itineraryNumber:String?,
    val arrivalDate:String?

)
data class FetchBookingDetailsByListOfEmail(
    val emailId:String?,
    val arrivalDate:String?
)