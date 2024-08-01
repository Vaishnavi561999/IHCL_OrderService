package com.ihcl.order.model.dto.response

import com.ihcl.order.model.schema.OrderStatus
import com.ihcl.order.model.schema.OrderType
import com.ihcl.order.model.schema.PaymentMethod
import com.ihcl.order.model.schema.PaymentStatus

data class EpicureNeucoinsRedemptionResponse(
    val orderId:String?,
    val customerHash:String,
    val orderType: OrderType,
    val gradTotal:Double?,
    val isRefundable:Boolean,
    val payableAmount:Double,
    val paymentInfo: List<PaymentInfo>?,
    val paymentMethod: PaymentMethod = PaymentMethod.PAY_ONLINE,
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    val orderStatus: OrderStatus = OrderStatus.PENDING,


    )
