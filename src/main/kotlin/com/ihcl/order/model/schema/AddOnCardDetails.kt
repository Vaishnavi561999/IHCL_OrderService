package com.ihcl.order.model.schema

import com.google.gson.annotations.SerializedName
import com.ihcl.order.utils.ValidatorUtils.notEmpty
import io.konform.validation.Validation
import kotlinx.serialization.Serializable

@Serializable
data class AddOnCardDetails(
    val email: String?,
    val firstName: String?,
    val lastName: String?,
    val title: String?,
    val dateOfBirth:String?,
    val mobile:String?,
    val mobileCountryCode:String?,
    val obtainAddOnCard:Boolean
)
val validateAddOnCardReq= Validation{
    AddOnCardDetails::title required {notEmpty()}
    AddOnCardDetails::firstName required {notEmpty()}
    AddOnCardDetails::lastName required {notEmpty()}
    AddOnCardDetails::email required {notEmpty()}
    AddOnCardDetails::dateOfBirth required {notEmpty()}
}
data class AddOnCardRequest(
    @SerializedName("member_id")
    val memberId:String?,
    @SerializedName("membership_plan_id")
    val memberShipPlanId:String?,
    @SerializedName("card_start_date")
    val cardStartDate:String?,
    @SerializedName("card_expiry_date")
    val cardExpiryDate:String?,
    @SerializedName("card_status")
    val cardStatus:String?,
    @SerializedName("name_on_card")
    val nameOnCard:String?,
    @SerializedName("relationship_type")
    val relationShipType:String?,
    @SerializedName("spouse_name")
    val spouseName:String?,
    @SerializedName("spouse_gender")
    val spouseGender:String?,
   @SerializedName("spouse_date_of_birth")
    val spouseDateOfBirth:String?,
    @SerializedName("spouse_email_address")
    val spouseEmailAddress:String?,
    @SerializedName("membership_plan_code")
    val memberShipPlanCode:String?,
    @SerializedName("spouse_phone_number")
    val spousePhoneNumber:String?,
    @SerializedName("receipt_number")
    val receiptNumber:String?,
    @SerializedName("card_type")
    val cardType:String?,
    @SerializedName("dispatched_date")
    val dispatchedDate: String?,
    @SerializedName("fulfilment_status")
    val fulfilmentStatus:String?
)


