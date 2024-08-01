package com.ihcl.order.model.schema

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kotlinx.serialization.Serializable

@JsonIgnoreProperties(ignoreUnknown = true)
@Serializable
data class Address(
    val city: String,
    val contactNumber: String,
    val directions: String,
    val landmark: String,
    val lat: String,
    val long: String,
    val mapLink: String,
    val pinCode: String,
    val state: String,
    val street: String
)