package com.ihcl.order.utils

import org.koin.dsl.module

val validatorModule = module {
    single {
        DataMapperUtils
    }
}