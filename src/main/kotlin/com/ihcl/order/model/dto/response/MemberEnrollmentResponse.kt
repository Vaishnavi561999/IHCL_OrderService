package com.ihcl.order.model.dto.response

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class MemberEnrollmentResponse(
    val address_line1: String?,
    val address_line2: String?,
    val country: Int?,
    val date_of_birth: String?,
    val enrolling_location: String?,
    val enrolling_sponsor: Int?,
    val enrollment_channel: String?,
    val enrollment_touchpoint: Int?,
    val extra_data: ExtraData?,
    val gender: String?,
    val member_id: String?,
    val mobile: String?,
    val salutation: String?,
    val user: User?
)
@Serializable
data class ExtraData(
    val country_code: String?,
    val domicile: String?,
    val epicure_type: String?,
    val state: String?
)
@Serializable
data class User(
    val email: String?,
    val first_name: String?,
    val last_name: String?
)
data class MemberLookupDTO(
    @SerializedName("member_id")
    val member_id:String
)
data class MemberErrorDTO(
    @SerializedName("error" ) var error : Error?
)
data class Error (

    @SerializedName("message"     ) var message    : String? = null,
    @SerializedName("code"        ) var code       : String? = null,
    @SerializedName("type"        ) var type       : String? = null,
    @SerializedName("status_code" ) var statusCode : Int?    = null,
    @SerializedName("data"        ) var data       : MemberEnrollErrorData?,
    @SerializedName("scope"       ) var scope      : String? = null

)
data class MemberEnrollErrorData (

    @SerializedName("member_id" ) var memberId : String? = null

)