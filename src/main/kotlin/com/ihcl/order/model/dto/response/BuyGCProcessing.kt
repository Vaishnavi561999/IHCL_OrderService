package com.ihcl.order.model.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class ProcessingResponse(
    val cancel: CancelDetails?,
    val currency: CurrencyDetails?,
    val orderId: String?,
    val payments: List<PaymentDetails?>?,
    val refno: String?,
    val status: String?
)

@Serializable
data class CurrencyDetails(
    val code: String?,
    val numericCode: String?,
    val symbol: String?
)
@Serializable
data class PaymentDetails(
    val balance: String?,
    val code: String?
)

@Serializable
data class CancelDetails(
    val allowed: Boolean?,
    val allowedWithIn: Int?
)
