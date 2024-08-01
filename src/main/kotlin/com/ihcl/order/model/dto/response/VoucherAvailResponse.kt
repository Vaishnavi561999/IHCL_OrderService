package com.ihcl.order.model.dto.response

data class ChamberVoucherAvailResponse(
    val type:String,
    val data:VoucherAvailResponse
)
data class VoucherAvailResponse(
    val original_bit:OriginalBitChamberVoucherDetails,
    val availed_privileges:List<VoucherRedeemDetails>
)
data class OriginalBitChamberVoucherDetails(
    val header:ChamberHeader
)
data class ChamberHeader(
    val h_bit_date:String
)
data class VoucherRedeemDetails(
    val member_id:String,
    val privilege_code:String,
    val availment_bit_id:String,
    val start_date:String,
    val privilege_type:String,
    val usage_date:String,
    val status:String,
    val cdc:String,
)


