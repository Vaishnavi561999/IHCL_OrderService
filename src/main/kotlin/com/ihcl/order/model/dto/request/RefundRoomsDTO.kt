package com.ihcl.order.model.dto.request

import com.google.gson.annotations.SerializedName

data class RefundRoomsDTO(
    @SerializedName("confirmationId" ) var confirmationId : String? = null
)
