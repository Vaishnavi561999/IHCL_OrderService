package com.ihcl.order.model.dto.request

import kotlinx.serialization.Serializable

data class StatesDto(
    val country: String,
    val states: List<String>
)
data class CitiesDto(
    val state: String,
    val cities: List<String>
)
@Serializable
data class CountriesDto(
    val _id: String,
    val countries: List<String>
)