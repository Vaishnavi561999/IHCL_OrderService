package com.ihcl.order.model.schema

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.ihcl.order.model.dto.response.DailyRates
import com.ihcl.order.model.dto.response.Tax
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
@Serializable
data class Room(
    val isPackage: Boolean?,
    var confirmationId: String?,
    var cancellationId: String?,
    var status: String?,
    val addOnDetails: List<AddOnDetail>?,
    val checkIn: String?,
    val checkOut: String?,
    val taxAmount: Double?,
    val tax: Tax?,
    val bookingPolicyDescription: String?,
    val daily:List<DailyRates>?,
    val cancelPolicyDescription: String?,
    val description: String?,
    val detailedDescription: String?,
    var penaltyAmount: Double?,
    var penaltyDeadLine: String?,
    var cancellationTime: String?,
    var penaltyApplicable: Boolean?,
    var cancelRemark: String?,
    val discountAmount: Double,
    val discountCode: String,
    var isModified: Boolean,
    val isRefundedItem: Boolean,
    val modifiedWith: String,
    val price: Double?,
    val rateDescription: String,
    val refundedAmount: String,
    val roomDescription: String,
    val roomId: String,
    val roomName: String?,
    val roomNumber: Int,
    val roomType: String?,
    val rateCode: String?,
    val packageCode:String?,
    val adult:Int?,
    val children:Int?,
    val packageName: String?,
    val currency: String?,
    val travellerDetails: List<TravellerDetail>?,
    val roomImgUrl: String?,
    val changePrice:Double?,
    val changeTax:Double?,
    val modifyBooking: ModifyBooking?,
    val grandTotal: Double,
    var paidAmount: Double?,
    var roomCode: String?,
    var roomDepositAmount: Double?,
    var cancelPayableAmount: Double?,
    var cancelRefundableAmount: Double?,
    var noOfNights: Int?,
    var couponDiscountValue: Double? = 0.0,
    @Contextual
    var createdTimestamp: Date? = Date(),
    @Contextual
    var modifiedTimestamp: Date? = Date(),

    )
@JsonIgnoreProperties(ignoreUnknown = true)
@Serializable
data class ModifyBooking(
    val isPackage: Boolean?,
    var confirmationId: String?,
    var cancellationId: String?,
    var status : String?,
    val addOnDetails: List<AddOnDetail>,
    val checkIn: String,
    val checkOut: String,
    val taxAmount: Double?,
    val tax: Tax?,
    val daily:List<DailyRates>?,
    val bookingPolicyDescription: String?,
    val cancelPolicyDescription: String?,
    var penaltyAmount: Double?,
    var penaltyDeadLine: String?,
    var cancellationTime: String?,
    var penaltyApplicable: Boolean?,
    val description: String?,
    val detailedDescription: String?,
    val discountAmount: Double,
    val discountCode: String,
    var isModified: Boolean,
    val isRefundedItem: Boolean,
    val modifiedWith: String,
    val price: Double,
    val rateDescription: String,
    val refundedAmount: String,
    val roomDescription: String,
    val roomId: String,
    val roomName: String,
    val roomNumber: Int,
    val roomType: String,
    val rateCode: String?,
    val packageCode:String?,
    val adult:Int?,
    val children:Int?,
    val packageName: String?,
    val currency: String?,
    val travellerDetails: List<TravellerDetail>?,
    val roomImgUrl: String?,
    var cancelRemark: String?,
    val grandTotal: Double,
    val paidAmount: Double?,
    var noOfNights: Int?,
    var roomCode: String?,
    @Contextual
    var createdTimestamp: Date? = Date(),
    @Contextual
    var modifiedTimestamp: Date? = Date(),
)