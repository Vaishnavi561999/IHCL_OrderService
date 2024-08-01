package com.ihcl.order.plugins

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonMappingException
import com.ihcl.order.model.dto.BadRequest
import com.ihcl.order.model.dto.GravtyVoucherNotFoundDto
import com.ihcl.order.model.dto.InternalServerErrorDto
import com.ihcl.order.model.dto.TimeOutException
import com.ihcl.order.model.exception.*
import com.ihcl.order.utils.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import kotlinx.serialization.SerializationException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeoutException
const val ERROR_MESSAGE="error {} "
fun Application.configureStatusPages() {
    val log: Logger = LoggerFactory.getLogger(javaClass)

    install(StatusPages) {
        exception<JsonMappingException> { call,cause ->
            log.error(ERROR_MESSAGE,cause.message)
            cause.printStackTrace()
            call.invalidBodyStructure(cause)
            throw cause
        }
        exception<JsonParseException> { call,cause ->
            log.error(ERROR_MESSAGE,cause.message)
            cause.printStackTrace()
            call.fireHttpResponse(HttpStatusCode.BadRequest, INVALID_REQUEST_STRUCTURE_ERR_MSG)
            throw cause
        }
        exception<SerializationException> { call,cause ->
            log.error(ERROR_MESSAGE,cause.message)
            cause.printStackTrace()
            call.fireHttpResponse(HttpStatusCode.ExpectationFailed, SERIALIZE_ERR_MSG)
            throw cause
        }
        exception<HttpResponseException> { call, cause ->
            log.error(ERROR_MESSAGE,cause.message)
            cause.printStackTrace()
            call.fireHttpResponse(cause.statusCode, cause.data)
        }
        exception<ResponseException> { call, cause ->
            log.error(ERROR_MESSAGE,cause.message)
            cause.printStackTrace()
            call.fireHttpResponse(cause.statusCode, cause.data.toString(),cause.objectDetails)
        }
        exception<TimeoutException> { call,cause ->
            log.error(ERROR_MESSAGE,cause.message)
            cause.printStackTrace()
            call.fireHttpResponse(HttpStatusCode.RequestTimeout, CLIENT_UNAVAILABLE_ERR_MSG)
            throw cause
        }
        exception<Throwable> { call,cause ->
            log.error(ERROR_MESSAGE, cause.message)
            log.error(ERROR_MESSAGE, cause.stackTraceToString())
            call.fireHttpResponse(HttpStatusCode.InternalServerError, SOMETHING_WENT_WRONG_ERR_MSG)
            throw cause
        }
        exception<QCInternalServerException> { call,cause ->
            log.error(ERROR_MESSAGE, cause.message)
            call.respond(HttpStatusCode.OK, InternalServerErrorDto(
               "Order created Successfully, Card details will be shared via email",
               "${cause.message}"))
        }

        exception<OrderInternalServerException> { call, cause ->
            log.error(ERROR_MESSAGE, cause.message)
            call.respond(HttpStatusCode.OK, InternalServerErrorDto(
                "Orders are not there for customer...",
                "${cause.message}"))
        }
        exception<QCTimeOutException> { call, cause ->
            log.error(ERROR_MESSAGE,cause.message)
            call.respond(HttpStatusCode.RequestTimeout, TimeOutException(
                SOMETHING_WENT_WRONG_ERR_MSG, "${cause.message}"
            )
            )
        }
        exception<QCOrderNotAvailableException> { call, cause ->
            log.error(ERROR_MESSAGE,cause.message)
            call.respond(HttpStatusCode.RequestTimeout, TimeOutException(
                SOMETHING_WENT_WRONG_ERR_MSG, "${cause.message}"
            )
            )
        }
        exception<GravtyVoucherNotFoundException> { call, cause ->
            log.error(ERROR_MESSAGE, cause.message)
            call.respond(HttpStatusCode.NotFound, GravtyVoucherNotFoundDto(
                GRAVTY_VOUCHER_NOT_FOUND))
        }
        exception<ValidationException> { call, cause ->
            log.error("Validation error occurred: ${cause.errorMessage}")
            call.respond(
                HttpStatusCode.BadRequest, ErrorResponse(
                    cause.statusCode!!.toInt(),
                    cause.errorMessage,
                    cause.message
                )
            )
        }
        exception<PrimaryCardCreationException> { call, cause ->
            log.error("BadRequest error occurred: ${cause.message} ${cause.stackTraceToString()}")
            call.respond(HttpStatusCode.BadRequest, BadRequest(cause.message!!))

        }

    }
}