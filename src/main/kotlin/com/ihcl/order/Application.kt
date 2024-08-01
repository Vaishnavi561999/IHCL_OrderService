package com.ihcl.order


import com.ihcl.order.config.Configuration
import com.ihcl.order.config.MongoConfig
import com.ihcl.order.config.RedisConfig
import com.ihcl.order.plugins.*
import com.ihcl.order.route.v1.configureOrderRouting
import com.ihcl.order.route.v1.configureStaticPagesRouting
import io.ktor.server.application.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)
fun Application.module() {
    Configuration.initConfig(environment)
    configureDependencyInjection()
    configureHTTP()
    configureSerialization()
    configureStatusPages()
    configureOrderRouting()
    configureStaticPagesRouting()
    environment.monitor.subscribe(ApplicationStopPreparing){
        println("Shutting down  Redis..")
        RedisConfig.shutdown()
    }
}




