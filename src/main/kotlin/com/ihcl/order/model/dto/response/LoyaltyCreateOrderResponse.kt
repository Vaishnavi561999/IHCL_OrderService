package com.ihcl.order.model.dto.response

import com.ihcl.order.model.dto.request.ExtraDataRequest
import com.ihcl.order.model.dto.request.User
import com.ihcl.order.model.schema.AddOnCardDetails
import kotlinx.serialization.Serializable

@Serializable
data class LoyaltyCreateOrderResponse(
    val orderId: String?,
    val customerHash:String?,
    val bankName: String,
    val isBankUrl: Boolean,
    val isShareHolder: Boolean,
    val memberShipPurchaseType: String,
    var gstNumber: String?,
    val address: String?,
    val country: String?,
    val dateOfBirth: String?,
    val extraData: ExtraDataRequest?,
    val mobile: String?,
    val pinCode: String?,
    val salutation: String?,
    val user: User?,
    val addOnCardDetails: AddOnCardDetails?,
    val priceSummary: LoyaltyPriceSummaryInfo
)

@Serializable
 data class LoyaltyPriceSummaryInfo(
    val price: Double?,
    val tax: Double?,
    var totalPrice: Double?,
    var neuCoins: Double,
    var totalPayableAmount: Double
)

