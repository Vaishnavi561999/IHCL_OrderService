package com.ihcl.order.model.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class OrderDummySuccess(val status: String,
    val orderID: String)
