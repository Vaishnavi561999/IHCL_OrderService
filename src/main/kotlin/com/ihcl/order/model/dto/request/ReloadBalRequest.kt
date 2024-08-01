package com.ihcl.order.model.dto.request

import com.ihcl.order.utils.ValidatorUtils.notEmpty
import io.konform.validation.Validation
import kotlinx.serialization.Serializable

@Serializable
data class ReloadBalRequest(
    val cardNumber: String,
    val amount: Double,
    val invoiceNumber: String? = null,
    val agreedTnc: Boolean?,
    val agreedPrivacyPolicy: Boolean?
)

data class LoyaltyReloadRequest(
    val cardNumber: String,
    val amount: Double,
    val invoiceNumber: String? = null,
    val idempotencyKey: String?
)
val validateReloadBalRequest=Validation{
    ReloadBalRequest::cardNumber required {notEmpty()}
    ReloadBalRequest::amount required {}
}
