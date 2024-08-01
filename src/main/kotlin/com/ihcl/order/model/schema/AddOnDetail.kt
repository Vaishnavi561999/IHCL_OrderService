package com.ihcl.order.model.schema

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kotlinx.serialization.Serializable

@JsonIgnoreProperties(ignoreUnknown = true)
@Serializable
data class AddOnDetail(
    val addOnCode: String,
    val addOnDesc: String,
    val addOnName: String,
    val addOnPrice: Double,
    val addOnType: String
)