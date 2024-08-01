package com.ihcl.order.model.dto.response

import com.ihcl.order.model.schema.AddOnCardDetails
import kotlinx.serialization.Serializable
data class LoyaltyPurcchaseSuccessResponse(
    val purchaseOrderNumber:String,
    val membershipId:String,
    val buyerName:String?,
    val emailId:String?,
    val mobileNumber:String?,
    val dateOfBirth:String?,
    val gstNumber:String?,
    val paymentMethod:String?,
    val cardNumber:String?,
    //product.price
    val neuCoinsRedeemed:String?,
    val finalAmountPaid:String?,
    val address:LoyaltyAddress?,
    val products:List<Product?>?,
)

@Serializable
data class ConfirmLoyaltyResponse(
    val purchaseOrderNumber:String,
    val orderId:String?,
    val membershipId:String?,
    val buyerName:String?,
    val emailId:String?,
    val mobileNumber:String?,
    val dateOfBirth:String?,
    val gstNumber:String?,
    val paymentMethod:String?,
    val cardNumber:String?,
    val address: LoyaltyAddress?,
    val products: List<Product?>?,
    val orderStatus:String?,
    val nameOnCard:String?,
    val neuCoinsRedeemed:String?,
    val priceBreakUp: LoyaltyPriceSummary?,
    val addOnCardDetails: AddOnCardDetails?,
    val paymentTransactionId: String?
)
@Serializable
data class Product(
    val type:String?,
    val price:String?,
    val tax: String?,
    val quantity:String?,
    val total:String?
)
@Serializable
data class LoyaltyAddress(
    val address1:String?,
    val city:String?,
    val state:String?,
    val pincode:String?,
    val country:String?
)