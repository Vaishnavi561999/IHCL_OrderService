package com.ihcl.order.model.dto.request

data class BankUrlValuesRequest(
    val _id:String,
    val bankUrlValues: List<BankUrlDetails>
)
data class BankUrlDetails(
    val membershipPlanName:String?,
    val membershipPlanCode: String?,
    val bankUrlName:String?,
    val bankUrlCode:String?
)