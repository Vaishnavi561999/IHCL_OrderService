package com.ihcl.order.model.dto.request

import com.ihcl.order.model.dto.response.DailyRates
import com.ihcl.order.model.dto.response.Tax
import kotlinx.serialization.Serializable

@Serializable
data class DiscountRequest(
    val customerHash: String,
    val orderId: String,
    val discountPrices: Map<Int, Pair<Double,Double>>,
    val daily:List<DailyRates>,
    val tax: Tax
)
