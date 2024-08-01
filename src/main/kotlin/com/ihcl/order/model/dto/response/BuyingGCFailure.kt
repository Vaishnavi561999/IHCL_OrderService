package com.ihcl.order.model.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class BuyingGCFailure(
    val additionalTxnFields: List<String>?,
    val code: Int,
    val message: String?,
    val messages: List<String>?,
    val status: String?
)