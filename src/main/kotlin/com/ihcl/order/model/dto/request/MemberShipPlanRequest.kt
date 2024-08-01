package com.ihcl.order.model.dto.request

import com.google.gson.annotations.SerializedName

data class MemberShipPlanRequest(
    @SerializedName("member_id")
    var memberId: String,
    @SerializedName("membership_plan_name")
    var membershipPlanName: String,
    @SerializedName("membership_plan_type")
    var membershipPlanType: String,
    @SerializedName("membership_plan_code")
    var membershipPlanCode: String,
    @SerializedName("membership_plan_status")
    var membershipPlanStatus: String,
    @SerializedName("membership_plan_start_date")
    var membershipPlanStartDate: String,
    @SerializedName("membership_plan_end_date")
    var membershipPlanEndDate: String,
    @SerializedName("payment_transaction_id")
    var paymentTransactionId: String,
    @SerializedName("issued_partner")
    var issuedPartner: String,
    @SerializedName("plan_price")
    var planPrice: Double?,
    @SerializedName("payment_status")
    var paymentStatus: String,
    @SerializedName("orion_payment_method")
    var orionPaymentMethod: String,
    @SerializedName("orion_payment_type")
    var orionPaymentType: String,
    @SerializedName("enrolment_promo_code")
    var enrolmentPromoCode: String,
    @SerializedName("refund_amount")
    var refundAmount: Double?,
    var tax: Double?,
    @SerializedName("membership_plan_id")
    var membershipPlanId: String,
    @SerializedName("payment_amount")
    var paymentAmount: String,
    @SerializedName("payment_date")
    var paymentDate: String,
    @SerializedName("payment_method")
    var paymentMethod: String,
    @SerializedName("payment_type")
    var paymentType: String,
    @SerializedName("receipt_number")
    var receiptNumber: String,
    @SerializedName("payment_reference")
    var paymentReference: String,
    @SerializedName("membership_plan_source")
    var membershipPlanSource: String,
    @SerializedName("partner_category")
    var partnerCategory: String,
    @SerializedName("epicure_type")
    var epicureType: String
)