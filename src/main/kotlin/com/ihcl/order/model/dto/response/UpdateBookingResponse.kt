package com.ihcl.order.model.dto.response

data class UpdateBookingResponse(
    val data: HotelInfo
)

data class HotelInfo(
    val updateHotelBooking: UpdateHotelBooking
)

data class UpdateHotelBooking(
    val message: String,
    val reservations : List<RoomReservationDetailsInfo>?
    )

data class RoomReservationDetailsInfo(
    val crsConfirmationNumber: String,
    val itineraryNumber: String

)
