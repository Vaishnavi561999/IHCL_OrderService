package com.ihcl.order.model.schema

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.google.gson.annotations.SerializedName
import com.ihcl.order.model.dto.request.VoucherRedemptionAvailPrivileges
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.Date
@Serializable
@JsonIgnoreProperties(ignoreUnknown = true)
data class Order(
    val orderId: String,
    val customerHash: String,
    val customerEmail: String,
    val customerId: String,
    val customerMobile: String,
    val channel: String,
    val currencyCode: String,
    val discountAmount: Double,
    val basePrice: Double?,
    val taxAmount: Double?,
    val gradTotal: Double,
    var payableAmount: Double,
    val isRefundable: Boolean,
    val orderType: OrderType,
    val transactionId: String?,
    val billingAddress: BillingAddress?,
    val offers: List<Offers>?,
    val orderLineItems: MutableList<OrderLineItem>,
    var modifyBookingCount: Int,
    var paymentDetails: TransactionInfo?,
    var paymentMethod: String,
    var paymentStatus: String,
    var orderStatus: String,
    var transactionType: String?,
    val refundAmount: Double,
    var transactionStatus: String?,
    val agreedTnc: Boolean? = null,
    val agreedPrivacyPolicy: Boolean? = null,
    var retryCount: Int = 0,
    @SerializedName("createdTimestamp")
    @Contextual
    var createdTimestamp: Date? = null,
    @SerializedName("modifiedTimestamp")
    @Contextual
    var modifiedTimestamp: Date? = Date(),
    var bookingCancelRemarks: String?,
    var brandName :String
)
@Serializable
@JsonIgnoreProperties(ignoreUnknown = true)
data class OrderLineItem(
    val hotel: Hotel?,
    val giftCard: GiftCard?,
    val loyalty:Loyalty?
)
@Serializable
@JsonIgnoreProperties(ignoreUnknown = true)
data class Hotel(
    val addOnDetails: List<AddOnDetail>?,
    val address: Address?,
    var bookingNumber: String?,
    val category: String,
    val hotelId: String?,
    val invoiceNumber: String,
    val invoiceUrl: String,
    val name: String?,
    val reservationId: String,
    val roomCount:Int,
    var adultCount:Int?,
    var childrens:Int,
    var checkIn: String?,
    var checkOut:String?,
    val promoCode:String?,
    val promoType:String?,
    var rooms: List<Room>?,
    var status: String,
    val mobileNumber: String?,
    var country: String?,
    var storeId: String?,
    val hotelSponsorId:String?,
    var synxisId: String?,
    val emailId: String?,
    val specialRequest: String?,
    var totalDepositAmount: Double?,
    var balancePayable: Double?,
    var isDepositAmount: Boolean?,
    var isDepositPaid: Boolean? = false,
    var isDepositFull: Boolean? = false,
    val voucherRedemption: VoucherRedemptionAvailPrivileges?,
    val neupassMembershipId:String?,
    var revisedPrice: Double?,
    var grandTotal: Double?,
    var totalBasePrice: Double?,
    var totalTaxPrice: Double?,
    var amountPaid: Double?,
    var payableAmount: Double?,
    var refundAmount: Double?,
    var oldTotalBasePrice: Double?,
    var oldTotalTaxPrice: Double?,
    var oldGrandTotal: Double?,
    val totalPriceChange: Double?,
    val totalTaxChange: Double?,
    var totalCouponDiscountValue: Double?,
    var totalCancelPayableAmount:Double? = 0.0,
    var totalCancelRefundableAmount:Double? = 0.0,
    var totalCancellationPenaltyAmount: Double? = 0.0,
    var totalCancellationPaidAmount: Double? = 0.0,
    var complementaryBasePrice:Double? = 0.0,
    var bookingNoOfNights:Int?,
    val voucherNumber:String?,
    val voucherPin:String?,
    var isSeb: Boolean? = false,
    var sebRequestId: String? = null

)
@Serializable
@JsonIgnoreProperties(ignoreUnknown = true)
data class PaymentDetail(
    var paymentType: String?,
    var paymentMethod: String?,
    var paymentMethodType: String?,
    var txnGateway: Int?,
    var txnId: String?,
    var ccAvenueTxnId: String?,
    var txnNetAmount: Double?,
    var txnStatus: String?,
    var txnUUID: String?,
    var cardNo: String?,
    var nameOnCard: String?,
    var userId: String?,
    var redemptionId: String?,
    var pointsRedemptionsSummaryId: String?,
    var externalId: String?,
    var cardNumber: String?,
    val cardPin: String?,
    var preAuthCode: String?,
    var batchNumber: String?,
    var approvalCode: String?,
    var transactionId: Int?,
    var transactionDateAndTime: String?,
    var expiryDate:String?
)
@Serializable
@JsonIgnoreProperties(ignoreUnknown = true)
data class Offers(
    val offerAmount: Double,
    val offerName: String,
    val offerType: String
)
@Serializable
data class Loyalty(
    val memberCardDetails: MemberCardDetails,
    val membershipDetails: MembershipDetails,
    val shareHolderDetails: ShareHolderDetails?,
    val memberShipPurchaseType: String? = null,
    val isBankUrl: Boolean = false,
    val isShareHolder: Boolean = false,
    val isTata: Boolean? = false,
    val gravityVoucherCode: String?,
    val gravityVoucherPin: String?,
    val epicureFiestaOfferName: String? = null,
    val epicureFiestaOfferCode: String? = null,
)
@Serializable
data class ShareHolderDetails(
    val membershipPlanName: String?,
    val membershipPlanCode: String?,
    val membershipPlanType: String?,
    val bankName: String?
)
@Serializable
data class MemberCardDetails(
    val enrolling_location: String,
    val enrolling_sponsor: Int,
    val enrollment_channel: String,
    val enrollment_touchpoint: Int,
    val extra_data: ExtraData,
    val epicure_price:Double,
    val taxAmount: Double,
    val discountPercent: Int? = null,
    val discountPrice: Double? = null,
    val discountTax: Double? = null,
    val addOnCardDetails: AddOnCardDetails?
)
@Serializable
data class ExtraData(
    val country_code: String,
    val domicile: String,
    val epicure_type: String,
    val state: String,
    val country:String,
    val gstNumber:String
)
@Serializable
data class MembershipDetails(
    val memberId: String,
    val mobile: String,
    val user: User,
    val addOnCardDetails: AddOnCardDetails?
)
@Serializable
data class User(
    val email: String,
    val first_name: String,
    val last_name: String,
    val gender:String?,
    val salutation:String,
    val date_of_birth:String,
    val address: String,
    val pincode:String
)
@Serializable
@JsonIgnoreProperties(ignoreUnknown = true)
data class TransactionInfo(
    var transaction_1: MutableList<PaymentDetail>?,
    var transaction_2: MutableList<PaymentDetail>?,
    var transaction_3: MutableList<PaymentDetail>?,
    var transaction_4: MutableList<PaymentDetail>?
)

