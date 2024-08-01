package com.ihcl.order.model.dto.request

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class OrderDTO(
    @SerializedName("orderId"        ) var orderId        : String?                   = null,
    @SerializedName("bookingNumber"  ) var bookingNumber  : String?                   = null,
    @SerializedName("type"        ) var type        : String?                   = null,
    @SerializedName("rooms"          ) var rooms          : List<RoomsDTO>          = listOf(),
    @SerializedName("paymentDetails" ) var paymentDetails : MutableList<PaymentDetailsDTO> = mutableListOf()
)
