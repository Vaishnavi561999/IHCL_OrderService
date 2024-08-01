package com.ihcl.order.model.dto.response

import com.ihcl.order.model.schema.OrderType
import kotlinx.serialization.Serializable

@Serializable
data class CustomerDetailsResponse(val primaryMobile:PrimaryMobile?, var primaryEmailId:String?, val customerHash: String?)
@Serializable
data class PrimaryMobile(val isdCode:String?,val phoneNumber:String?)
data class BookingsResponse(
    val customerEmail:String?,
    val customerMobile:String?,
    val channel:String,
    val orderType: OrderType,
    val orderLineItems:List<OrderItems>,
    var paymentMethod: String?,
    var transactionStatus: String?,
)
data class OrderItems(
    val hotel:Hotels,
)
data class Hotels(
    val bookingNumber:String?,
    val name:String?,
    val checkIn:String?,
    val checkOut:String?,
    val rooms:List<RoomDto>?,
    val storeId:String?,
    val synxisId:String?,
    val bookingNoOfNights: Int
    )
data class RoomDto(
    val confirmationId:String?,
    val status:String?,
    val checkIn:String?,
    val checkOut:String?,
    val description:String?,
    val detailedDescription:String?,
    val roomName:String?,
    val roomType: String?,
    val rateCode:String?,
    val packageName:String?,
    val currency:String?,
    val roomCode:String?,
    val roomId:String?,
    val tax: Tax,
    val price:Double?,
    val grandTotal:Double?,
    val penaltyAmount:Double?,
    val penaltyDeadLine :String?,
    val guestCount: List<GuestCount?>?,
    val cancelPolicyDescription:String?,
    val bookingPolicyDescription:String?,
    val travellerDetails: List<TravellerDetails>?
)
data class TravellerDetails(
    val salutation:String?,
    val countryCode: String?,
    val email: String?,
    val firstName: String?,
    val lastName: String?,
    val mobile: String?
)