package com.ihcl.order.model.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class OrderBuyGiftCardExceptionDto(
    val message: String
)