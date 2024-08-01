package com.ihcl.order.model.dto.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.ihcl.order.model.dto.request.GiftCardRequest
import com.ihcl.order.model.dto.request.VoucherRedemptionAvailPrivileges
import com.ihcl.order.model.schema.*
import com.microsoft.applicationinsights.core.dependencies.google.j2objc.annotations.AutoreleasePool
import kotlinx.serialization.Serializable
import org.intellij.lang.annotations.Identifier

@JsonIgnoreProperties(ignoreUnknown = true)
@Serializable
data class OrderResponse(
    val orderId: String?,
    val customerHash: String?,
    val orderType: OrderType = OrderType.HOTEL_BOOKING,
    val customerEmail: String,
    val customerId: String,
    val customerMobile: String,
    val channel: String,
    val currencyCode: String,
    val discountAmount: Double,
    val basePrice: Double?,
    val taxAmount: Double?,
    val gradTotal: Double?,
    val payableAmount: Double?,
    val refundAmount: Double?,
    val isRefundable: Boolean,
    val transactionId: String,
    val billingAddress: BillingAddressDetails,
    val offers: List<OffersDetails>?,
    val orderLineItems: List<OrderLineItemDetails>?,
    val paymentDetails: MutableList<PaymentDetailInfo>?,
    val paymentMethod: String?,
    val paymentStatus: String?,
    val orderStatus: String?,
    val totalPriceChange: Double?,
    val totalTaxChange: Double?,
    val errorCode:Int?,
    val errorMessage:MutableList<Int>?
)
@JsonIgnoreProperties(ignoreUnknown = true)
@Serializable
data class RoomDetailsInfo(
    val isPackageCode:Boolean?,
    val confirmationId: String?,
    val addOnDetails: List<AddOnDetailInfo>?,
    val checkIn: String?,
    val checkOut: String?,
    val discountAmount: Double,
    val discountCode: String,
    val isModified: Boolean,
    val isRefundedItem: Boolean,
    val modifiedWith: String,
    val price: Double?,
    val tax: Tax?,
    val bookingPolicyDescription: String?,
    val daily:List<DailyRates>?,
    val cancelPolicyDescription: String?,
    var description: String?,
    var detailedDescription: String?,
    val rateDescription: String,
    val refundedAmount: String,
    val roomDescription: String,
    val roomId: String,
    val roomName: String?,
    val roomType: String?,
    val rateCode: String?,
    val roomNumber: Int,
    val status: String?,
    val packageName: String?,
    val currency: String?,
    val roomImgUrl: String?,
    val travellerDetails: List<TravellerDetailsInfo>?,
    val changePrice:Double?,
    val changeTax:Double?,
    val modifiedRoom: ModifiedRoom?,
    val grandTotal: Double?,
    var paidAmount: Double?,
    var roomCode: String?,
    var roomDepositAmount: Double?,
    var couponDiscountValue: Double?
)
@JsonIgnoreProperties(ignoreUnknown = true)
@Serializable
data class ModifiedRoom(
    val isPackageCode:Boolean?,
    val confirmationId: String,
    val addOnDetails: List<AddOnDetailInfo>,
    val checkIn: String,
    val checkOut: String,
    val discountAmount: Double,
    val discountCode: String,
    val isModified: Boolean,
    val isRefundedItem: Boolean,
    val modifiedWith: String,
    val price: Double,
    val tax: Tax?,
    val bookingPolicyDescription: String?,
    val cancelPolicyDescription: String?,
    var description: String?,
    var detailedDescription: String?,
    val rateDescription: String,
    val refundedAmount: String,
    val roomDescription: String,
    val roomId: String,
    val roomName: String,
    val roomType: String,
    val rateCode: String?,
    val roomNumber: Int,
    val status: String,
    val packageName: String,
    val currency: String?,
    val travellerDetails: List<TravellerDetailsInfo>?,
    val grandTotal: Double,
    var paidAmount: Double?,
    var roomCode: String?,
    var roomDepositAmount: Double?
)

@JsonIgnoreProperties(ignoreUnknown = true)
@Serializable
data class TravellerDetailsInfo(
    val dateOfBirth: String,
    val address: String,
    val city: String,
    val countryCode: String,
    val customerId: String,
    val customerType: String,
    val email: String,
    val firstName: String,
    val gender: String,
    val gstNumber: String,
    val lastName: String,
    val membershipNumber: String,
    val mobile: String,
    val name: String,
    val secondaryContact: String,
    val state: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
@Serializable
data class BillingAddressDetails(
    val address1: String,
    val address2: String,
    val address3: String,
    val city: String,
    val country: String,
    val firstName: String,
    val lastName: String,
    val pinCode: String,
    val state: String,
    val phoneNumber: String,
    val countyCodeISO: String
)
@JsonIgnoreProperties(ignoreUnknown = true)
@Serializable
data class OrderLineItemDetails(
    val hotel: HotelDetailsInfo
)

@JsonIgnoreProperties(ignoreUnknown = true)
@Serializable
data class HotelDetailsInfo(
    val addOnDetails: List<AddOnDetailInfo>?,
    val address: AddressDetails?,
    val bookingNumber: String?,
    val category: String?,
    val hotelId: String?,
    val invoiceNumber: String?,
    val invoiceUrl: String?,
    val name: String?,
    val reservationId: String?,
    var promoType: String?,
    val promoCode:String?,
    val rooms: List<RoomDetailsInfo>?,
    val voucherRedemption: VoucherRedemptionAvailPrivileges?,
    val adults:Int?,
    val childrens:Int?,
    val totalRooms:Int?,
    val status: String?,
    var country: String?,
    var totalDepositAmount: Double?,
    var balancePayable: Double?,
    val specialRequest: String?,
    var grandTotal: Double?,
    var totalBasePrice: Double?,
    var totalTaxPrice: Double?,
    val amountPaid: Double?,
    var payableAmount: Double?,
    var refundAmount: Double?,
    var oldTotalBasePrice: Double?,
    var oldTotalTaxPrice: Double?,
    var oldGrandTotal: Double?,
    var bookingNoOfNights: Int?,
    val checkIn: String?,
    val checkOut: String?,
    val storeId: String?,
    val hotelSponsorId:String?,
    var totalCouponDiscountValue: Double?
    )

@JsonIgnoreProperties(ignoreUnknown = true)
@Serializable
data class AddressDetails(
    val city: String,
    val contactNumber: String,
    val directions: String,
    val landmark: String,
    val lat: String,
    val long: String,
    val mapLink: String,
    val pinCode: String,
    val state: String,
    val street: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
@Serializable
data class AddOnDetailInfo(
    val addOnCode: String,
    val addOnDesc: String,
    val addOnName: String,
    val addOnPrice: Double,
    val addOnType: String
)
@JsonIgnoreProperties(ignoreUnknown = true)
@Serializable
data class PaymentDetailInfo(
    val paymentType: String?,
    val paymentMethod: String?,
    val paymentMethodType: String?,
    val txnGateway: Int?,
    val txnId: String?,
    val txnNetAmount: Double?,
    val txnStatus: String?,
    val txnUUID: String?,
    var cardNo: String?,
    var nameOnCard: String?,
    var cardNumber: String?,
    var preAuthCode: String?,
    var batchNumber: String?,
    var approvalCode: String?,
    var transactionId: Int?,
    var transactionDateAndTime: String?,
    var expiryDate:String?
)
@JsonIgnoreProperties(ignoreUnknown = true)
@Serializable
data class OffersDetails(
    val offerAmount: Double,
    val offerName: String,
    val offerType: String
)


