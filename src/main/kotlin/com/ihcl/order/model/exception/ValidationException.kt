package com.ihcl.order.model.exception

data class ValidationException(
    var statusCode: Int?,
    var errorMessage: Map<String,String>?,
    override val message: String?
) : Exception()

data class ErrorResponse(
    var statusCode: Int?,
    var errorMessage: Map<String,String>?,
    val message: String?
)
