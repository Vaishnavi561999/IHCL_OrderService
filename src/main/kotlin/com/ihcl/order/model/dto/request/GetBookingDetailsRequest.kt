package com.ihcl.order.model.dto.request

data class GetBookingDetailsRequest(
    val confirmationNumber: String?,
    val hotelId: String?
)