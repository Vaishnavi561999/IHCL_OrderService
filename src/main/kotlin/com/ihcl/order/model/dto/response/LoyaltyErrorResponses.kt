package com.ihcl.order.model.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class LoyaltyErrorResponses(
    var error: Errorss?
)
@Serializable
data class Errorss (
    var mobile: List<ErrorObject>?,
    var email: List<ErrorObject>?,
    var gender: List<ErrorObject>?,
    var salutation: List<ErrorObject>?,
    var country: List<ErrorObject>?,
    var date_of_birth: List<ErrorObject>?,
    var scope: String,
    var message: String,
    var code: String,
    var type: String,
    var status_code: String,
    var data: DataError,
    var enrollment_touchpoint: List<ErrorObject>?,
    var non_field_errors: List<ErrorObject>?,
    var enrollment_channel: List<ErrorObject>?,
    var user: UserError,
)

@Serializable
data class UserError(
    var first_name: List<ErrorObject>?,
    var last_name: List<ErrorObject>?,
)
@Serializable
data class DataError(
    var member_id:String
)

@Serializable
data class ErrorObject(
    var message: String,
    var code: String
)
data class GravtyErrorDTO(
    var error:GravtyError
)
data class GravtyError(
    var message:String
)
data class ValidationLookupDTO(
    val id:String
)