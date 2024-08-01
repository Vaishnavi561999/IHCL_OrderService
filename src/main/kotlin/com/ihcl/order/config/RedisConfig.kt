package com.ihcl.order.config

import com.ihcl.order.model.dto.response.RedisDto
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import redis.clients.jedis.ConnectionPoolConfig
import redis.clients.jedis.DefaultJedisClientConfig
import redis.clients.jedis.HostAndPort
import redis.clients.jedis.JedisPooled
import javax.net.ssl.SSLSocketFactory

object RedisConfig {
    //redis://vzaLORNA7cO9HjIDYg32CTqElV6CzGziDAzCaGRlwCY=@app-dev-redis.redis.cache.windows.net:6379
    private val log: Logger = LoggerFactory.getLogger(javaClass)
    private val prop = Configuration.env
    private val jedisPooled = initialize()
    fun setKeyAndTTL(key: String, value: String, timeInSeconds: Long) {
        try {
            jedisPooled.set(key,value)
            jedisPooled.expire(key,timeInSeconds)
        } catch (e:Exception){
            log.error("Redis Error: ${e.localizedMessage} while storing value for Key : [${key}]. Cause: "+e.cause)
        }
    }
    fun getKey(key:String):String?{
        var value : String? = null
        try {
            value = jedisPooled.get(key)
        } catch (e:Exception){
            log.error("Redis Error: ${e.localizedMessage} while reading value for Key : [${key}]. Cause: "+e.cause)
        }
        return value
    }
    fun shutdown(){
        try {
            jedisPooled.close()
        }catch (e:Exception){
            log.error("Redis Error: ${e.localizedMessage} while releasing redis connection]")
        }
    }
    private fun initialize(): JedisPooled {
        // val redisUri = "redis://${prop.redisKey}=${prop.redisHost}:${prop.redisPort}"
        val sslSocketFactory = SSLSocketFactory.getDefault()
        val poolConfig = ConnectionPoolConfig()
        // maximum active connections in the pool,
        // tune this according to your needs and application type
        // default is 8
        poolConfig.maxTotal = 20

        // maximum idle connections in the pool, default is 8
        poolConfig.maxIdle = 20
        // minimum idle connections in the pool, default 0
        poolConfig.minIdle = 0

        // Enables waiting for a connection to become available.
        poolConfig.blockWhenExhausted = true
        // The maximum number of seconds to wait for a connection to become available
        poolConfig.setMaxWait(Duration.ofMillis(prop.requestTimeoutMillis.toLong()))

        // Enables sending a PING command periodically while the connection is idle.
        poolConfig.testWhileIdle = true
        // controls the period between checks for idle connections in the pool
        poolConfig.timeBetweenEvictionRuns = Duration.ofSeconds(1)

        // JedisPooled does all hard work on fetching and releasing connection to the pool
        // to prevent connection starvation
        // val jedis : JedisPooled = JedisPooled(poolConfig,redisUri);
        log.info("Connecting to Redis using HOST: ${prop.redisHost}, PORT: ${prop.redisPort}")
        val hostAndPort = HostAndPort(prop.redisHost,prop.redisPort.toInt())
        val jedisPooled = JedisPooled(
            hostAndPort,
            DefaultJedisClientConfig.builder()
                .socketTimeoutMillis(prop.requestTimeoutMillis.toInt()) // set timeout to 5 seconds
                .connectionTimeoutMillis(prop.requestTimeoutMillis.toInt())
                .password("${prop.redisKey}=")// set connection timeout to 5 seconds
                .ssl(true) //.sslSocketFactory(sslSocketFactory as SSLSocketFactory?)
                .build(),
            poolConfig
        )
        log.info("Connected to Redis...${jedisPooled}")
        return jedisPooled
    }
    fun deleteFromRedis(data: RedisDto): Boolean {
        val delResult = jedisPooled.del(data.key)
        return delResult == 1L // Returns true if the key was deleted successfully
    }
}
