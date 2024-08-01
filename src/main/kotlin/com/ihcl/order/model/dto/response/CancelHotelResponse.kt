package com.ihcl.order.model.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class CancelHotelResponse(
    var orderId: String?,
    val remarks: String,
    val rooms: List<RoomStatus?>?
)

@Serializable
data class RoomStatus(
    var roomNumber: Int?,
    var status: String?
)
