package com.ihcl.order.repository

import com.ihcl.order.config.MongoConfig
import com.ihcl.order.model.schema.ApportionOrder
import com.ihcl.order.model.schema.RefundOrders
import com.mongodb.BasicDBObject
import com.mongodb.client.model.FindOneAndUpdateOptions
import org.bson.Document
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq
import org.litote.kmongo.json
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ApportionOrderRepository {
    private val orderDataCollection: CoroutineCollection<ApportionOrder> = MongoConfig.getDatabase().getCollection()
    private val refundOrderDataCollection: CoroutineCollection<RefundOrders> = MongoConfig.getDatabase().getCollection()
    val log: Logger = LoggerFactory.getLogger(javaClass)

    suspend fun findUpdatedOrderByOrderId(orderId: String?): ApportionOrder {
        log.info("enter into apportioned repository $orderId")
        var order = ApportionOrder()
        val orderList = orderDataCollection.find(ApportionOrder::orderId eq orderId).sort(Document("_id", -1)).limit(1)
        log.info("order list in apportioned orders ${orderList}")
        if (orderList.toList().isNotEmpty()) {
            order = orderList.first()!!
        }
        log.info("order in apportioned order $order")
        return order
    }

    suspend fun saveOrderData(orderData: ApportionOrder) {
        orderDataCollection.save(orderData)
    }

    suspend fun findRefundRecordsByOrderId(orderId: String): ArrayList<ApportionOrder> {
        log.info("enter into refund repository $orderId")
        val compareOrdersList = arrayListOf<ApportionOrder>()
        val orderList = orderDataCollection.find(ApportionOrder::orderId eq orderId).sort(Document("_id", -1)).limit(1)
        if (orderList.toList().isNotEmpty() && orderList.toList().size >= 2) {
                orderList.first()?.let { compareOrdersList.add(it) }
                orderList.toList()[1].let { compareOrdersList.add(it) }
        }
        return compareOrdersList
    }

    suspend fun saveRefundOrderData(refundOrder: RefundOrders) {
        refundOrderDataCollection.save(refundOrder);
    }

    suspend fun findOneAndUpdateOrder(orderId: String,order: ApportionOrder) {
        log.info("order details $orderId and order ${order.json}")
        val updateObject = BasicDBObject()
        updateObject["\$set"] = order
        orderDataCollection.findOneAndUpdate(order::orderId eq orderId, updateObject, FindOneAndUpdateOptions().upsert(true))
        log.info("order updated $orderId and order ${order.json}")

    }
    suspend fun deleteOrder(orderId: String?) {
        var order = ApportionOrder()
        val orderList = orderDataCollection.find(ApportionOrder::orderId eq orderId).sort(Document("_id", -1)).limit(1)
        if (orderList.toList().isNotEmpty()) {
            order = orderList.first()!!
            log.info(order.createdOn)
        }

        orderDataCollection.deleteOne(order::_id eq order._id);
    }
}