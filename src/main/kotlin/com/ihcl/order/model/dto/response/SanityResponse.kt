package com.ihcl.order.model.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class OfferPackage(
    val title: String,
    val rateCode: String,
    val minNeucoinsCap: Double?
)

@Serializable
data class OfferValidationResponse(
    val query: String,
    val result: List<OfferPackage>,
    val syncTags: List<String>,
    val ms: Int
)