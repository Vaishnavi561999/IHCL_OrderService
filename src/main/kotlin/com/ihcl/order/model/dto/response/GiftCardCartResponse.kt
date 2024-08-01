package com.ihcl.order.model.dto.response

import com.ihcl.order.model.schema.*

data class GiftCardCartResponse(
    var _id: String?,
    val items: GCCartResponseItems?,
    var paymentDetails: MutableList<PaymentDetail>?,
    var priceSummary: GiftCardPriceSummary?
)

data class GiftCardPriceSummary(
    var totalPrice: Double,
    var neuCoins: Double?,
    var totalPayableAmount: Double
)
data class GCCartResponseItems(
    val isMySelf:Boolean?,
    val category: String?,
    val quantity: Int?,
    val giftCardDetails: List<GiftCardDetailsDto>?,
    val deliveryMethods: DeliveryMethodsDto?,
    val receiverAddress: ReceiverAddressDto?,
    val senderAddress: ReceiverAddressDto?,
    val receiverDetails: ReceiverDetailsDto?,
    val senderDetails: SenderDetailsDto?
)