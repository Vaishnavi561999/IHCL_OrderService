package com.ihcl.order.model.dto.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kotlinx.serialization.Serializable

@Serializable
data class PreAuthCompleteResponse(
    val Card: List<CardDetailsInfo>,
    val currentBatchNumber: String,
    val responseCode: Int,
    val responseMessage: String,
    val transactionId: Int
)
@Serializable
data class CardDetailsInfo(
    val approvalCode: String?,
    val balance: Double,
    val preAuthCode: String?,
    val responseCode: Int,
    val responseMessage: String,
    val transactionAmount: String,
    val transactionDateAndTime: String
)