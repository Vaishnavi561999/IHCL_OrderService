package com.ihcl.order.model.dto.request

import com.google.gson.annotations.SerializedName

data class CancelRoomsDTO(
    @SerializedName("confirmationId"    ) var confirmationId    : String? = null,
    @SerializedName("roomStatus"        ) var roomStatus        : String? = null,
    @SerializedName("penaltyApplicable" ) var penaltyApplicable : Boolean? = null,
    @SerializedName("penaltyAmount"     ) var penaltyAmount     : Double? = null
)
