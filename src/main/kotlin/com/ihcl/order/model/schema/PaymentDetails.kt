package com.ihcl.order.model.schema

import kotlinx.serialization.Serializable

@Serializable
data class PaymentDetails(
    var paymentType: String? = null,
    var paymentMethod: String? = null,
    var paymentMethodType: String? = null,
    var txnGateway: Int? = null,
    var txnId: String? = null,
    var ccAvenueTxnId: String? = null,
    var txnNetAmount: Double? = null,
    var txnStatus: String? = null,
    var txnUUID: String? = null,
    var cardNo: String? = null,
    var nameOnCard: String? = null,
    var cardNumber: String? = null,
    var cardPin: String? = null,
    var preAuthCode: String? = null,
    var batchNumber: String? = null,
    var approvalCode: String? = null,
    var transactionId: Int? = null,
    var transactionDateAndTime: String? = null,
    var userId: String? = null,
    var redemptionId: String? = null,
    var pointsRedemptionsSummaryId: String? = null,
    var externalId: String? = null,
)
