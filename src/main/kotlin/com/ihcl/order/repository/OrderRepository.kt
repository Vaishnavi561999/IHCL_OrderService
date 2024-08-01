package com.ihcl.order.repository

import com.ihcl.order.config.Configuration
import com.ihcl.order.config.MongoConfig
import com.ihcl.order.model.dto.request.BankUrlValuesRequest
import com.ihcl.order.model.dto.request.FetchBookingDetailsDto
import com.ihcl.order.model.dto.request.RecordIdSequence
import com.ihcl.order.model.dto.request.*
import com.ihcl.order.model.dto.response.*
import com.ihcl.order.model.dto.response.AddOnCardResponse
import com.ihcl.order.model.dto.response.BuyingGCSuccess
import com.ihcl.order.model.dto.response.OrderStatusResponse
import com.ihcl.order.model.dto.response.ReloadBalResponse
import com.ihcl.order.model.exception.HttpResponseException
import com.ihcl.order.model.exception.QCInternalServerException
import com.ihcl.order.model.schema.*
import com.ihcl.order.model.schema.Hotel
import com.ihcl.order.model.schema.Room
import com.ihcl.order.utils.Constants
import com.ihcl.order.utils.Constants.DATA_ERROR_EXCEPTION
import com.ihcl.order.utils.Constants.COUNTRY_ID
import com.ihcl.order.utils.Constants.DATE_PATTERN
import com.ihcl.order.utils.Constants.FIRST_ORDER_ID
import com.ihcl.order.utils.Constants.INCREMENT_NUMBER
import com.ihcl.order.utils.Constants.INITIATED
import com.ihcl.order.utils.Constants.ORDER_ID_TYPE
import com.ihcl.order.utils.Constants.ORDER_NOT_FOUND
import com.ihcl.order.utils.Constants.ORDER_NOT_FOUND_ERROR_MESSAGE
import com.ihcl.order.utils.Constants.PENDING
import com.ihcl.order.utils.DataMapperUtils
import com.mongodb.BasicDBObject
import com.mongodb.client.model.*
import org.bson.Document
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineCollection
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import com.mongodb.client.model.UpdateOptions
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.LinkedHashSet
import kotlin.collections.List
import java.time.LocalDateTime
import java.time.ZoneOffset

class OrderRepository {
   private val  orderCollection: CoroutineCollection<Order> = MongoConfig.getDatabase().getCollection()
    private val giftCardCollection: CoroutineCollection<GiftCardValues> = MongoConfig.getDatabase().getCollection()
    private val giftCardRestrictionValues: CoroutineCollection<GiftCardRestrictionValues> = MongoConfig.getDatabase().getCollection()
    private val bankUrlCollection:CoroutineCollection<BankUrlValuesRequest> = MongoConfig.getDatabase().getCollection("BankUrlValues")
    private val recordIdCollection: CoroutineCollection<RecordIdSequence> = MongoConfig.getDatabase().getCollection()
    private val  countriesCollection:CoroutineCollection<CountriesDto> = MongoConfig.getDatabase().getCollection("countries")
    private val  statesCollection:CoroutineCollection<StatesDto> = MongoConfig.getDatabase().getCollection("states")
    private val  citiesCollection:CoroutineCollection<CitiesDto> = MongoConfig.getDatabase().getCollection("cities")
    private val  gifCardCollection:CoroutineCollection<CPGRestrictionDto> = MongoConfig.getDatabase().getCollection("giftCardRestrictionValues")


    private val prop = Configuration.env
    val log: Logger = LoggerFactory.getLogger(javaClass)

    private val Success_Message="Successfully updated the document"
    private val Failure_Message ="Failed to update the document"
   suspend fun saveOrder(order: Order) {
      order.modifiedTimestamp = Date()
      orderCollection.save(order)
   }
    suspend fun findOneAndUpdateOrder(orderId: String?,order: Order) {
        val updateObject = BasicDBObject()
        updateObject["\$set"] = order
        orderCollection.findOneAndUpdate(order::orderId eq orderId, updateObject, FindOneAndUpdateOptions().upsert(true))
    }
    suspend fun findMultipleOrders(orderId: String): List<Order> {
        val filter = Order::orderId lt orderId
        return orderCollection.find(filter).toList()
    }

