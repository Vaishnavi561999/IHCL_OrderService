package com.ihcl.order.model.dto.request

import com.ihcl.order.model.dto.response.RedemptionBreakupByEarningProgram
import com.ihcl.order.utils.ValidatorUtils.notEmpty
import io.konform.validation.Validation
import kotlinx.serialization.Serializable

@Serializable
data class TravelerDto(
    val salutation:String?=null,
    val gender: String,
    val title: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val countryCode: String,
    val phoneNo: String,
    val membershipNo: String,
    val GSTNumber: String,
    val memberShipType:String,
    val specialRequest: String,
    val paymentMethod: String?,
    val agreedTnc: Boolean?,
    val agreedPrivacyPolicy: Boolean?,
    val voucherPin:String?,
    val voucherNumber:String?,
    val membershipProgramId: String?,
    val membershipId:String?,
    val isCampaignOffer:Boolean,
    val offerIdentifier:String?
)
val validateTravelerDtoRequest= Validation{
    TravelerDto::email required{notEmpty()}
    TravelerDto::firstName required{notEmpty()}
    TravelerDto::lastName required{notEmpty()}
    TravelerDto::countryCode required{notEmpty()}
    TravelerDto::phoneNo required{notEmpty()}
    TravelerDto::paymentMethod required{notEmpty()}
    TravelerDto::agreedTnc required{}
    TravelerDto::agreedPrivacyPolicy required{}
}
data class CPGRestrictionDto(
    val _id:String?,
    val giftCardValues:List<CPGValueDetails>?
)
data class CPGValueDetails(
    val cardType:String?,
    val merchant:String?,
    val online:String?,
    val booking:String?
)