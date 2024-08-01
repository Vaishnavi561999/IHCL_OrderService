package com.ihcl.order.model.dto.response

import com.ihcl.order.model.dto.request.VoucherRedemptionAvailPrivileges
import kotlinx.serialization.Serializable

@Serializable
data class ConfirmHotelResponse(
    val orderId: String?,
    val booking_status: String?,
    val isSeb:Boolean?,
    val hotelId: String?,
    val hotelName: String?,
    val hotelAddress: HotelAddress?,
    val country: String?,
    val check_in: String?,
    val check_out: String?,
    val guest_name: String?,
    val firstName: String?,
    val lastName: String?,
    val salutation: String?,
    val itinerary_number: String?,
    val number_of_guests_ADULTS: String?,
    val number_of_guests_CHILDREN: String?,
    val number_of_rooms: String?,
    val paymentBreakup: PaymentBreakup?,
    val refundBreakUp:RefundPaymentBreakup?,
    val paymentStatus: String?,
    val transactionStatus: String?,
    val transactionType: String?,
    val cardNumber: String?,
    val nameOnCard: String?,
    val cardBrand: String?,
    val modifiedCount:Int?,
    val promoCode:String?,
    var promoType: String?,
    var totalDepositAmount : Double?,
    var isDepositPaid: Boolean?,
    var isDepositFull: Boolean?,
    val rooms: List<Room?>?,
    val emailId: String?,
    val mobileNumber: String?,
    val specialRequest: String?,
    val voucherRedemption: VoucherRedemptionAvailPrivileges?,
    val paidAmount:Double?,
    val cancellationAmount:Double?,
    val cancelPayableAmount:Double?,
    val cancelRefundableAmount:Double?,
    var revisedPrice: Double?,
    var grandTotal: Double?,
    var totalBasePrice: Double?,
    var totalTaxPrice: Double?,
    val amountPaid: Double?,
    val balancePayable: Double?,
    var payableAmount: Double?,
    var refundAmount: Double?,
    var oldTotalBasePrice: Double?,
    var oldTotalTaxPrice: Double?,
    var oldGrandTotal: Double?,
    val totalPriceChange: Double?,
    val totalTaxChange: Double?,
    val paymentMethod: String?,
    val paymentTransactionId: String?,
    val bookingCancelRemarks:String?,
    val bookingNoOfNights: Int?,
    var totalCouponDiscountValue : Double?,
    var createdTimestamp : String?,
    var modifiedTimestamp : String?
    )

@Serializable
data class HotelAddress(
    val landMark: String?,
    val pinCode: String?,
    val state: String?
)

@Serializable
data class PaymentBreakup(
    val add_ons: String?,
    val giftCard_redemption: String?,
    val member_discount: String?,
    val neuCoins_redemption: String?,
    val voucher_discount: String?,
    val price: String?,
    val taxes_and_fees: TaxesAndFees?,
    val total: String?,
    val payableAmount: String
   )

@Serializable
data class TaxesAndFees(
    val taxAmount: String?,
    val gstAmount: String?
)
@Serializable
data class RefundPaymentBreakup(
    val add_ons: String?,
    val giftCard_redemption: String?,
    val member_discount: String?,
    val neuCoins_redemption: String?,
    val voucher_discount: String?,
    val refundAmount: String?,
    val refundableAmount: String?
)

@Serializable
data class Room(
    val isPackageCode: Boolean?,
    val addOns: ADDONS?,
    val adults: Int?,
    val children: Int?,
    val noOfNights: Int?,
    val booking_number: String?,
    val cancellation_number: String?,
    val booking_status: String?,
    val check_in: String?,
    val check_out: String?,
    val tax: Tax,
    val bookingPolicyDescription: String?,
    val daily:List<DailyRates>?,
    val cancelPolicyDescription: String?,
    var description: String?,
    var detailedDescription: String?,
    var penaltyAmount: Double?,
    var penaltyDeadLine: String?,
    var cancellationTime: String?,
    var penaltyApplicable: Boolean?,
    val guest_information: GuestInformation?,
    val packageData: Package?,
    val packageName: String,
    val room_name: String?,
    val roomImgUrl: String?,
    val roomNumber: Int,
    val changePrice: Double?,
    val changeTax: Double?,
    val cancelRemark: String?,
    val modifyBooking: ModifyBookingInfo?,
    val grandTotal: Double,
    var paidAmount: Double?,
    var roomCode: String?,
    var roomDepositAmount: Double?,
    val cancelPayableAmount: Double?,
    val cancelRefundableAmount: Double?,
    val currency: String?,
    var couponDiscountValue: Double?,
    val createdTimestamp: String?,
    val modifiedTimestamp: String?
)
@Serializable
data class ModifyBookingInfo(
    val isPackageCode: Boolean?,
    val addOns: ADDONS?,
    val adults: Int?,
    val children: Int?,
    val noOfNights: Int?,
    val booking_number: String?,
    val cancellation_number: String?,
    val booking_status: String?,
    val check_in: String?,
    val check_out: String?,
    val tax: Tax?,
    val daily:List<DailyRates>?,
    val bookingPolicyDescription: String?,
    val cancelPolicyDescription: String?,
    var description: String?,
    var detailedDescription: String?,
    val guest_information: GuestInformation?,
    val packageData: Package?,
    val packageName: String,
    val room_name: String?,
    val roomImgUrl: String?,
    val roomNumber: Int,
    val cancelRemark: String?,
    val grandTotal: Double,
    var paidAmount: Double?,
    var roomCode: String?,
    var roomDepositAmount: Double?,
    val currency: String?,
    val createdTimestamp: String?,
    val modifiedTimestamp: String?
)

@Serializable
data class ADDONS(
    val ADDONS_amount: String?,
    val ADDONS_code: String?,
    val ADDONS_name: String?
)

@Serializable
data class GuestInformation(
    val email: String?,
    val name: String?,
    val phn_number: String?,
    val salutation: String?
)

@Serializable
data class Package(
    val package_amount: String?,
    val package_code: String?,
    val package_name: String?
)