    suspend fun updateMultipleOrders(order: Order){
        val filter = Order::orderId eq order.orderId
        orderCollection.updateOne(filter,order)
        log.info("Updated successfully")
    }

   suspend fun findOrderByOrderId(orderId: String?): Order {
       log.info("enter into repository $orderId")
       val order = orderCollection.findOne(Order::orderId eq orderId)
           ?: throw HttpResponseException(ORDER_NOT_FOUND,HttpStatusCode.NotFound)
      return order
   }
    suspend fun getOrderByOrderId(orderId: String?, customerHash: String?): Order {
        log.info("enter into repository $orderId")
        return orderCollection.findOne(Order::orderId eq orderId, Order::customerHash eq customerHash)
            ?: throw HttpResponseException(ORDER_NOT_FOUND, HttpStatusCode.NotFound)
    }
    suspend fun getOrderByOrderIdAndEmailId(orderId: String?, emailId: String): Order {
        log.info("entered into order repository with order id = $orderId and email id =$emailId")
        val query = and(Order::orderId eq orderId, Order::customerEmail eq emailId)
        return orderCollection.findOne(query)
            ?: throw HttpResponseException(ORDER_NOT_FOUND, HttpStatusCode.NotFound)
    }
    suspend fun findOrderByOrderTypeAndOrderStatus(orderType: Enum<OrderType>, orderStatus: String): List<Order> {
        val thirtyMinutesAgo = Date.from(
            LocalDateTime.now()
                .minusMinutes(prop.pendingBookingsForDuration.toLong())
                .atZone(ZoneOffset.UTC)
                .toInstant()
        )
        val query = and(
            Order::orderType eq orderType,
            Order::orderStatus eq orderStatus,
            Order::modifiedTimestamp gte thirtyMinutesAgo,
        )
        return orderCollection.find(query).toList()
    }

    suspend fun findOrderByCustomerHash(customerHash: String): List<Order> {
        val order = orderCollection.find(Order::customerHash eq customerHash)
        log.info("order details $order")
        return order.toList()
    }

   suspend fun deleteOrderById(order: Order){
     val result = orderCollection.deleteOneById(order.orderId)
      log.info("deleted item $result")
   }

    suspend fun updatePaymentStatus(orderStatus: String, order: Order){
        val filter = Filters.eq("paymentStatus", order.paymentStatus)
        val update = Updates.set("paymentStatus", orderStatus)
        orderCollection.updateOne(filter, update)
    }

    suspend fun findOneAndUpdate(valueToUpdate:String,order:Order):String{
        val filter = Order::orderId eq order.orderId
        val update = setValue(Order::paymentStatus,valueToUpdate)
        val result = orderCollection.findOneAndUpdate(filter, update)
        if (result != null) {
            return "successfully updated payment status"
        } else {
            return "payment status not updated"
        }
    }
    suspend fun updatePayment(orderStatus: OrderStatusResponse, order: Order){
        log.info("update payment details")
        val filter = Filters.eq("orderId", order.orderId)
        val update1 = Updates.set("paymentDetails.$[elem].paymentMethod", orderStatus.payment_method)
        val update2 = Updates.set("paymentDetails.$[elem].paymentMethodType", orderStatus.payment_method_type)
        val update3 = Updates.set("paymentDetails.$[elem].txnGateway", orderStatus.gateway_id)
        val update4 = Updates.set("paymentDetails.$[elem].txnId", orderStatus.txn_id)
        val update5 = Updates.set("paymentDetails.$[elem].txnNetAmount", order.paymentDetails?.transaction_1!![0].txnNetAmount)
        val update6 = Updates.set("paymentDetails.$[elem].txnStatus", order.paymentDetails?.transaction_1!![0].txnStatus)
        val update7 = Updates.set("paymentDetails.$[elem].txnUUID", orderStatus.txn_uuid)
        val update8 = Updates.set("paymentStatus", orderStatus.status)
        val update = Updates.combine(update1,update2,update3,update4,update5,update6,update7,update8)
        val result = orderCollection.updateOne(filter, update, UpdateOptions().arrayFilters(listOf(Filters.exists("elem"))))
        if (result.wasAcknowledged()) {
            log.info(Success_Message)
        } else {
            log.info(Failure_Message)
        }
    }

