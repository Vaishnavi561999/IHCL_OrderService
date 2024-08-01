package com.ihcl.order.model.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class PaymentDetailsDTO(
    var paymentType: String? = null,
    var paymentMethod: String? = null,
    var paymentMethodType: String? = null,
    var txnGateway: Int? = null,
    var txnId: String? = null,
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
    var expiryDate:String? = null,
    var ccAvenueTxnId: String? = null,
   /* @SerializedName("paymentType"       ) var paymentType       : String? = null,
    @SerializedName("paymentMethod"     ) var paymentMethod     : String? = null,
    @SerializedName("paymentMethodType" ) var paymentMethodType : String? = null,
    @SerializedName("txnId"             ) var txnId             : String? = null,
    @SerializedName("txnNetAmount"      ) var txnNetAmount      : Int?    = null,
    @SerializedName("txnStatus"         ) var txnStatus         : String? = null,
    @SerializedName("cardNumber"        ) var cardNumber        : String? = null*/
)
