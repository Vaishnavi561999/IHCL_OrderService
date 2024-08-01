package com.ihcl.order.model.dto.response

import com.google.gson.annotations.SerializedName


data class MemberShipBadRequestError(
    val error: MemberShipError
)
data class MemberShipError(
    val payment_date:ArrayList<Messages>?,
    @SerializedName("membership_plan_id")
    val memberShipPlanId:ArrayList<Messages>?,
    val orion_payment_method:ArrayList<Messages>?,
    val orion_payment_type:ArrayList<Messages>?,
    val membership_plan_start_date:ArrayList<Messages>?,
    val scope:String?
)
data class Messages(
    val message:String,
    val code: String
)
