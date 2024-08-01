package com.ihcl.order.model.dto.request

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class RoomsDTO(
    @SerializedName("confirmationId" ) var confirmationId : String? = null,
    @SerializedName("price"          ) var price          : Double?    = null,
    @SerializedName("taxAmount"      ) var taxAmount      : Double? = null,
    @SerializedName("noOfNights"     ) var noOfNights     : Int? = null,
    @SerializedName("totalAmount"    ) var totalAmount    : Double? = null,
    @SerializedName("checkIn"        ) var checkIn        : String? = null,
    @SerializedName("checkOut"       ) var checkOut       : String? = null,
    @SerializedName("roomStatus"     ) var roomStatus     : String? = null
)
