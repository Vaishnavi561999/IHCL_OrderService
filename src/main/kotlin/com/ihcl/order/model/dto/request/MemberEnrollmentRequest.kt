package com.ihcl.order.model.dto.request

import kotlinx.serialization.Serializable
import com.google.gson.annotations.SerializedName


@Serializable
data class MemberEnrollmentRequest(
    val address_line1: String?,
    val address_line2: String?,
    val user: UserDetails?,
    val country: String?,
    val date_of_birth: String?,
    val enrolling_location: String?,
    val enrolling_sponsor: Int?,
    val enrollment_channel: String?,
    val enrollment_touchpoint: Int?,
    val extra_data: ExtraData?,
    val gender: String?,
    val mobile: String?,
    val salutation: String?,
    @SerializedName("postal_code")
    var postalCode: String?
    )
@Serializable
data class UserDetails(
    val email: String?,
    val first_name: String?,
    val last_name: String?
)
@Serializable
data class ExtraData(
    val country_code: String?,
    val domicile: String?,
    val epicure_type: String?,
    val state: String?
)
