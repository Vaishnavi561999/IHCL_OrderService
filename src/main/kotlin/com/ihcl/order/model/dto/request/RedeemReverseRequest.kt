package com.ihcl.order.model.dto.request

data class RedeemReverseRequest(
    val TransactionId: String,
    val CardNumber: String,
    val Amount: Double
)
