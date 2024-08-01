package com.ihcl.order.model.schema

data class GiftCardRestrictionValues(
    val _id: String,
    val  giftCardValues:List<GiftCardRestrictions>
)

data class GiftCardRestrictions(
    val cardType: String,
    val merchant: String,
    val online: String,
    val booking: String
)
