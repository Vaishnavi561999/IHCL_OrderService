package com.ihcl.order.model.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class QCCreateBooking(
    val address: AddressDto?,
    val billing: Billing?,
    val deliveryMode: String?,
    val remarks: String?,
    val payments: List<PaymentDto?>?,
    val products: List<Product?>?,
    val refno: String?

)
@Serializable
data class AddressDto(
    val billToThis: Boolean?,
    val city: String?,
    val company: String?,
    val country: String?,
    val email: String?,
    val firstname: String?,
    val lastname: String?,
    val line1: String?,
    val line2: String?,
    val postcode: String?,
    val region: String?,
    val telephone: String?
)
@Serializable
data class Billing(
    val city: String?,
    val company: String?,
    val country: String?,
    val email: String?,
    val firstname: String?,
    val lastname: String?,
    val line1: String?,
    val line2: String?,
    val postcode: String?,
    val region: String?,
    val telephone: String?
)
@Serializable
data class PaymentDto(
    val amount: Int?,
    val code: String?
)
@Serializable
data class Product(
    val currency: String?,
    val price: Int?,
    val qty: Int?,
    val sku: String?,
    val theme: String?,
    val payout:PayOut,
    val giftMessage: String?
)
@Serializable
data class PayOut(
    val id: String
)