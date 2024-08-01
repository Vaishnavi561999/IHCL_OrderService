package com.ihcl.order.model.dto.response

import kotlinx.serialization.Serializable

data class ReserveNeuCoinResponse(
    val customerId: Int,
    val identifier: Identifier,
    val orgId: Int,
    val pointsReversed: Int,
    val pointsToBeReversed: Int,
    val redemptionId: String,
    val reversalId: String,
    val warnings: List<Any>
)

@Serializable
data class Identifier(
    val type: String,
    val value: String
)