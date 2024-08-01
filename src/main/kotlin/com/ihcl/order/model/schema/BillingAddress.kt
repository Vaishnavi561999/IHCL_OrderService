package com.ihcl.order.model.schema

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kotlinx.serialization.Serializable

@JsonIgnoreProperties(ignoreUnknown = true)
@Serializable
data class BillingAddress(
    val address1: String,
    val address2: String,
    val address3: String,
    val city: String,
    val country: String,
    val firstName: String,
    val lastName: String,
    val pinCode: String,
    val state: String,
    val phoneNumber: String,
    val countyCodeISO: String
)