package com.ihcl.order.model.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateBookingRequest(
    val hotelId: String,
    val status: String,
    val itineraryNumber:String,
    val promotionType: String?,
    val promotionAccessKey: String?,
    val travelIndustryId: String?,
    val couponOffersCode: String,
    val currency: Currency,
    val guests: List<GuestDetails>,
    val notification: Notification?,
    val roomStay: RoomStay,
    val loyaltyMemberships:LoyaltyMemberships?
)

@Serializable
data class Currency(
    val code: String
)

@Serializable
data class GuestDetails(
    val role: String,
    val emailAddress: List<EmailAddressDetails>,
    val contactNumbers:List<ContactNumbers?>?
)

@Serializable
data class ContactNumbers(
    val number:String?,
    val code:String?,
    val role:String?,
    val sortOrder:Int?,
    val type:String?,
    val use:String?
)
@Serializable
data class EmailAddressDetails(
    val type: String,
    val value: String
)
@Serializable
data class RoomStay(
    val startDate: String,
    val endDate: String,
    val numRooms: Int,
    val guestCount: MutableList<GuestCount>,
    val products: MutableList<Products>

)
@Serializable
data class GuestCount(
    val ageQualifyingCode: String,
    val numGuests: Int
)
@Serializable
data class ProductDetails(
    val rateCode: String,
    val roomCode: String
)

@Serializable
data class Products(
    val product: ProductDetails,
    val startDate: String,
    val endDate: String
)

@Serializable
data class Notification(
    val bookingComment: String?,
    val deliveryComments: DeliveryComments?
)

@Serializable
data class DeliveryComments(
    val comment: String?
)

@Serializable
data class LoyaltyMemberships(
    val levelCode:String?,
    val membershipID:String?,
    val programID:String?,
    val source:String?
)