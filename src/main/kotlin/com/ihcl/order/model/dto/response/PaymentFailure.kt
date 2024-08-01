package com.ihcl.order.model.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class PaymentFailure(
    val status: String,
    val bankErrorCode: String,
    val bankErrorMessage: String,
    val transactionId: String,
    val orderId: String
)
