package com.ihcl.order.model.schema

import com.ihcl.order.model.dto.request.PaymentDetailsDTO
import kotlinx.serialization.Serializable

@Serializable
data class
ApportionOrder(
    var _id:String? = null,
    var orderId: String? = null,
    var bookingNumber: String? = null,
    var type: String? = null,
    var rooms: ArrayList<Rooms> = arrayListOf(),
    var createdOn: String? = null,
    var paymentDetails: MutableList<PaymentDetailsDTO> = mutableListOf()
)
