package com.ihcl.order.model.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class ConfigurationProperties(
    val database_url : String,
    val database_name : String,
    val paymentServiceHost: String,
    val loyaltyServiceHost: String,
    val hudiniServiceHost: String,
    val cartServiceHost: String,
    val sanityHost:String,
    val memberShipPlanURL: String,
    val memberLookupValidationURL: String,
    val memberEnrollmentURL: String,
    val orderIdBooking: String,
    val orderIdEpicure: String,
    val orderIdTEGC: String,
    val ssoServiceHost:String,
    val redisKey:String,
    val redisHost:String,
    val redisPort:String,
    val pastBookings:String,
    val upcomingBookings:String,
    val nonDepositRoomCommitBooking:String,
    val timeSecondsOfTTL:String,
    val membershipSource : String?,
    val requestTimeoutMillis:String,
    val connectionPoolMinSize:Int,
    val connectionPoolMaxSize:Int,
    val bookingRetryCount:Int,
    val pendingBookingsForDuration:Int,
    val tenMinAgoPendingOrders:Int,
    val twoDaysAgoPendingOrders:Int,
    val dataEncryptionAndDecryptionKey: String,
    val twoMinAgoPendingOrders:Int,

)

