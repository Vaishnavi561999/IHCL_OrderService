package com.ihcl.order.model.dto.response

import com.google.gson.annotations.SerializedName
import java.util.*

data class MemberShipPlanResponse(
    var data: DataPlan
)

data class DataPlan(
    var id: Int,
    @SerializedName("payment_transaction_id")
    var paymentTransactionId: String,
    @SerializedName("membership_plan_end_date")
    var membershipPlanEndDate: String,
    @SerializedName("orion_payment_method")
    var orionPaymentMethod: String,
    @SerializedName("created_ts")
    var createdTs: String,
    @SerializedName("plan_price")
    var planPrice: Int,
    @SerializedName("partner_category")
    var partnerCategory: String,
    @SerializedName("payment_date")
    var paymentDate: String,
    @SerializedName("issued_partner")
    var issuedPartner: String,
    @SerializedName("refund_amount")
    var refundAmount: Int,
    @SerializedName("membership_plan_name")
    var membershipPlanName: String,
    @SerializedName("orion_payment_type")
    var orionPaymentType: String,
    @SerializedName("epicure_type")
    var epicureType: String,
    @SerializedName("payment_amount")
    var paymentAmount: Float,
    @SerializedName("enrolment_promo_code")
    var enrolmentPromoCode: String,
    @SerializedName("membership_plan_status")
    var membershipPlanStatus: String,
    @SerializedName("membership_plan_source")
    var membershipPlanSource: String,
    @SerializedName("payment_type")
    var paymentType: String,
    @SerializedName("payment_reference")
    var paymentReference: String,
    @SerializedName("membership_plan_type")
    var membershipPlanType: String,
    @SerializedName("payment_status")
    var paymentStatus: String,
    @SerializedName("updated_ts")
    var updatedTs: Date,
    @SerializedName("payment_method")
    var paymentMethod: String,
    @SerializedName("membership_plan_id")
    var membershipPlanId: String,
    @SerializedName("receipt_number")
    var receiptNumber: String,
    var tax: String,
    @SerializedName("membership_plan_start_date")
    var membershipPlanStartDate: Date,
    @SerializedName("membership_plan_code")
    var membershipPlanCode: String,
    @SerializedName("member_id")
    var memberId: String,

    )