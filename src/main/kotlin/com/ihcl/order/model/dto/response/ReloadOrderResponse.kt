package com.ihcl.order.model.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class ReloadOrderResponse(
    val orderId: String?,
    val cardNumber: String,
    val amount: Double,
    val customerHash: String)

data class CreateOrderGCResponse(
    val orderId: String?
)