    suspend fun updateCardDetails(giftCard: BuyingGCSuccess, order: Order){
        val filter = Filters.eq("orderId", order.orderId)
        val update1 = Updates.set("orderLineItems.$[elem].giftCard.giftCardDetails.cardId", giftCard.cards?.get(0)?.cardId)
        val update2 = Updates.set("orderLineItems.$[elem].giftCard.giftCardDetails.cardNumber", giftCard.cards?.get(0)?.cardNumber)
        val update3 = Updates.set("orderLineItems.$[elem].giftCard.giftCardDetails.validity", giftCard.cards?.get(0)?.validity)
        val update4 = Updates.set("orderLineItems.$[elem].giftCard.giftCardDetails.orderId", giftCard.orderId)
        val update = Updates.combine(update1,update2,update3,update4)
        val result = orderCollection.updateOne(filter, update, UpdateOptions().arrayFilters(listOf(Filters.exists("elem"))))
        if (result.wasAcknowledged()) {
            log.info(Success_Message)
        } else {
            log.info(Failure_Message)
        }
    }

    suspend fun updateReloadBalCardDetails(reloadBal: ReloadBalResponse, order: Order){
        val filter = Filters.eq("orderId", order.orderId)
        val update1 = Updates.set("transactionId", reloadBal.transactionId)
        val update2 = Updates.set("orderLineItems.$[elem].giftCard.giftCardDetails.amount", reloadBal.Cards?.get(0)?.balance)
        val update = Updates.combine(update1,update2)
        val result = orderCollection.updateOne(filter, update, UpdateOptions().arrayFilters(listOf(Filters.exists("elem"))) )
        if (result.wasAcknowledged()) {
            log.info(Success_Message)
        } else {
            log.info(Failure_Message)
        }
    }

    suspend fun findingLastItem(): Long{
        val firstOrderId = 100000
        val sort = Document("_id" ,-1)
        log.info("sort document $sort")
        val lastDocument = orderCollection.find().limit(1).sort(sort).first()
        log.info("lastDocument${lastDocument?.json}")
        val lastOrderId = lastDocument?.orderId
        log.info("last document orderId${lastDocument?.orderId}")
        return if(lastOrderId == null){
            log.info("first order Id${firstOrderId.toLong()}")
            firstOrderId.toLong()
        }else{
            val split = lastOrderId.split("_")
            val seqValue = split[1]
            log.info("Split Value $seqValue")
            val nextSequenceId = seqValue.toLong().plus(1)
            log.info("increment Id $nextSequenceId")
            nextSequenceId
        }

    }

