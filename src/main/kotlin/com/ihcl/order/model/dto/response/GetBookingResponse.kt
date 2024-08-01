package com.ihcl.order.model.dto.response

data class GetBookingResponse(
    val reservations: List<Reservations?>?
)
data class Reservations(
    val content: ContentX?,
    val roomStay: RoomStay?,
    val roomPrices: RoomPricesX?,
    val crsConfirmationNumber1: String?,
    val status: String?,
    val hotel: Hotel?,
    val cancelPolicy: CancelPolicy

)
data class ContentX(
    val rateCategories: List<RateCategory?>?,
    val rooms: List<RoomsDetailsInfo?>?
)

data class RoomPricesX(
    val totalPrice: TotalPriceX?


)

