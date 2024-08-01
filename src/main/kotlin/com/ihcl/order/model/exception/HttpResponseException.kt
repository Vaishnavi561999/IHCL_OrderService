package com.ihcl.order.model.exception

import io.ktor.http.*

class HttpResponseException(val data: Any, val statusCode: HttpStatusCode) : Exception()
class ResponseException(val data:String?,val objectDetails:Any,val statusCode: HttpStatusCode) : Exception()

data class SSOErrorResponse(
    val errorCode: String,
    val errorReason: String,
    val errorMessage: String

)