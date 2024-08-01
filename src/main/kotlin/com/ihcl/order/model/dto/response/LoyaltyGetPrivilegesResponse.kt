package com.ihcl.order.model.dto.response

import com.google.gson.annotations.SerializedName
data class Vouchers(
    var count:Int,
    var pendingVouchers:List<LoyaltyGetPrivilegesResponse>,
)

data class LoyaltyGetPrivilegesResponse(
    @SerializedName("memberID")
    var memberID: String?,
    @SerializedName("privilegeCode")
    var privilegeCode: String?,
    @SerializedName("uniquePrivilegeCode")
    var uniquePrivilegeCode: String?,
    var extraData: PrivilegeExtraData,
    @SerializedName("productName")
    var productName: String?,
    @SerializedName("productDescription")
    var productDescription: String?,
    @SerializedName("productCategory")
    var productCategory: String?,
    @SerializedName("offerName")
    var offerName: String?,
    @SerializedName("offerType")
    var offerType: String?,
    @SerializedName("offerDesc")
    var offerDesc: String?,
    @SerializedName("offerTitle")
    var offerTitle: String?,
    @SerializedName("cdc")
    var cdc: String?,
    @SerializedName("offer_desktop_image")
    var offerDesktopImage: String?,
    @SerializedName("offer_mobile_image")
    var offerMobileImage: String?,
    @SerializedName("validTill")
    var validTill: String?,
    var isReedemable: Boolean,
    @SerializedName("pin")
    var pin: String?,
    @SerializedName("createdOn")
    var createdOn: String?,
    @SerializedName("status")
    var status: String?,
    var label:String?,
    var labelType:String?

)
data class PrivilegeExtraData(
    var program_prefix:String?,
    var product_code:String?,
    var promocode:String?
)
