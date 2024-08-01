package com.ihcl.order.model.dto.request

data class GCOrderStatusRequest (
    val orderNumber:String?,
    val email:String?
)
data class ActivateGCRequest(
    val orderId:String
)