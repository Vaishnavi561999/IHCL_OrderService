package com.ihcl.order.model.schema

import com.google.gson.annotations.SerializedName
import com.ihcl.order.model.dto.request.PaymentDetailsDTO

data class RefundOrders(
    @SerializedName("orderId") var orderId: String? = null,
    @SerializedName("bookingNumber") var bookingNumber: String? = null,
    @SerializedName("type") var type: String? = null,
    @SerializedName("rooms") var rooms: ArrayList<Rooms> = arrayListOf(),
    var createdOn: String? = null,
    @SerializedName("paymentDetails") var paymentDetails: ArrayList<PaymentDetailsDTO> = arrayListOf()
)
