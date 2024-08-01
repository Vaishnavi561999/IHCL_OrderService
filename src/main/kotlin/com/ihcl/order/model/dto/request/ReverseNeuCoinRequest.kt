package com.ihcl.order.model.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class ReverseNeuCoinRequest(
    val identifier: Identifier?,
    val pointsToBeReversed: String?,
    val redemptionId: String?
)

@Serializable
data class Identifier(
    val type: String?,
    val value: String?
)