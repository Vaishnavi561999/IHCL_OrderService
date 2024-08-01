package com.ihcl.order.model.dto.request

import com.ihcl.order.utils.ValidatorUtils.notEmpty
import io.konform.validation.Validation
import kotlinx.serialization.Serializable

@Serializable
data class CancelBookingReq(
    val hotelId : String,
    val crsConfirmationNumber: String,
    val cancellationReason : String
)

@Serializable
data class CancelBookingRequest(
    val hotelId : String,
    var isFullCancellation: Boolean,
    val room : List<ConfirmationDetails>,
    val cancelReason : String,
    val orderId : String?,
    val cancelType: String
)
@Serializable
data class ConfirmationDetails(
    val roomNumber : Int
)



val validateCancelBookingRequest = Validation{
    CancelBookingRequest::room required {  }
    CancelBookingRequest::hotelId required { notEmpty() }
    CancelBookingRequest::isFullCancellation required {  }
    CancelBookingRequest::orderId required { notEmpty() }
    CancelBookingRequest::cancelType required { notEmpty() }
    CancelBookingRequest::room.onEach {
        ConfirmationDetails::roomNumber required {}
    }
}
