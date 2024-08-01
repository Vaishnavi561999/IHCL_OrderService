package com.ihcl.order.client


import com.ihcl.order.model.exception.HttpResponseException
import io.github.resilience4j.core.IntervalFunction
import io.github.resilience4j.retry.RetryConfig
import io.github.resilience4j.retry.RetryRegistry
import io.ktor.client.plugins.*
import kotlinx.coroutines.TimeoutCancellationException
import java.io.IOException
import java.net.ConnectException
import java.util.concurrent.TimeoutException

val retryConfig: RetryConfig = RetryConfig.custom<RetryConfig>()
    .maxAttempts(3)
    .intervalFunction(IntervalFunction.ofExponentialBackoff())
    .retryExceptions(
        IOException::class.java,
        TimeoutException::class.java,
        TimeoutCancellationException::class.java,
        ConnectException::class.java,
        HttpRequestTimeoutException::class.java,
        NullPointerException::class.java
    )
    .ignoreExceptions(ClientRequestException::class.java, HttpResponseException::class.java)
    .failAfterMaxAttempts(true)
    .build()
val registry: RetryRegistry = RetryRegistry.of(retryConfig)