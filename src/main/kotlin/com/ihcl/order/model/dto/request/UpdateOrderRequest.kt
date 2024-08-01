package com.ihcl.order.model.dto.request

import com.ihcl.order.model.schema.PaymentDetail

data class UpdateOrderRequest(
    val orderId: String?,
    var paymentDetails: MutableList<PaymentDetail>?,
    var payableAmount: Double?,
    var balancePayable: Double?,
    var paymentMethod: String?,
    var isDepositAmount : Boolean?
)
