package com.ihcl.order.model.dto.request

import com.ihcl.order.model.schema.AddOnCardDetails
import com.ihcl.order.utils.ValidatorUtils.notEmpty
import io.konform.validation.Validation
import kotlinx.serialization.Serializable

@Serializable
data class LoyaltyCreateOrderRequest(
    var gstNumber: String?,
    val address: String?,
    val country: String?,
    val dateOfBirth: String?,
    val extraData: ExtraDataRequest?,
    val mobile: String?,
    val gender: String?,
    val pinCode: String?,
    val salutation: String?,
    val user: User?,
    val addOnCardDetails: AddOnCardDetails?,
    val agreedTnc: Boolean = false,
    val agreedPrivacyPolicy: Boolean = false
)

@Serializable
data class ExtraDataRequest(
    val city: String?,
    val countryCode: String?,
    val state: String?,
    val gravityVoucherCode :String?,
    val gravityVoucherPin :String?,
)
@Serializable
data class User(
    val email: String?,
    val firstName: String?,
    val lastName: String?
)
val validateCreateOrder=Validation{
    LoyaltyCreateOrderRequest::mobile required {notEmpty()}
    LoyaltyCreateOrderRequest::salutation required {notEmpty()}
    LoyaltyCreateOrderRequest::country required {notEmpty()}
    LoyaltyCreateOrderRequest::dateOfBirth required {notEmpty()}
    LoyaltyCreateOrderRequest::address required {notEmpty()}
    LoyaltyCreateOrderRequest::pinCode required {notEmpty()}
    LoyaltyCreateOrderRequest::extraData required {
        ExtraDataRequest::city required {notEmpty()}
        ExtraDataRequest::state required {notEmpty()}
        ExtraDataRequest::countryCode required {notEmpty()}
    }
    LoyaltyCreateOrderRequest::user required {
        User::firstName required {notEmpty()}
        User::lastName required {notEmpty()}
        User::email required {notEmpty()}
    }
}

val gravityVoucherValidation = Validation{
    ExtraDataRequest::gravityVoucherCode required {notEmpty()}
    ExtraDataRequest::gravityVoucherPin required {notEmpty()}
}
