package com.ihcl.order.model.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class PreAuthResponse(
    val Card: List<CardInfo>,
    val currentBatchNumber: Int,
    val responseCode: Int,
    val responseMessage: String,
    val transactionId: Int
)
@Serializable
data class CardInfo(
    val approvalCode: String,
    val balance: Double,
    val preAuthCode: String,
    val responseCode: Int,
    val responseMessage: String,
    val transactionAmount: String,
    val transactionDateAndTime: String
)