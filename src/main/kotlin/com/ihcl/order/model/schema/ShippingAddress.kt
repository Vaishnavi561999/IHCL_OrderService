package com.ihcl.order.model.schema

import kotlinx.serialization.Serializable

@Serializable
data class ShippingAddress(
    val address1: String,
    val address2: String,
    val address3: String,
    val city: String,
    val country: String,
    val firstName: String,
    val lastName: String,
    val pinCode: String,
    val state: String,
    val phoneNumber: Long,
    val countyCodeISO: String
)