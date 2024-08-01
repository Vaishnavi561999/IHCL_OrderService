package com.ihcl.order.client


import com.google.gson.Gson
import com.ihcl.order.utils.Constants
import io.ktor.client.engine.okhttp.*
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.serialization.gson.*
import okhttp3.ConnectionPool
import okhttp3.Protocol
import java.util.concurrent.TimeUnit

val client = HttpClient(OkHttp) {
    engine {

        config {
            // this: OkHttpClient.Builder
            connectionPool(ConnectionPool(100, 5, TimeUnit.MINUTES))
            readTimeout(Constants.REQUEST_TIME_OUT.toLong(), TimeUnit.MILLISECONDS)
            connectTimeout(Constants.REQUEST_TIME_OUT.toLong(), TimeUnit.MILLISECONDS)
            writeTimeout(Constants.REQUEST_TIME_OUT.toLong(), TimeUnit.MILLISECONDS)
            retryOnConnectionFailure(true)
            protocols(listOf(Protocol.HTTP_2, Protocol.HTTP_1_1))
            followRedirects(true)

        }

    }

    install(ContentNegotiation) {
        gson()
        register(contentType = ContentType.Text.Html,converter =  GsonConverter(Gson()))
    }
    install(Logging) {
        level = LogLevel.ALL
    }
    install(HttpTimeout)
}


