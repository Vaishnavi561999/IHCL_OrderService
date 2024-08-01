package com.ihcl.order.model.dto.response

import kotlinx.serialization.*

@Serializable
data class Program(
    val id: Int,
    val name: String,
    val description: String
)

@Serializable
data class GroupLoyaltyProgramDetail(
    val groupProgramId: Int,
    val title: String,
    val description: String,
    val programsList: List<Program>,
    val lifetimePoints: Double,
    val loyaltyPoints: Double,
    val promisedPoints: Int,
    val pointsToCurrencyRatio: Double
)

@Serializable
data class LoyaltyProgram(
    val groupLoyaltyProgramDetails: List<GroupLoyaltyProgramDetail>,
    val blockedRedeemption: String,
    val blockedRedeemptionReason: String?
)