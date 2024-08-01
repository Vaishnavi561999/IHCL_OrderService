package com.ihcl.order.model.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class ModifyBookingRequest(
    val customerHash: String,
    val orderId: String
)
