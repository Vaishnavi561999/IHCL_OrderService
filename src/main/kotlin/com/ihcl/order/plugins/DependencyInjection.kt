package com.ihcl.order.plugins

import com.ihcl.order.repository.repositoryModule
import com.ihcl.order.service.serviceModule
import com.ihcl.order.utils.validatorModule
import io.ktor.server.application.*
import org.koin.core.context.startKoin

fun Application.configureDependencyInjection() {
   startKoin {
       modules(serviceModule, validatorModule, repositoryModule)
   }
}
