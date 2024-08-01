package com.ihcl.order.model.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class AddOnCardResponse(
    val data: Datax

)
@Serializable
data class Datax(
    val air_way_bill_number: String,
    val card_expiry_date: String,
    val card_number: String,
    val card_start_date: String,
    val card_status: String,
    val card_type: String,
    val created_ts: String,
    val dispatched_date: String,
    val fulfilment_status: String,
    val id: Int,
    val member_id: String,
    val membership_plan_code: String,
    val membership_plan_id: String,
    val name_on_card: String,
    val receipt_number: String,
    val relationship_type: String,
    val spouse_date_of_birth: String,
    val spouse_email_address: String,
    val spouse_gender: String,
    val spouse_name: String,
    val spouse_phone_number: String,
    val updated_ts: String
)
