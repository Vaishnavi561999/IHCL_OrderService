package com.ihcl.order.model.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class ConfirmOrder(
    val orderId: String?
)

@Serializable
data class RefreshTokenResponse(
    val message: String?
)