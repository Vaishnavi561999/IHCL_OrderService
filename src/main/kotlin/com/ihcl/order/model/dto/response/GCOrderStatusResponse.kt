package com.ihcl.order.model.dto.response

data class GCOrderStatusResponse(
    val cancel: Cancel?,
    val orderId: String?,
    val refno: String?,
    val status: String?,
    val statusLabel: String?
)

data class GCOrderStatusErrorResponse(
    val code: String,
    val message: String,
    val messages: List<Any>
)