enum class PaymentStatus {
    FAILED, PENDING, SUCCESS, CANCELLED, REFUND_INITIATED, PARTIAL_REFUND_INITIATED, REFUND_SUCCESSFUL, REFUND_REJECTED, REFUND_FAILED,
    PARTIAL_REFUND_SUCCESSFUL, FULL_REFUND_SUCCESSFUL, CHARGED, AUTHORIZATION_FAILED
}

enum class OrderStatus {
    AWAITING_CONFIRMATION, PENDING, CREATED, ALLOCATED, CANCEL_INITIATED, CANCELLATION_REJECTED, CANCELLED, FAILED, REFUND_INITIATED, REFUND_SUCCESSFUL, REFUND_REJECTED,
    PARTIAL_REFUND_INITIATED, FULL_REFUND_INITIATED, PARTIAL_REFUND_SUCCESSFUL, FULL_REFUND_SUCCESSFUL, SUCCESS,PROCESSING,CONFIRMED
}

enum class OrderType {
    HOTEL_BOOKING, GIFT_CARD_PURCHASE, RESTAURANTS, SPA, RELOAD_BALANCE, MEMBERSHIP_PURCHASE, HOLIDAYS
}
enum class PaymentMethod {
    PAY_ONLINE, PAY_AT_HOTEL
}
enum class GiftCardStatus {
    COMPLETE, PROCESSING
}
enum class BookingStatus {
    AWAITING_CONFIRMATION, PENDING, BOOKED, PARTIALLY_CONFIRMED, ALLOCATED, CANCEL_INITIATED, CANCELLATION_REJECTED, CANCELLED, FAILED, PARTIAL_CANCELLED
}
data class TotalTransactionSummary(
    val transactions: List<Any>,
)

data class TransactionSummary(
    val createdTimestamp: Date?,
    val transactionId: String?,
    val gradTotal: Double?,
    val payableAmount:Double?,
    val discountAmount: Double?,
    val orderType: OrderType?,
    val transactionDetails: List<TransactionDetails?>
)
data class TransactionDetails(
    val name: Any,
    val theme: String?,
    val paymentDetails: List<PaymentsDetails?>,
)
data class PaymentsDetails(
    val paymentType: String,
    val txnNetAmount: Double?
)
data class ItineraryNumber(
    val itineraryNumber:String?
)
data class FetchBookingReq(val email: String?, val mobile:String?,val itineraryNumber:String?)

