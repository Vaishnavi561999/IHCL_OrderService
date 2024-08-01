package com.ihcl.order.model.dto.response

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class OrderStatusResponse(
    val amount: Double?,
    val amount_refunded: Double?,
    val auth_type: String?,
    val bank_error_code: String?,
    val bank_error_message: String?,
    val card: CardInformation?,
    val currency: String?,
    val customer_email: String?,
    val customer_id: String?,
    val customer_phone: String?,
    val date_created: String?,
    val effective_amount: Double?,
    val gateway_id: Int?,
    val gateway_reference_id: String?,
    val id: String?,
    val maximum_eligible_refund_amount: Double?,
    val merchant_id: String?,
    val metadata: Metadata?,
    val offers: List<String>?,
    val order_id: String?,
    val payment_gateway_response: PaymentGatewayResponse?,
    val payment_links: PaymentLinks?,
    val payment_method: String?,
    val payment_method_type: String?,
    val product_id: String?,
    val refunded: Boolean?,
    val resp_code: String?,
    val resp_message: String?,
    val return_url: String?,
    val status: String?,
    val status_id: Int?,
    val txn_detail: TxnDetail?,
    val txn_id: String?,
    val txn_uuid: String?,
    val udf1: String?,
    val udf10: String?,
    val udf2: String?,
    val udf3: String?,
    val udf4: String?,
    val udf5: String?,
    val udf6: String?,
    val udf7: String?,
    val udf8: String?,
    val udf9: String?
)
@Serializable
data class Metadata(
    @SerializedName("CCAVENUE_V2:merchant_param1")
    val CCAVENUE_V2_merchant_param1: String?,
    @SerializedName("CCAVENUE_V2:merchant_param2")
    val CCAVENUE_V2_merchant_param2: String?,
    @SerializedName("CCAVENUE_V2:merchant_param3")
    val CCAVENUE_V2_merchant_param3: String?,
    @SerializedName("CCAVENUE_V2:merchant_param4")
    val CCAVENUE_V2_merchant_param4: String?,
    @SerializedName("CCAVENUE_V2:merchant_param5")
    val CCAVENUE_V2_merchant_param5: String?,
    @SerializedName("CCAVENUE_V2:sub_account_id")
    val CCAVENUE_V2_sub_account_id: String?,
    @SerializedName("JUSPAY:gateway_reference_id")
    val JUSPAY_gateway_reference_id: String?,
    val payment_links: PaymentLinks?,
    val payment_locking: PaymentLocking?,
    val payment_page_client_id: String?
)
@Serializable
data class PaymentGatewayResponse(
    val auth_id_code: String?,
    val created: String?,
    val epg_txn_id: String?,
    val gateway_response: GatewayResponse?,
    val resp_code: String?,
    val resp_message: String?,
    val rrn: String?,
    val txn_id: String?
)
@Serializable
data class PaymentLinks(
    val iframe: String?,
    val mobile: String?,
    val web: String?
)
@Serializable
data class TxnDetail(
    val created: String?,
    val currency: String?,
    val error_code: String?,
    val error_message: String?,
    val express_checkout: Boolean?,
    val gateway: String?,
    val gateway_id: Int?,
    val net_amount: Double?,
    val offer_deduction_amount: Double?,
    val order_id: String?,
    val redirect: Boolean?,
    val status: String?,
    val surcharge_amount: Double?,
    val tax_amount: Double?,
    val txn_amount: Double?,
    val txn_flow_type: String?,
    val txn_id: String?,
    val txn_uuid: String?,
    val metadata: TxnDetailMetaData?
)
@Serializable
data class TxnDetailMetaData(
    val paymentChannel:String?
)
@Serializable
data class GatewayResponse(
    val amount: Double?,
    val bank_ref_no: String?,
    val billing_address: String?,
    val billing_city: String?,
    val billing_country: String?,
    val billing_email: String?,
    val billing_name: String?,
    val billing_notes: String?,
    val billing_state: String?,
    val billing_tel: String?,
    val billing_zip: String?,
    val bin_country: String?,
    val card_name: String?,
    val currency: String?,
    val delivery_address: String?,
    val delivery_city: String?,
    val delivery_country: String?,
    val delivery_name: String?,
    val delivery_state: String?,
    val delivery_tel: String?,
    val delivery_zip: String?,
    val discount_value: String?,
    val eci_value: String?,
    val failure_message: String?,
    val mer_amount: Double?,
    val merchant_param1: String?,
    val merchant_param2: String?,
    val merchant_param3: String?,
    val merchant_param4: String?,
    val merchant_param5: String?,
    val offer_code: String?,
    val offer_type: String?,
    val order_id: String?,
    val order_status: String?,
    val payment_mode: String?,
    val response_code: String?,
    val retry: String?,
    val status_code: String?,
    val status_message: String?,
    val sub_account_id: String?,
    val tracking_id: String?,
    val trans_date: String?,
    val vault: String?
)
@Serializable
data class CardInformation(
    val card_brand: String?,
    val card_fingerprint: String?,
    val card_isin: String?,
    val card_issuer: String?,
    val card_issuer_country: String?,
    val card_reference: String?,
    val card_type: String?,
    val expiry_month: String?,
    val expiry_year: String?,
    val extended_card_type: String?,
    val juspay_bank_code: String?,
    val last_four_digits: String?,
    val name_on_card: String?,
    val payment_account_reference: String?,
    val saved_to_locker: Boolean?,
    val token:Token?,
    val tokens: List<Token?>?,
    val using_saved_card: Boolean?,
    val using_token: Boolean?
)

@Serializable
data class Token(
    val card_reference:String?,
    val card_fingerprint:String?,
    val last_four_digits:String?,
    val card_isin:String?,
    val expiry_year:String?,
    val expiry_month:String?,
    val par:String?,
    val tokenization_status:String?,
    val provider:String?,
    val provider_category:String?
)
@Serializable
class PaymentLocking
