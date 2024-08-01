package com.ihcl.order.model.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class TenderModeRequest(
    val orderId:String,
    val tenderMode:String,
    val amount:Double
)
