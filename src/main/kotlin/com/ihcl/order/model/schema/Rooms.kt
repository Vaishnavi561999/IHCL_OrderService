package com.ihcl.order.model.schema

import kotlinx.serialization.Serializable

@Serializable
data class Rooms(
    var confirmationId : String?                   = null,
    var price          : Double?                      = null,
    var taxAmount      : Double?                   = null,
    var noOfNights     : Int?                   = null,
    var totalAmount    : Double?                   = null,
    var checkIn        : String?                   = null,
    var checkOut       : String?                   = null,
    var roomStatus     : String?                   = null,
    var apportionValues : ArrayList<PaymentDetails> = arrayListOf()
)
