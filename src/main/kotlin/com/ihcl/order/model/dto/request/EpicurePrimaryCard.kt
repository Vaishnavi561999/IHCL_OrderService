package com.ihcl.order.model.dto.request

import com.google.gson.annotations.SerializedName

data class EpicurePrimaryCard(
    @SerializedName("member_id"          ) var memberId         : String? = null,
    @SerializedName("membership_plan_id" ) var membershipPlanId : String? = null,
    @SerializedName("card_start_date"    ) var cardStartDate    : String? = null,
    @SerializedName("card_expiry_date"   ) var cardExpiryDate   : String? = null,
    @SerializedName("card_status"        ) var cardStatus       : String? = null,
    @SerializedName("card_type"          ) var cardType         : String? = null,
    @SerializedName("name_on_card"       ) var nameOnCard       : String? = null,
    @SerializedName("membership_plan_code"       ) var membershipPlanCode       : String? = null

)
