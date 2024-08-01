package com.ihcl.order.model.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class OrderStatusFailure(
    val error_code : String?,
    val error_message: String?,
    val order_id: String?,
    val status: String?,
    val status_id: Int?
)
data class OrderStatusError(
    val code:Int?,
    val message:String?
)