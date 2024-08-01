package com.ihcl.order.model.dto.response

import com.ihcl.order.model.schema.Order
import kotlinx.serialization.Serializable

data class BookingDetails(
    val hotelBookings:TotalBookings,
    val memberShips:TotalMemberShips
 )
 data class TotalBookings(
    val upComingBookings: LinkedHashSet<Order>,
    val pastBookings: LinkedHashSet<Order>
)
data class TotalMemberShips(
    val data : List<Any>
)
@Serializable
data class BookingDetail(
    val hotelBookings:TotalBookingsResponse?
)
data class BookingDetailResponse(
    val hotelBookings:TotalBookingsResponseDto?
)
data class TotalBookingsResponseDto(
    val upComingCount:Int?,
    val upComingBookings:List<BookingsResponse>?,
    val pastCount:Int?,
    val pastBookings:List<BookingsResponse>?
)
@Serializable
data class TotalBookingsResponse(
    val upComingCount:Int?,
    val upComingBookings: java.util.LinkedHashSet<Order>?,
    val pastCount:Int?,
    val pastBookings: LinkedHashSet<Order>?
)
 data class RedisDto(val key: String)