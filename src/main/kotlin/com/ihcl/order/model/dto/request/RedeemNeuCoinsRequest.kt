package com.ihcl.order.model.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class RedeemNeuCoinsRequest(
    val customFields: CustomFields?,
    val externalReferenceNumber: String?,
    val pointsRedeemed: String?,
    val transactionNumber: String?
)
@Serializable
data class CustomFields(
    val `field`: List<Field?>?
)
@Serializable
data class Field(
    val name: String?,
    val value: String?
)