    suspend fun saveGiftCardValueDetails(request: GiftCardValues) {
        giftCardCollection.save(request)
    }
    suspend fun saveBankUrlValueDetails(request:BankUrlValuesRequest){
        bankUrlCollection.save(request)
    }
    suspend fun getBankUrlValues(id:String):BankUrlValuesRequest?{
       return bankUrlCollection.findOneById(id)
    }
    suspend fun getGiftCardValues(_id: String): GiftCardValues? {
        return giftCardCollection.findOneById(_id)
    }
    suspend fun getGiftCardRestrictionValues(_id: String): GiftCardRestrictionValues? {
        return giftCardRestrictionValues.findOneById(_id)
    }
    suspend fun bankUrlValues(_id: String): BankUrlValuesRequest? {
        return bankUrlCollection.findOneById(_id)
    }
    suspend fun updateAddOnCardDetails(addOnCardRes:AddOnCardResponse,orderId:String){
        log.info("response body of addOnCard is$addOnCardRes")
        val filter = Document("orderId",orderId)
        val setFields = Document()
        setFields["orderLineItems.0.loyalty.membershipDetails.addOnCardDetails.card_number"] = addOnCardRes.data.card_number
        setFields["orderLineItems.0.loyalty.membershipDetails.addOnCardDetails.relationship_type"] = addOnCardRes.data.relationship_type
        setFields["orderLineItems.0.loyalty.membershipDetails.addOnCardDetails.card_type"] = addOnCardRes.data.card_type
        setFields["orderLineItems.0.loyalty.membershipDetails.addOnCardDetails.fulfilment_status"] = addOnCardRes.data.fulfilment_status
        setFields["orderLineItems.0.loyalty.memberCardDetails.addOnCardDetails.card_number"] = addOnCardRes.data.card_number
        setFields["orderLineItems.0.loyalty.memberCardDetails.addOnCardDetails.relationship_type"] = addOnCardRes.data.relationship_type
        setFields["orderLineItems.0.loyalty.memberCardDetails.addOnCardDetails.card_type"] = addOnCardRes.data.card_type
        setFields["orderLineItems.0.loyalty.memberCardDetails.addOnCardDetails.fulfilment_status"] = addOnCardRes.data.fulfilment_status
        val update = Document("\$set", setFields)
        orderCollection.updateOne(filter,update)
    }

    suspend fun getBookingDetails(req:FetchBookingDetailsDto):Any{
        log.info("request body of fetch bookings$req")
        val confirmationNumberFilter = or(Order::orderLineItems.elemMatch(OrderLineItem::hotel / Hotel::rooms/Room::confirmationId eq req.itenaryNumber),
            Order::orderLineItems.elemMatch(OrderLineItem::hotel / Hotel::bookingNumber eq req.itenaryNumber))
        val emailFilter = Order::orderLineItems.elemMatch(OrderLineItem::hotel / Hotel::rooms / Room::travellerDetails / TravellerDetail::email eq req.email)
        val query = and(confirmationNumberFilter, emailFilter)
        val bookingDetails = orderCollection.find(query).toList()
         return if (bookingDetails.isNotEmpty()){
            bookingDetails
        }else{
            throw  HttpResponseException(ORDER_NOT_FOUND_ERROR_MESSAGE, HttpStatusCode.MovedPermanently)
        }

    }
    suspend fun getBookingDetailsByItineraryNumber(itineraryNumbers: LinkedHashSet<String?>,orders:List<Order>): LinkedHashSet<Order> {
        val mapUpcomingBookingResponse = LinkedHashSet<Order>()
        val bookingItineraryNumbers = LinkedHashSet<String?>()
        for (itineraryNumber in itineraryNumbers){
            orders.map {order->
                if (order.orderLineItems.first().hotel?.bookingNumber?.equals(itineraryNumber) == true){
                    bookingItineraryNumbers.add(order.orderLineItems.first().hotel?.bookingNumber)
                    mapUpcomingBookingResponse.add(order)
                }
            }
        }
        mapUpcomingBookingResponse.forEach {
            it.createdTimestamp = null
            it.modifiedTimestamp = null
            it.orderLineItems.first().hotel?.rooms?.forEach {
                it.createdTimestamp = null
                it.modifiedTimestamp = null
                it.modifyBooking?.createdTimestamp = null
                it.modifyBooking?.modifiedTimestamp = null
            }
        }
        return mapUpcomingBookingResponse
    }
    suspend fun getBookings(itineraryNumbers: LinkedHashSet<String?>, hudiniResponse:HashSet<GetBookingDetailsResponse>): LinkedHashSet<Order> {
        val mapUpcomingBookingResponse = LinkedHashSet<Order>()
        val bookingItineraryNumbers = LinkedHashSet<String?>()
        val status = Order::orderLineItems.elemMatch(OrderLineItem::hotel / Hotel::status nin  listOf(INITIATED, PENDING))
        val bookingNumberFilter = (Order::orderLineItems.elemMatch(OrderLineItem::hotel / Hotel::bookingNumber `in` itineraryNumbers))
        val query= and(status,bookingNumberFilter)
        val bookingDetails = orderCollection.find(query).toList()
        mapUpcomingBookingResponse.addAll(bookingDetails)
         bookingDetails.forEach {booking ->
            bookingItineraryNumbers.add(booking.orderLineItems.first().hotel?.bookingNumber)
            mapUpcomingBookingResponse.addAll(listOf(booking))
        }
        hudiniResponse.forEach { upcomingBooking ->
            upcomingBooking.data?.getHotelBookingDetails?.reservations?.forEach { reservation ->
                when {
                    reservation?.status != "Ignored" -> {
                        if (reservation?.itineraryNumber !in bookingItineraryNumbers) {
                            val nonMatchedOrder = DataMapperUtils.mapReservationToOrder(linkedSetOf(reservation))
                            mapUpcomingBookingResponse.addAll(nonMatchedOrder)
                        }
                    }
                }
            }
        }
        mapUpcomingBookingResponse.forEach {
            it.createdTimestamp = null
            it.modifiedTimestamp = null
            it.orderLineItems.first().hotel?.rooms?.forEach {
                it.createdTimestamp = null
                it.modifiedTimestamp = null
                it.modifyBooking?.createdTimestamp = null
                it.modifyBooking?.modifiedTimestamp = null
            }
        }
        return mapUpcomingBookingResponse
    }
    suspend fun getBookingDetailsByItineraryNumber(itineraryNumbers: List<ItineraryNumber>):LinkedHashSet<Order> {
        val bookings =LinkedHashSet<Order>()
        for (itineraryNumber in itineraryNumbers) {
            val bookingNumberFilter = (Order::orderLineItems.elemMatch(OrderLineItem::hotel / Hotel::bookingNumber eq itineraryNumber.itineraryNumber))
            val bookingDetails = orderCollection.find(bookingNumberFilter).toList()
            bookingDetails.forEach {
                if (!it.orderStatus.equals(INITIATED, ignoreCase = true)) {
                    it.createdTimestamp = null
                    it.modifiedTimestamp = null
                    it.orderLineItems.first().hotel?.rooms?.forEach {
                        it.createdTimestamp = null
                        it.modifiedTimestamp = null
                        it.modifyBooking?.createdTimestamp = null
                        it.modifyBooking?.modifiedTimestamp = null
                    }
                    bookings.addAll(listOf(it))

                }
            }
        }
        return bookings
    }

