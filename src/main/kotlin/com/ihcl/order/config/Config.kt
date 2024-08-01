package com.ihcl.order.config


import com.ihcl.order.model.dto.ConfigurationProperties
import io.ktor.server.application.*

object Configuration {
    lateinit var env: ConfigurationProperties

    fun initConfig(environment: ApplicationEnvironment){
        env = ConfigurationProperties(
            database_url = environment.config.property("ktor.database.connectionString").getString(),
            database_name = environment.config.property("ktor.database.databaseName").getString(),
            paymentServiceHost = environment.config.property("ktor.deployment.paymentServiceHost").getString(),
	        loyaltyServiceHost = environment.config.property("ktor.deployment.loyaltyServiceHost").getString(),
	        hudiniServiceHost = environment.config.property("ktor.deployment.hudiniServiceHost").getString(),
            cartServiceHost = environment.config.property("ktor.deployment.cartServiceHost").getString(),
            sanityHost=environment.config.property("ktor.deployment.sanityHost").getString(),
            memberShipPlanURL = environment.config.property("ktor.deployment.memberShipPlanURL").getString(),
            memberLookupValidationURL = environment.config.property("ktor.deployment.memberLookupValidationURL").getString(),
            memberEnrollmentURL = environment.config.property("ktor.deployment.memberEnrollmentURL").getString(),
            orderIdBooking = environment.config.property("ktor.deployment.orderIdBooking").getString(),
            orderIdEpicure = environment.config.property("ktor.deployment.orderIdEpicure").getString(),
            orderIdTEGC = environment.config.property("ktor.deployment.orderIdTEGC").getString(),
            ssoServiceHost =environment.config.property("ktor.deployment.ssoServiceHost").getString(),
            redisKey = environment.config.property("ktor.deployment.redisKey").getString(),
            redisHost = environment.config.property("ktor.deployment.redisHost").getString(),
            redisPort = environment.config.property("ktor.deployment.redisPort").getString(),
            pastBookings =environment.config.property("ktor.deployment.pastBookings").getString(),
            upcomingBookings = environment.config.property("ktor.deployment.upcomingBookings").getString(),
            nonDepositRoomCommitBooking = environment.config.property("ktor.deployment.nonDepositRoomCommitBooking").getString(),
            timeSecondsOfTTL = environment.config.property("ktor.deployment.timeSecondsOfTTL").getString(),
            membershipSource = environment.config.property("ktor.deployment.membershipSource").getString(),
            requestTimeoutMillis = environment.config.property("ktor.deployment.requestTimeoutMillis").getString(),
            connectionPoolMinSize = environment.config.property("ktor.database.connectionPoolMinSize").getString().toInt(),
            connectionPoolMaxSize = environment.config.property("ktor.database.connectionPoolMaxSize").getString().toInt(),
            bookingRetryCount = environment.config.property("ktor.deployment.bookingRetryCount").getString().toInt(),
            pendingBookingsForDuration = environment.config.property("ktor.deployment.pendingBookingsForDuration").getString().toInt(),
            tenMinAgoPendingOrders = environment.config.property("ktor.deployment.tenMinAgoPendingOrders").getString().toInt(),
            twoDaysAgoPendingOrders = environment.config.property("ktor.deployment.twoDaysAgoPendingOrders").getString().toInt(),

            dataEncryptionAndDecryptionKey = environment.config.property("ktor.deployment.dataEncryptionAndDecryptionKey").getString(),
            twoMinAgoPendingOrders = environment.config.property("ktor.deployment.twoMinAgoPendingOrders").getString().toInt()
        )
    }
}
