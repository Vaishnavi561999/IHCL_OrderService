package com.ihcl.order.model.dto.response

data class CancelBookingResponse(
    val `data`: CancelBookingData
)
data class CancelBookingData(
    val cancelHotelBooking: CancelHotelBooking
)
data class CancelHotelBooking(
    val cancellationNumber: String,
    val message: String
)