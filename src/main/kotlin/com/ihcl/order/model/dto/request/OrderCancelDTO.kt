package com.ihcl.order.model.dto.request

import com.google.gson.annotations.SerializedName

data class OrderCancelDTO(
    @SerializedName("orderId"       ) var orderId       : String?          = null,
    @SerializedName("bookingNumber" ) var bookingNumber : String?          = null,
    @SerializedName("rooms"         ) var rooms         : List<CancelRoomsDTO> = listOf()

)
