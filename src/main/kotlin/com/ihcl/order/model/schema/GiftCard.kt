package com.ihcl.order.model.schema

import kotlinx.serialization.Serializable

@Serializable
data class GiftCard(
    val deliveryMethods: DeliveryMethodsDto?,
    val quantity: Int?,
    val isMySelf: Boolean?,
    val giftCardDetails: List<GiftCardDetailsDto>?,
    val receiverAddress: ReceiverAddressDto?,
    val receiverDetails: ReceiverDetailsDto?,
    val senderDetails: SenderDetailsDto?
)
@Serializable
data class GiftCardDetailsDto(
    var amount: Double?,
    val sku: String?,
    val type: String?,
    val theme: String?,
    var cardNumber: String?,
    var cardPin: String?,
    var cardId: String?,
    var validity: String?,
    var orderId: String?
    )
@Serializable
data class ReceiverAddressDto(
    val addressLine1: String?,
    val addressLine2: String?,
    val city: String?,
    val country: String?,
    val pinCode: String?,
    val state: String?
)
@Serializable
data class ReceiverDetailsDto(
    val email: String,
    val firstName: String,
    val lastName: String,
    val message: String,
    val phone: String,
    val rememberMe: Boolean,
    val scheduleOn: String?
)
@Serializable
data class SenderDetailsDto(
    val email: String,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val registerAsNeuPass: Boolean
)
@Serializable
data class DeliveryMethodsDto(
    val phone: String?,
    val email: Boolean?,
    val smsAndWhatsApp: Boolean?
)