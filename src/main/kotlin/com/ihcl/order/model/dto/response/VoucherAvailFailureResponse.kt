package com.ihcl.order.model.dto.response

data class VoucherAvailFailureResponse(
    val error: AvailPrivilegeError
)

data class AvailPrivilegeError(
    val code: String,
    val message: String,
    val scope: String,
)
