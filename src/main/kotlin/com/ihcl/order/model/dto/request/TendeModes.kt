package com.ihcl.order.model.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class TenderModes(
    val orderId: String,
    val tenderMode: TenderMode,
    val tenderModeDetails: MutableList<TenderModeDetails>
)

@Serializable
data class TenderModeDetails(
    var cardNumber: String?,
    var cardPin: String?,
    var amount : Double?
)

