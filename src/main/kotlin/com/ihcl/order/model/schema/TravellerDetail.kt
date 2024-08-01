package com.ihcl.order.model.schema

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kotlinx.serialization.Serializable

@JsonIgnoreProperties(ignoreUnknown = true)
@Serializable
data class TravellerDetail(
     val salutation:String?=null,
    val dateOfBirth: String,
    val address: String,
    val city: String,
    val countryCode: String,
    val customerId: String,
    val customerType: String,
    val email: String,
    val firstName: String,
    val gender: String,
    val gstNumber: String,
    val lastName: String,
    val membershipNumber: String,
    val mobile: String,
    val name: String,
    val secondaryContact: String,
    val state: String,
     val isCampaignOffer:Boolean,
     val offerIdentifier:String?
)