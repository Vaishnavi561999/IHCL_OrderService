package com.ihcl.order.model.dto.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.ihcl.order.model.schema.OrderStatus
import com.ihcl.order.model.schema.OrderType
import com.ihcl.order.model.schema.PaymentMethod
import com.ihcl.order.model.schema.PaymentStatus
import kotlinx.serialization.Serializable

@JsonIgnoreProperties(ignoreUnknown = true)
@Serializable
data class GiftCardResponse(
    val orderId: String?,
    val customerHash: String,
    val orderType: OrderType,
    val customerEmail : String,
    val customerId: String,
    val customerMobile: String,
    val channel: String,
    val currencyCode: String,
    val discountAmount: Double,
    val gradTotal: Double?,
    val isRefundable: Boolean,
    val payableAmount: Double,
    val transactionId: String?,
    val theme: String?,
    val billingAddress: GiftCardBillingAddress,
    val offers: List<OffersForGiftCard>,
    val orderItems: List<OrderItem>,
    val paymentInfo: List<PaymentInfo>?,
    val paymentMethod: PaymentMethod = PaymentMethod.PAY_ONLINE,
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    val orderStatus: OrderStatus = OrderStatus.PENDING,
    val refundAmount: Double,
    val taxAmount: Double
)
@JsonIgnoreProperties(ignoreUnknown = true)
@Serializable
data class OrderItem(
    val shippingAddress: GiftCardShippingAddress,
    val invoiceNumber: String?,
    val invoiceUrl: String?,
    val name: String?,
    val status: String?,
    val giftCard: GiftCardDto
)
@Serializable
data class GiftCardDto(
    val deliveryMethods: DeliveryMethodsInfo,
    val giftCardDetails: List<GiftCardInfo>?,
    val quantity: Int?,
    val receiverAddress: ReceiverAddressDetails,
    val receiverDetails: ReceiverInfo,
    val senderDetails: SenderInfo
)
@Serializable
data class GiftCardInfo(
    val amount: Double?,
    val sku: String?,
    val type: String?
)
@Serializable
data class ReceiverAddressDetails(
    val addressLine1: String?,
    val addressLine2: String?,
    val city: String?,
    val country: String?,
    val pinCode: String?,
    val state: String?
)
@Serializable
data class ReceiverInfo(
    val email: String?,
    val firstName: String?,
    val lastName: String?,
    val message: String?,
    val phone: String?,
    val rememberMe: Boolean?,
    val scheduleOn: String?
)
@Serializable
data class SenderInfo(
    val email: String?,
    val firstName: String?,
    val lastName: String?,
    val phone: String?,
    val registerAsNeuPass: Boolean?
)
@Serializable
data class DeliveryMethodsInfo(
    val phone: String?,
    val email: Boolean?,
    val smsAndWhatsApp: Boolean?
)
@JsonIgnoreProperties(ignoreUnknown = true)
@Serializable
data class PaymentInfo(
    val paymentType:String?,
    val paymentMethod: String?,
    val paymentMethodType: String?,
    val txnGateway: String,
    val txnId: String,
    val txnNetAmount: Double?,
    val txnStatus: String?,
    val txnUUID: String
)
@JsonIgnoreProperties(ignoreUnknown = true)
@Serializable
data class OffersForGiftCard(
    val offerAmount: Double?,
    val offerName: String?,
    val offerType: String?
)
@JsonIgnoreProperties(ignoreUnknown = true)
@Serializable
data class GiftCardBillingAddress(
    val address1: String?,
    val address2: String?,
    val address3: String?,
    val city: String?,
    val country: String?,
    val firstName: String?,
    val lastName: String?,
    val pinCode: String?,
    val state: String?,
    val phoneNumber: String?,
    val countyCodeISO: String?
)
@Serializable
data class GiftCardShippingAddress(
    val address1: String?,
    val address2: String?,
    val address3: String?,
    val city: String?,
    val country: String?,
    val firstName: String?,
    val lastName: String?,
    val pinCode: String?,
    val state: String?,
    val phoneNumber: String?,
    val countyCodeISO: String?
)




