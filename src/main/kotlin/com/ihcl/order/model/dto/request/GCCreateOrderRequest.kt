package com.ihcl.order.model.dto.request

data class GCCreateOrderRequest(
    val agreedTnc: Boolean?,
    val agreedPrivacyPolicy: Boolean?
)