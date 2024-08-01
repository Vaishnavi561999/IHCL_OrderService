package com.ihcl.order.route.v1

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureStaticPagesRouting() {
    routing {
        get("/"){
            call.respondText("Welcome to Order Service....!!")
        }
    }
}