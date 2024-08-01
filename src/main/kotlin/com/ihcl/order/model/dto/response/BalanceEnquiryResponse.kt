package com.ihcl.order.model.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class BalanceEnquiryResponse(
    val Cards: List<BalanceEnquiryCards>?,
    val ResponseCode: String,
    val ResponseMessage: String
)
@Serializable
data class BalanceEnquiryCards(
    val Balance: Int,
    val CardNumber: String,
    val CardType: String,
    val ResponseCode: String,
    var ResponseMessage: String?,
    val TotalRedeemedAmount: Double,
    val TotalReloadedAmount: Double
)