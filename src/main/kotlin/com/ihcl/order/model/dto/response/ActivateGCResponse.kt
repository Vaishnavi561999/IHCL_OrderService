package com.ihcl.order.model.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class ActivateGCResponse(
    val cards: List<Card?>?,
    val currency: Currency?,
    val delivery: ACtivateDelivery?,
    val deliveryMode: String?,
    val products: ActivateProducts?,
    val total_cards: Int?
)

@Serializable
data class ACtivateDelivery(
    val summary: Summary?
)

@Serializable
data class ActivateEmail(
    val delivered: Int?,
    val failed: Int?,
    val inProgress: Int?,
    val totalCount: Int?
)

@Serializable
data class ProductName(
    val balanceEnquiryInstruction: String?,
    val cardBehaviour: String?,
    val images: Images?,
    val name: String?,
    val sku: String?,
    val specialInstruction: String?
)

@Serializable
data class ActivateProducts(
    val productName: ProductName?
)

@Serializable
data class SummarySms(
    val delivered: Int?,
    val failed: Int?,
    val inProgress: Int?,
    val totalCount: Int?
)

@Serializable
data class Summary(
    val delivered: Int?,
    val email: ActivateEmail?,
    val failed: Int?,
    val inProgress: Int?,
    val sent: Int?,
    val sms: SummarySms?,
    val totalCardsCount: Int?
)