package com.ihcl.order.model.dto.request

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class VoucherRedemptionAvailPrivileges(
    var bitDate: String?,
    var memberId: String?,
    var privileges: String?,
    var pin:String? = null,
    var availBitId: String? = null,
    var status: String? = null,
    var bitCategory: String? = null,
    val isComplementary: Boolean?,
    var type:String?
)

data class VoucherRedemptionAvailRequest(
    @SerializedName("h_bit_date")
    var hBitDate: String?,
    @SerializedName("h_member_id")
    var hMemberId: String?,
    @SerializedName("h_privileges")
    var hPrivileges: String?,
    @SerializedName("pin")
    var pin:String? = null,
    var type:String?,
    val hotelSponsorId:String?
)

data class BitCancellationVoucherReversal(
    @SerializedName("h_bit_date")
    var hBitDate: String?,
    @SerializedName("cancel_bit_id")
    var cancelBitId: String?,
    @SerializedName("h_member_id")
    var hMemberId: String?,
    var type:String?,
    val hotelSponsorId:String?
)
