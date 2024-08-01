package com.ihcl.order.repository

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(:: OrderRepository)
    singleOf(:: LoyaltyRepository)
    singleOf(:: ApportionOrderRepository)
}