    suspend fun orderIdGeneration(orderIdPrefix: String): String? {
        val type = ORDER_ID_TYPE
        val result = recordIdCollection.findOne(RecordIdSequence::type eq type)
        if (!result?.value.isNullOrBlank()) {
            val split = result?.value?.split("_")
            log.info("order value for next sequence $split")
            val orderId = split?.get(1)
            val incrementedNumericPart = orderId!!.toLong().plus(INCREMENT_NUMBER)
            val formatOfOrderId = orderIdPrefix.plus(Constants.ORDER).plus(incrementedNumericPart)
            recordIdCollection.updateOne(
                RecordIdSequence::value eq result.value,
                RecordIdSequence(ORDER_ID_TYPE, formatOfOrderId),
                UpdateOptions().upsert(true)
            ).modifiedCount
            val dbresult = recordIdCollection.findOne(RecordIdSequence::type eq type)
            return dbresult?.value
        } else {
            val initialFormatOfOrderId = orderIdPrefix.plus(Constants.ORDER).plus(FIRST_ORDER_ID)
            recordIdCollection.insertOne(
                RecordIdSequence(ORDER_ID_TYPE, initialFormatOfOrderId))
            return initialFormatOfOrderId
        }
    }
    suspend fun pastBookingRecords(req: FetchBookingReq?, type: Boolean): List<Order>{
        log.info("request body of fetch emails$req")
        val mobileNumbers = LinkedHashSet<String>()
        val bookingType = or(Order::orderType eq OrderType.HOTEL_BOOKING, Order::orderType eq OrderType.HOLIDAYS)
        val status = Order::orderLineItems.elemMatch(OrderLineItem::hotel / Hotel::status nin listOf(INITIATED, PENDING))
        val currentDate = LocalDate.now()
        val oneYearAgo= currentDate.minusYears(prop.pastBookings.toLong())
        val orders = if (type) {
            val emailFilter=  orderCollection.find(and(Order::customerEmail ne null,
                Order::customerEmail eq req?.email),bookingType,status,Order ::orderLineItems/ OrderLineItem::hotel/ Hotel ::checkIn lte currentDate.toString()).toList()
            emailFilter.forEach { mobileNumbers.add(it.customerMobile) }
            val mobileFilter =  and(Order::customerMobile ne null,Order::customerMobile `in` mobileNumbers)
            orderCollection.find(mobileFilter,bookingType,status,Order ::orderLineItems/ OrderLineItem::hotel/ Hotel ::checkIn  lte  currentDate.toString(),Order ::orderLineItems/OrderLineItem::hotel / Hotel::checkIn gte  oneYearAgo.toString()).toList()

        } else {
            val mobileFilter =  and(Order::customerMobile ne null,Order::customerMobile eq req?.mobile)
            orderCollection.find(mobileFilter,bookingType,status,Order ::orderLineItems/ OrderLineItem::hotel/ Hotel ::checkIn lte currentDate.toString(),Order ::orderLineItems/OrderLineItem::hotel / Hotel::checkIn gte  oneYearAgo.toString()).toList()
        }
        orders.forEach {
            it.createdTimestamp = null
            it.modifiedTimestamp = null
            it.orderLineItems.first().hotel?.rooms?.forEach {
                it.createdTimestamp = null
                it.modifiedTimestamp = null
                it.modifyBooking?.createdTimestamp = null
                it.modifyBooking?.modifiedTimestamp = null
            }
        }
        return orders
    }
    suspend fun getListOfBookings(req: FetchBookingReq?, type: Boolean): List<Order>{
        log.info("request body of fetch emails$req")
        val mobileNumbers = LinkedHashSet<String>()
        val bookingType = or(Order::orderType eq OrderType.HOTEL_BOOKING, Order::orderType eq OrderType.HOLIDAYS)
        val status = Order::orderLineItems.elemMatch(OrderLineItem::hotel / Hotel::status nin listOf(INITIATED, PENDING))
        val currentDate = LocalDate.now()
        val futureDate= currentDate.plusYears(1)
        val orders = if (type) {
            val emailFilter=  orderCollection.find(and(Order::customerEmail ne null,
                Order::customerEmail eq req?.email),bookingType,status,Order ::orderLineItems/ OrderLineItem::hotel/ Hotel ::checkIn gte currentDate.toString()).toList()

            emailFilter.forEach { mobileNumbers.add(it.customerMobile) }
            val mobileFilter =  and(Order::customerMobile ne null,Order::customerMobile `in` mobileNumbers)
            orderCollection.find(mobileFilter,bookingType,status,Order ::orderLineItems/ OrderLineItem::hotel/ Hotel ::checkIn gte currentDate.toString(),Order ::orderLineItems/OrderLineItem::hotel / Hotel::checkIn lte  futureDate.toString()).toList()

        } else {
            val mobileFilter =  and(Order::customerMobile ne null,Order::customerMobile eq req?.mobile)
            orderCollection.find(mobileFilter,bookingType,status,Order ::orderLineItems/ OrderLineItem::hotel/ Hotel ::checkIn gte currentDate.toString(),Order ::orderLineItems/OrderLineItem::hotel / Hotel::checkIn lte  futureDate.toString()).toList()
        }
       return orders
    }

