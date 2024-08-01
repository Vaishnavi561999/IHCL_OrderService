package com.ihcl.order.model.dto.response

import com.google.gson.annotations.SerializedName
data class BitCancellationVoucherReversalResponse(

    @SerializedName("original_bit")
    var originalBit: OriginalBits,
    @SerializedName("processing_date")
    var processingDate: String,
    @SerializedName("bit_id")
    var bitId: String,
    @SerializedName("status")
    var status: String,
    @SerializedName("error")
    var error: String
)
data class OriginalBits(
    @SerializedName("header")
    var header: Headers,
    @SerializedName("lines")
    var lines: ArrayList<String>,
    @SerializedName("payment_details")
    var paymentDetails: ArrayList<String>

)
data class Headers(

    @SerializedName("h_bit_category")
    var hBitCategory: String,
    @SerializedName("h_sponsor_id")
    var hSponsorId: Int,
    @SerializedName("h_rep_email")
    var hRepEmail: String,
    @SerializedName("h_rep_id")
    var hRepId: Int?,
    @SerializedName("h_bit_type")
    var hBitType: String,
    @SerializedName("h_member_id")
    var hMemberId: String,
    @SerializedName("h_program_id")
    var hProgramId: Int,
    @SerializedName("h_sponsor_id_text")
    var hSponsorIdText: String,
    @SerializedName("h_comment")
    var hComment: String,
    @SerializedName("cancel_bit_id")
    var cancelBitId: String,
    @SerializedName("h_bit_date")
    var hBitDate: String,

    )