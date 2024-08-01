package com.ihcl.order.service

import com.ihcl.order.service.v1.OrderService
import com.ihcl.order.service.v1.ApportionOrderService
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val serviceModule = module {
   singleOf(:: OrderService)
   singleOf(:: ApportionOrderService)
}