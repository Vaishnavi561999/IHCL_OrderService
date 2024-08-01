package com.ihcl.order.model.dto.response

data class CreateBookingResponse(
    val data: HotelData
)

data class HotelData(
    val createHotelBooking: CreateHotelBooking
)

data class CreateHotelBooking(
    val message: String,
    val reservations : List<RoomReservationDetails>?
    )

data class RoomReservationDetails(
    val crsConfirmationNumber: String?,
    val itineraryNumber: String?,
    val notAvailableRooms:MutableList<Int>
)

data class CreateBookingConfirmation(
    val reservations : List<RoomReservationDetails>
)