package com.ihcl.order.model.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class BalanceEnquiryRequest(
    val balanceEnquiry: List<BalanceEnquiry>
)
@Serializable
data class BalanceEnquiry(
    val CardNumber: String
)

@Serializable
data class OriginalBalanceEnquiryRequest(
    val balanceEnquiry: List<BalanceEnquiryDto>
)
@Serializable
data class BalanceEnquiryDto(
    val CardNumber:String?,
    val CardPin:String?
)