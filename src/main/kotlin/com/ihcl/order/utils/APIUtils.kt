package com.ihcl.order.utils

import com.fasterxml.jackson.databind.JsonMappingException
import com.ihcl.order.model.dto.HttpResponseData
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import java.util.*

private data class APIResponse<T : Any>(
    val path: String, val timestamp: Date, val statusCode: HttpStatusCode, val data: T
)
private data class APIConfirmResponse<T : Any>(
    val path: String, val timestamp: Date, val statusCode: HttpStatusCode, val data: T,val objectDetails:T
)

suspend fun <T : Any> ApplicationCall.fireHttpResponse(httpResponseData: HttpResponseData<T>) {
    respond(
        httpResponseData.statusCode,
        APIResponse(request.path(), Date(), httpResponseData.statusCode, httpResponseData.data)
    )
}

suspend fun <T : Any> ApplicationCall.fireHttpResponse(statusCode: HttpStatusCode, data: T) {
    respond(statusCode, APIResponse(request.path(), Date(), statusCode, data))
}
suspend fun <T : Any> ApplicationCall.fireHttpResponse(statusCode: HttpStatusCode, data: T,objectDetails:T) {
    respond(statusCode, APIConfirmResponse(request.path(), Date(), statusCode, data,objectDetails))
}

suspend fun ApplicationCall.invalidBodyStructure(exception: JsonMappingException) {
    val errorMessage: String =
        exception.message?.substringAfterLast("[")?.substringBeforeLast("]")?.plus(" is missing").toString()
            .replace("\"", "")
    fireHttpResponse(HttpStatusCode.BadRequest, errorMessage)
}

suspend fun ApplicationCall.generateCookies(call: ApplicationCall) : String{
    var cookies: String? = null
    call.request.cookies.rawCookies.forEach {
        if(it.key == ANONYMOUSCUSTOMERHASH) {
            cookies = decodeCookieValue(it.value, CookieEncoding.URI_ENCODING)
        }
    }
    return cookies!!
}
