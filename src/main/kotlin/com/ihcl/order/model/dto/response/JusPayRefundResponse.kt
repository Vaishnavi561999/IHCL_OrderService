package com.ihcl.order.model.dto.response

import kotlinx.serialization.Serializable
@Serializable
data class JusPayRefundResponse(
    val card: Card?,
    val order_id: String?,
    val udf1: String?,
    val udf2: String?,
    val udf3: String?,
    val udf4: String?,
    val udf5: String?,
    val udf6: String?,
    val udf7: String?,
    val udf8: String?,
    val udf9: String?,
    val udf10: String?,
    val status: String?,
    val amount: Double?,
    val auth_type: String?,
    val refunded: Boolean?,
    val payment_method: String?,
    val gateway_id: Int?,
    val refunds: List<RefundData?>?,
    val payment_method_type: String?,
    val txn_uuid: String?,
    val customer_id: String?,
    val bank_pg: String?,
    val payment_links: PaymentLinks?,
    val effective_amount: Double?,
    val payment_gateway_response: PaymentGatewayResponse?,
    val product_id: String?,
    val txn_detail: TxnDetail?,
    val amount_refunded: Int?,
    val customer_email: String?,
    val currency: String?,
    val customer_phone: String?,
    val bank_error_message: String?,
    val id: String?,
    val txn_id: String?,
    val merchant_id: String?,
    val maximum_eligible_refund_amount: Double?,
    val date_created: String?,
    val bank_error_code: String?,
    val gateway_reference_id: String?,
    val return_url: String?,
    val status_id: String?
)
@Serializable
data class RefundData(
    val status: String?,
    val amount: Double?,
    val sent_to_gateway: Boolean?,
    val unique_request_id: String?,
    val error_code: String?,
    val created: String?,
    val initiated_by: String?,
    val refund_source: String?,
    val error_message: String?,
    val id: String?,
    val refund_type: String?,
    val ref: String?
)


