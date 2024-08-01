package com.ihcl.order.model.dto.response

data class GetBookingDetailsResponse(
    val data: BookingDetailsInfo?
)
data class BookingDetailsInfo(
    val getHotelBookingDetails: GetBookingDetails?
)
data class GetBookingDetails(
    val reservations: ArrayList<Reservation?>?,
    val errorCode: String?,
    val message: String?
)
data class Reservation(
    val modificationPermitted: Boolean?,
    val cancellationPermitted: Boolean?,
    val crsConfirmationNumber: String?,
    val itineraryNumber: String?,
    val externalReferenceNumber: String?,
    val id: String?,
    val channelConfirmationNumber: String?,
    val status: String?,
    val onHoldReleaseTime: Int?,
    val onPropertyStatus: String?,
    val purposeOfStay: String?,
    val singleUsePaymentCardAllowed: Boolean?,
    val sortOrder: Int?,
    val roomStay: RoomStay?,
    val hotel: Hotel?,
    val brand: Brand?,
    val currency: Currency?,
    val content: Content?,
    val notification: Notification?,
    val onPropertyInstructions: OnPropertyInstructions?,
    val guests: List<Guest?>?,
    val roomPrices: RoomPrices?,
    val overrides: List<String>?,
    val discounts: List<Discounts?>?,
    val cancelPolicy:CancelPolicy?,
    val bookingPolicy:BookingPolicy?,
    val channels:ChannelsInfo?,
    val bookingDues:BookingDues?,
)

data class ChannelsInfo(
    val primaryChannel: PrimaryChannel?
)

data class PrimaryChannel(
    val code: String?
)
data class Discounts(
    val adjustmentAmount:Double?,
    val adjustmentPercentage:Int?,
    val type:String?
)
data class BookingPolicy(
    val description:String?,
    val transactionFeeDisclaimer:String?,
    val guaranteeLevel:String?,
    val holdTime:String?,
    val allowPay:Boolean?,
    val code:String?,
    val requirements:List<String?>?,
    val depositFee:DepositFee?
)
data class DepositFee(
    val amount:Double?,
    val dueDays:Int?,
    val dueType:String?,
    val taxInclusive:Boolean?,
    val isPrePayment:Boolean?,
    val type:String?
)
data class CancelPolicy(
    val cancelFeeAmount:CancelFeeAmount?,
    val cancelFeeType:String?,
    val cancelPenaltyDate:String?,
    val cancelTime:String?,
    val cancelTimeIn:Int?,
    val chargeType:String?,
    val chargeThreshold:String?,
    val description:String?,
    val modificationRestrictions:String?,
    val noShowFeeType:String?,
    val code:String?,
    val lateCancellationPermitted:Boolean?,
    val charges:String?,
    val noShowFeeAmount:NoShowFeeAmount?
)
data class CancelFeeAmount(
    val taxInclusive:Boolean?,
    val value:Int?
)
data class NoShowFeeAmount(
    val taxInclusive:String?,
    val value:Int?
)
data class BookingDues(
    val noShowCharge:NoShowCharge?,
    val cancelPenalty:CancelPenalty?,
    val deposit:Deposit
)
data class Deposit(
    val dueDate:String?,
    val amount:Double?,
    val amountWithoutTax:Double?,
    val status:String?
)
data class NoShowCharge(
    val amount: Double?
)
data class CancelPenalty(
    val amount: Double?,
    val deadline:String?,
    val chargeList:List<Any?>?
)
data class Content(
    val rateCategories: List<RateCategory?>?,
    val rates: List<Rate?>?,
    val roomCategories: Any?,
    val rooms: List<RoomsDetailsInfo?>?
)
data class RateCategory(
    val categoryCode: String?,
    val description: String?,
    val name: String?
)
data class Rate(
    val categoryCode: String?,
    val code: String?,
    val description: String?,
    val detailedDescription: String?,
    val displayName: String?,
    val effectiveDate: String?,
    val expireDate: String?,
    val name: String?,
    val primary: Boolean?
)
data class RoomsDetailsInfo(
    val categoryCode: String?,
    val code: String?,
    val description: String?,
    val detailedDescription: String?,
    val name: String?
)
data class CurrencyInfo(
    val code: String?,
    val name: String?,
    val symbol: String?
)
data class Guest(
    val contactNumbers:List<ContactNumberDto>,
    val emailAddress: List<EmailAddress?>?,
    val endDate: String?,
    val marketingOptIn: Boolean?,
    val payments: List<Any?>?,
    val personName: PersonName?,
    val role: String?,
    val startDate: String?
)
data class ContactNumberDto(val number: String)
data class Hotel(
    val code: String?,
    val id: Int?,
    val name: String?
)
data class Notification(
    val bookingComment: String?,
    val deliveryComments: List<DeliveryComment?>?,
    val sendBookerEmail: Boolean?,
    val sendGuestEmail: Boolean?
)
data class AveragePrice(
    val price: Price?
)
data class BreakDownInfo(
    val amount: Double?,
    val code: String?,
    val name: String?,
    val isInclusive: Boolean?,
    val isPayAtProperty: Boolean?,
    val isPerStay: Boolean?
)
data class DeliveryComment(
    val comment: String?
)
data class EmailAddress(
    val default: Boolean?,
    val type: String?,
    val value: String?
)
data class Fees(
    val amount: Double?,
    val breakDown: List<BreakDownInfo?>?
)
data class GuestCount(
    val ageQualifyingCode: String?,
    val numGuests: Int?
)
data class OnPropertyInstructions(
    val chargeRoutingList: List<Any?>?
)
data class PersonName(
    val firstName: String?,
    val lastName: String?,
    val prefix: String?
)
data class Price(
    val tax: TaxInfo?,
    val fees: Fees?,
    val total: Total?,
    val currencyCode:String?,
    val displayOverrideAsPercentage:Boolean?,
    val originalAmount:Double?,
    val totalAmount:Double?,
    val amountPayableNow:Double?,
    val amountPayAtProperty:Double?,
    val totalAmountWithInclusiveTaxesFees:Double?,
    val totalAmountIncludingTaxesFees:Double?,
    val originalAmountIncludingTaxesAndFees:Double?,
)

