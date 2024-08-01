package com.ihcl.order.model.dto.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.ihcl.order.model.schema.PaymentDetail
import kotlinx.serialization.Serializable
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class LoyaltyCartResponse(
    var _id: String,
    val items: LoyaltyCartItems,
    var paymentDetails: MutableList<PaymentDetail>?,
    var priceSummary : LoyaltyPriceSummary,
    var paymentMethod: String? = null,
    val createdTimestamp: Date = Date(),
    var modifiedTimestamp: Date = Date()
)

data class LoyaltyCartItems(
    val epicureDetails: EpicureDetails,
)
@JsonIgnoreProperties(ignoreUnknown = true)
data class EpicureDetails(
    val bankName: String,
    val epicureType: String,
    val gravityVoucherCode: String? = null,
    val gravityVoucherPin: String? = null,
    val isBankUrl: Boolean,
    val isShareHolder: Boolean,
    val isTata: Boolean = false,
    val memberShipPurchaseType: String,
    val offerCode:String? = null,
    val offerName:String? = null
)
@Serializable
data class LoyaltyPriceSummary(
    val price: Double,
    val tax: Double,
    val discountPercent: Int,
    val discountPrice: Double,
    val discountTax: Double,
    var totalPrice: Double?,
    var neuCoins: Double,
    var totalPayableAmount: Double
)














