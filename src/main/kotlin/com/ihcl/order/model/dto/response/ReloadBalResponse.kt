package com.ihcl.order.model.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class ReloadBalResponse(
    val transactionId:Int?,
    val responseMessage:String?,
    val responseCode:Int?,
    val Cards:List<CardDetails?>?
)
@Serializable
data class CardDetails(
    val responseMessage:String?,
    val responseCode:Int?,
    val balance:Double?,
    val cardNumber: String,
    val expiryDate: String
)