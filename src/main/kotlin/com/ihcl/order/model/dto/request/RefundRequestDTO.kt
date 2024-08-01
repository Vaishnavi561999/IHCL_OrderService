package com.ihcl.order.model.dto.request

import com.google.gson.annotations.SerializedName

data class RefundRequestDTO(
    @SerializedName("orderId"       ) var orderId       : String?          = null,
    @SerializedName("bookingNumber" ) var bookingNumber : String?          = null,
    @SerializedName("type"          ) var type          : String?          = null,
    @SerializedName("rooms"         ) var rooms         : ArrayList<RefundRoomsDTO> = arrayListOf()
)
