package com.ihcl.order.model.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateBookingRequest(
    val crsConfirmationNumber: String?,
    val guests: List<Guest>,
    val hotelId: String,
    val roomStay: RoomStayInfo,
    val notification: Notification?,
    val status: String
)

@Serializable
data class ContactNumber(
        val number: String,
        val type: String
)
@Serializable
data class EmailAddress(
        val value: String
)
@Serializable
data class Guest(
        val contactNumbers: List<ContactNumber>,
        val emailAddress: List<EmailAddress>,
        val payments: List<PaymentDetailsInfo>,
        val personName: PersonName
)

@Serializable
data class PaymentDetailsInfo(
        val paymentCard: PaymentCard,
        val type: String
)
@Serializable
data class PaymentCard(
        val cardCode: String?,
        val cardHolder: String?,
        val cardNumber: String,
        val cardSecurityCode: String,
        val expireDate: String
)
@Serializable
data class PersonName(
        val firstName: String,
        val prefix: String?,
        val lastName: String
)
@Serializable
data class ProductInfo(
        val endDate: String,
        val product: ProductDetailsInfo,
        val startDate: String
)
@Serializable
data class ProductDetailsInfo(
        val rateCode: String,
        val roomCode: String
)
@Serializable
data class RoomStayInfo(
        val endDate: String,
        val guestCount: MutableList<GuestCount>,
        val numRooms: Int,
        val products: List<ProductInfo>,
        val startDate: String
)