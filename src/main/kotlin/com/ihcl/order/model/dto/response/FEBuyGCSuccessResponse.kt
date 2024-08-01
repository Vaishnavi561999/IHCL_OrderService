package com.ihcl.order.model.dto.response

import com.ihcl.order.model.schema.DeliveryMethodsDto
import com.ihcl.order.model.schema.ReceiverAddressDto
import kotlinx.serialization.Serializable

@Serializable
data class FEBuyGCSuccessResponse(
    val receiverLastName: String?,
    val receiverFirstName: String?,
    val senderFirstName: String,
    val senderLastName: String,
    val email: String?,
    val purchaseOrderNo: String?,
    val priceTotal: Double,
    val cardNumber: String?,
    val nameOnCard: String?,
    val cardBrand: String?,
    val receivedFrom: String?,
    val paymentMethod: String,
    val senderPhoneNumber: String?,
    val receiverPhoneNumber: String?,
    val deliveryMethods: DeliveryMethodsDto?,
    val receiverAddress: ReceiverAddressDto?,
    val giftCard : List<GiftCardDetails>?,
    val priceBreakUp: PriceBreakUp?,
    val paymentTransactionId: String?
)

@Serializable
data class PriceBreakUp(
    val price: Double,
    val neuCoinsAmount: Double,
    val totalPrice: Double
)

@Serializable
data class GiftCardDetails(
    val message: String,
    val giftCardNumber: String?,
    val theme: String?,
    val sku : String?,
    val scheduledOn: String?,
    val createdOn : String,
    val validity: String?,
    val amount: Double?
)
