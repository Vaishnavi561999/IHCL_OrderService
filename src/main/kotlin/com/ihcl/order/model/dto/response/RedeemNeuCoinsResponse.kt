package com.ihcl.order.model.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class RedeemNeuCoinsResponse(
    val response: Response?
)
@Serializable
data class Response(
    val responses: Responses?,
    val status: RedeemStatusStatus?
)
@Serializable
data class Responses(
    val points: Points?
)
@Serializable
data class RedeemStatusStatus(
    val code: Int?,
    val message: String?,
    val success: String?
)
@Serializable
data class Points(
    val balance: Double?,
    val email: String?,
    val external_id: String?,
    val is_group_redemption: Boolean?,
    val item_status: ItemStatus?,
    val mobile: String?,
    val points_redeemed: Double?,
    val redeemed_local_value: Double?,
    val redeemed_value: Double?,
    val redemption_breakup_by_earning_programs: List<RedemptionBreakupByEarningProgram?>?,
    val redemption_id: String?,
    val redemption_program_id: Int?,
    val redemption_purpose: String?,
    val side_effects: SideEffects?,
    val user_id: String?
)
@Serializable
data class ItemStatus(
    val code: Int?,
    val message: String?,
    val success: String?
)
@Serializable
data class RedemptionBreakupByEarningProgram(
    val points_redeemed: Double?,
    val program_current_points: Double?,
    val program_id: Int?
)
@Serializable
data class SideEffects(
    val effect: List<Effect?>?
)
@Serializable
data class Effect(
    val case_value: String?,
    val currency_value: Double?,
    val id: Int?,
    val num_points: Double?,
    val points_redemption_summary_id: String?,
    val redeemed_on_bill_id: Double?,
    val redeemed_on_bill_number: String?,
    val type: String?,
    val validation_code: String?
)