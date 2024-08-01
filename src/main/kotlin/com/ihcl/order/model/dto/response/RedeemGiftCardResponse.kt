package com.ihcl.order.model.dto.response

data class RedeemGiftCardResponse(
    val ApprovalCode: String,
    val Cards: List<CardsInfo>,
    val CurrentBatchNumber: String,
    val responseCode: Int,
    val responseMessage: String,
    val transactionId: Int
)
data class CardsInfo(
    val balance: Double,
    val responseCode: Int,
    val responseMessage: String
)