package com.ihcl.order.model.dto.request

data class RedeemGiftCardRequest(
    val billAmount:Double,
    val propertyName:String,
    val amount: String,
    val cardNumber: String,
    val cardPin: String,
    val invoiceNumber: String,
    val idempotencyKey: String
)