data class ProductsInfo(
    val endDate: String?,
    val primary: Boolean?,
    val product: ProductDetails?,
    val startDate: String?
)
data class ProductDetails(
    val id: String?,
    val rateCode: String?,
    val roomCode: String?
)
data class RoomPrices(
    val averagePrice: AveragePrice?,
    val totalPrice: TotalPrice?,
    val priceBreakdowns:List<PriceBreakdowns?>?
)
data class PriceBreakdowns(
    val type:String?,
    val productPrices:List<ProductPrices?>?
)
data class ProductPrices(
    val startDate:String?,
    val endDate:String?,
    val product:ProductDetails?,
    val price:Price?
)
data class RoomStay(
    val endDate: String?,
    val guestCount: List<GuestCount?>?,
    val numRooms: Int?,
    val products: List<ProductsInfo?>?,
    val startDate: String?
)
data class TaxInfo(
    val amount: Double?,
    val breakDown: List<BreakDownInfo?>?
)

data class TotalPrice(
    val price: Price?
)
data class TotalPriceX(
    val price: PriceX?
)
data class PriceX(
    val totalAmount: Double?,
    val currencyCode: String?,
    val fees: Fees?,
    val tax: TaxInfo?,
    val total: Total?,
    val amountPayableNow:Double?
)
data class Total(
    val amount:Double?,
    val amountPayAtProperty:Double?,
    val amountPayableNow:Double?,
    val amountWithInclusiveTaxes:Double?,
    val amountWithTaxesFees:Double?
)
data class Brand(
    val code: String?,
    val name: String?
)