    suspend fun getCountryDetails(country: String?, state: String?): Any? {
        return when {
            country.isNullOrEmpty() && state.isNullOrEmpty() -> {
                withContext(Dispatchers.IO) {
                        val data = countriesCollection.findOneById(COUNTRY_ID)
                        data?.countries ?:throw HttpResponseException(DATA_ERROR_EXCEPTION, HttpStatusCode.NotFound)
                }
            }
            country != null -> {
                withContext(Dispatchers.IO) {
                    val data = statesCollection.findOne(StatesDto::country.regex("^${Regex.escape(country)}$", options = "i"))
                    data?.states ?:throw HttpResponseException(DATA_ERROR_EXCEPTION, HttpStatusCode.NotFound)
                }
            }
            state != null -> {
                withContext(Dispatchers.IO) {
                    val data = citiesCollection.findOne(CitiesDto::state.regex("^${Regex.escape(state)}$", options = "i"))
                    data?.cities ?: throw HttpResponseException(DATA_ERROR_EXCEPTION, HttpStatusCode.NotFound)
                }
            }
            else ->null
        }
    }

    fun convertToDesiredFormat(dateString:String?): String {
        if (dateString != null) {
                val formatter = DateTimeFormatter.ofPattern(DATE_PATTERN)
                try {
                    val localDate = LocalDate.parse(dateString, formatter)
                    return localDate.format(formatter)
                } catch (e: Exception) {
                    val dateSubstring = extractDateSubstring(dateString)
                    try {
                        val localDate = LocalDate.parse(dateSubstring, formatter)
                        return localDate.format(formatter)
                    } catch (e: Exception) {
                        throw IllegalArgumentException("Invalid date format: $dateString")
                    }
                }
            }
        throw IllegalArgumentException("Input list is empty")
    }
    private fun extractDateSubstring(dateString: String): String {
        val index = dateString.indexOf('T')
        return if (index != -1) {
            dateString.substring(0, minOf(index, 10))
        } else {
            dateString.substring(0, minOf(dateString.length, 10))
        }
    }

