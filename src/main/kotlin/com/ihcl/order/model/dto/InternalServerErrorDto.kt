package com.ihcl.order.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class InternalServerErrorDto (
    val message:String,
    val orderId: String
)
@Serializable
data class TimeOutException (
    val message:String,
    val cause: String
)
