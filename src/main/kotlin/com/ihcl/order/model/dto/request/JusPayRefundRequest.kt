package com.ihcl.order.model.dto.request

data class JusPayRefundRequest(
    val amount: String,
    val orderId: String
)