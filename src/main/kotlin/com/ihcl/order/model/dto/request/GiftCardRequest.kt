package com.ihcl.order.model.dto.request

import com.ihcl.order.utils.ValidatorUtils.notEmpty
import io.konform.validation.Validation
import kotlinx.serialization.Serializable

@Serializable
data class GiftCardRequest(
    val orderId:String? = null,
    val deliveryMethods: DeliveryMethods,
    val giftCardDetails: GiftCardDetails,
    val promoCode: String,
    val receiverAddress: ReceiverAddress,
    val receiverDetails: ReceiverDetails,
    val senderDetails: SenderDetails,
    val isMySelf: Boolean
)
@Serializable
data class GiftCardDetails(
    val amount: Double,
    val quantity: Int,
    val sku: String,
    val type: String,
    val theme: String? = null
)
@Serializable
data class ReceiverAddress(
    val addressLine1: String?,
    val addressLine2: String?,
    val city: String?,
    val country: String?,
    val pinCode: String?,
    val state: String?
)
@Serializable
data class ReceiverDetails(
    val email: String,
    val firstName: String,
    val lastName: String,
    val message: String,
    val phone: String,
    val rememberMe: Boolean,
    val scheduleOn: String,
    val giftMessage: String?
)
@Serializable
data class SenderDetails(
    val email: String,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val registerAsNeuPass: Boolean
)
@Serializable
data class DeliveryMethods(
    val phone: String,
    val email: Boolean,
    val smsAndWhatsApp: Boolean
)
val validateGiftCardRequest=Validation{
    GiftCardRequest::deliveryMethods required {
        DeliveryMethods::phone required {notEmpty()}
        DeliveryMethods::email required{}
        DeliveryMethods::smsAndWhatsApp required {String}
    }
    GiftCardRequest::giftCardDetails required {
        GiftCardDetails::amount required {}
        GiftCardDetails::quantity required {}
        GiftCardDetails::sku required {notEmpty()}
        GiftCardDetails::type required {notEmpty()}
    }
    GiftCardRequest::receiverDetails required {
        ReceiverDetails::email required {notEmpty()}
        ReceiverDetails::firstName required {notEmpty()}
        ReceiverDetails::lastName required {notEmpty()}
        ReceiverDetails::phone required {notEmpty()}
        ReceiverDetails::message required {notEmpty()}
    }
    GiftCardRequest::receiverAddress required {
         ReceiverAddress::country required {notEmpty()}
    }
     GiftCardRequest::isMySelf required {}
}



