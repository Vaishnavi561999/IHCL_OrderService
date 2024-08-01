package com.ihcl.order.model.schema

import com.ihcl.order.model.dto.response.BookingsResponse
import java.util.LinkedHashSet

data class MyAccountsSchema(
    val hotelBookings:HotelBooking,
    val giftCards: GiftCards,
    val memberShips: MemberShips,
)
data class HotelBooking(
    val data:List<Any>
)
data class GiftCards(
    val data:List<Any>
)
data class MemberShips(
    val data:List<Any>
)
data class MyOrders(
    val orders:List<Any>
)
data class UpcomingBookingsInfo(
    val upComingCount: Int?,
    val upComingBookings: LinkedHashSet<Order>?

)
data class UpcomingBookings(
    val hotelBookings: HotelBookings
)
data class UpcomingBooking(
    val hotelBookings: HotelBookingResponse
)
data class HotelBookingResponse(
    val upComingCount: Int?,
    val upComingBookings: List<BookingsResponse>?

)
data class HotelBookings(
    val upComingCount: Int?,
    val upComingBookings: LinkedHashSet<Order>?

)
