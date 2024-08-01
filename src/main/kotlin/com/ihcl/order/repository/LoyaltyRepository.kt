package com.ihcl.order.repository

import com.ihcl.order.config.MongoConfig
import com.ihcl.order.model.schema.LoyaltyMemberDetails
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq

class LoyaltyRepository {
    private val loyaltyCollection: CoroutineCollection<LoyaltyMemberDetails> = MongoConfig.getDatabase().getCollection()

    suspend fun saveMemberDetails(loyaltyMemberDetails: LoyaltyMemberDetails) {
        val collection = loyaltyCollection.findOne(LoyaltyMemberDetails::mobile eq loyaltyMemberDetails.mobile)
        if (collection == null) {
            loyaltyCollection.save(loyaltyMemberDetails)
        }
    }
}
