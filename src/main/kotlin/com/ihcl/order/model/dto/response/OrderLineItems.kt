package com.ihcl.order.model.dto.response

import com.ihcl.order.model.dto.request.VoucherRedemptionAvailPrivileges
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class  OrderLineItems(
val paymentLabels: PaymentLabels?,
val cartDetails:CartResponse
)
@Serializable
data class PaymentLabels(
    var payNow: Boolean = false,
    var payAtHotel: Boolean = false,
    var isInternational: Boolean = false,
    var confirmBooking: Boolean = false,
    var payDeposit: Boolean = false,
    var payFull: Boolean = false,
    var gccRemarks: String? = null,
    var depositAmount: Double? = null
)
@Serializable
data class CartResponse(
    var cartId: String,
    val items : List<CartResponseItems>?,
    val paymentSummary : PriceSummary?,
    var paymentDetails: MutableList<PaymentDetailsInfo>?,
    var modifiedPaymentDetails: ModifiedPayment?,
    val totalPriceChange: Double?,
    val totalTaxChange: Double?,
    val basePrice: Double?,
    val tax : Double?,
    val totalPrice : Double?,
    var totalDepositAmount : Double,
    var totalCouponDiscountValue : Double,
    var balancePayable: Double?,
    var isDepositAmount : Boolean?,
    val modifiedPayableAmount: Double? = null,
    val refundableAmount: Double? = null,
    val payableAmount : Double?,
    val createdDate: String?,
    val modifiedDate:  String?
)

@Serializable
data class ModifiedPayment(
    var modifiedBasePrice: Double?,
    var modifiedTax : Double?,
    var modifiedTotalPrice : Double?,
    var modifiedPayableAmount : Double?
)

@Serializable
data class CartResponseItems(
    val category: String,
    val hotel: MutableList<HotelDetails>
)

@Serializable
data class HotelDetails(
    val hotelId: String,
    val hotelName: String,
    val hotelAddress: String,
    val pinCode: String,
    val state: String,
    val checkIn: String,
    val checkOut: String,
    val bookingNumber: String,
    val promoCode:String?,
    val promoType:String?,
    val room: MutableList<RoomDetails>,
    val mobileNumber: String?,
    val emailId: String?,
    val voucherRedemption: VoucherRedemptionAvailPrivileges?,
    var revisedPrice: Double?,
    var grandTotal: Double?,
    var totalBasePrice: Double?,
    var totalTaxPrice: Double?,
    var amountPaid: Double?,
    var country: String?,
    var storeId: String?,
    val hotelSponsorId:String?,
    var synxisId: String?,
    var complementaryBasePrice:Double?,
    var isSeb: Boolean? = false,
    var sebRequestId: String? = null


)
@Serializable
data class RoomDetails(
    val isPackageCode: Boolean?,
    val roomId: String,
    val roomType: String,
    val roomName: String,
    val cost: Double,
    val checkIn: String,
    val checkOut: String,
    val tax: Tax,
    val bookingPolicyDescription: String?,
    val daily:List<DailyRates>?,
    val cancelPolicyDescription: String?,
    val description: String?,
    val detailedDescription: String?,
    var children: Int?,
    var adult: Int?,
    val rateCode: String,
    val currency: String,
    val rateDescription: String,
    val roomDescription: String,
    val packageCode:String?,
    val packageName: String,
    var roomImgUrl : String,
    val roomNumber: Int,
    val status: String?,
    var cancellationId: String?,
    var cancellationTime: String?,
    var penaltyApplicable: Boolean?,
    var cancelRemark: String?,
    val changePrice:Double?,
    val changeTax:Double?,
    val confirmationId: String,
    var roomCode: String?,
    var roomDepositAmount: Double?,
    var noOfNights:Int?,
    val modifyBooking: ModifyBookingDetails?,
    val grandTotal: Double,
    var paidAmount: Double?,
    var penaltyAmount: Double?,
    var penaltyDeadLine: String?,
    var cancelPayableAmount: Double?,
    var cancelRefundableAmount: Double?,
    var couponDiscountValue: Double,
    @Contextual
    var createdTimestamp: Date? = Date(),
    @Contextual
    var modifiedTimestamp: Date? = Date(),
)

@Serializable
data class ModifyBookingDetails(
    val isPackageCode: Boolean?,
    val roomId: String,
    val roomType: String,
    val roomName: String,
    val cost: Double,
    val checkIn: String,
    val checkOut: String,
    val tax: Tax?,
    val daily:List<DailyRates>?,
    val status: String?,
    var cancellationId: String?,
    var cancellationTime: String?,
    var penaltyApplicable: Boolean?,
    var cancelRemark: String?,
    val bookingPolicyDescription: String?,
    val cancelPolicyDescription: String?,
    val description: String?,
    val detailedDescription: String?,
    var children: Int?,
    var adult: Int?,
    val rateCode: String,
    val currency: String,
    val rateDescription: String,
    val roomDescription: String,
    val packageCode:String?,
    val packageName: String,
    val roomNumber: Int,
    val changePrice:Double?,
    val roomImgUrl: String,
    val confirmationId: String,
    var roomCode: String?,
    var noOfNights:Int?,
    var roomDepositAmount: Double?,
    val grandTotal: Double,
    var paidAmount: Double?,
    var penaltyAmount: Double?,
    var penaltyDeadLine: String?,
    @Contextual
    var createdTimestamp: Date? = Date(),
    @Contextual
    var modifiedTimestamp: Date? = Date(),
)
@Serializable
data class PriceSummary(
    var totalPrice : Double?,
    val giftCardPrice : Double,
    val newCoins : Double,
    val voucher : Double,
    var totalPayableAmount : Double
)
@Serializable
data class PaymentDetailsInfo(
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
data class Tax(
    var amount: Double? = null,
    val breakDown: List<BreakDown>? = listOf()
)

@Serializable
data class BreakDown(
    val amount: Double? = null,
    val code: String? = null
)
@Serializable
data class DailyRates(
    var date:String? = null,
    var amount:Double? = null,
    var tax:TaxBreakDown? = null
)
@Serializable
data class TaxBreakDown(
    val amount: Double? = null,
    val breakDown: List<BreakDownDetails>? = listOf()
)

@Serializable
data class BreakDownDetails(
    var amount: Double? = null,
    val code: String? = null,
    val name: String?=null
)