    suspend fun findHotelBookingOrders( orderStatus: String, paymentMethod: String, orderType: List<OrderType>): List<Order> {

        val thirtyMinutesAgo = Date.from(
            LocalDateTime.now()
                .minusMinutes(prop.pendingBookingsForDuration.toLong())
                .atZone(ZoneOffset.UTC)
                .toInstant()
        )

        val twoMinutesAgo = Date.from(
            LocalDateTime.now()
                .minusMinutes(prop.twoMinAgoPendingOrders.toLong())
                .atZone(ZoneOffset.UTC)
                .toInstant()
        )

        val query = and(
            Order::orderStatus eq  orderStatus,
            Order::orderType `in` orderType,
            Order::paymentMethod eq paymentMethod,
            Order::retryCount lt prop.bookingRetryCount,
            Order::createdTimestamp lt twoMinutesAgo,
            Order::createdTimestamp gte thirtyMinutesAgo
        )
        return orderCollection.find(query).toList()
    }
    suspend fun getGiftCardDetails(id: String): List<CPGRestrictionDto> {
        val result = gifCardCollection.find(CPGRestrictionDto::_id eq id).toList()
        if (result.isNotEmpty()){
            return result
        }else{
            throw QCInternalServerException(DATA_ERROR_EXCEPTION)
        }
    }
    suspend fun findOrderForTEGCEpicure(orderStatus: String, orderType: List<OrderType>): List<Order> {

        val tenMinutesAgo = Date.from(
            LocalDateTime.now()
                .minusMinutes(prop.tenMinAgoPendingOrders.toLong())
                .atZone(ZoneOffset.UTC)
                .toInstant()
        )
        val twoDaysAgo = Date.from(
            LocalDateTime.now()
                .minusDays(prop.twoDaysAgoPendingOrders.toLong())
                .atZone(ZoneOffset.UTC)
                .toInstant()
        )

        val query = and(
            Order::orderStatus eq  orderStatus,
            Order::orderType `in` orderType,
            Order::modifiedTimestamp lt tenMinutesAgo,
            Order::modifiedTimestamp gte twoDaysAgo
        )
        return orderCollection.find(query).toList()
    }

}