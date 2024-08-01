package com.ihcl.order.model.dto.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kotlinx.serialization.Serializable

@Serializable
@JsonIgnoreProperties(ignoreUnknown = true)
data class PreAuthCancelRequest(
    val CardNumber: String,
    val OriginalRequest: OriginalRequest
)
@Serializable
@JsonIgnoreProperties(ignoreUnknown = true)
data class OriginalRequest(
    val OriginalAmount: Double,
    val OriginalApprovalCode: String,
    val OriginalBatchNumber: String,
    val OriginalTransactionId: String
)

enum class TenderMode{ GIFT_CARD, VOUCHER, TATA_NEU}
