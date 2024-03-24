package com.neko233.log233

import com.neko233.log233.api.LoggerApi
import com.neko233.log233.api.LoggerApiFactory
import kotlin.reflect.KClass

object Log233 {

    /**
     * 介绍
     */
    @JvmStatic
    fun readMe(): String {
        return """
            API introduce :
            LoggerApiFactory = SLF4J LoggerFactory
            LoggerApi = SLF4J Logger
            AppenderApi = logback <appender>
            loggerHandlerRootHandler = logback XML <root>
            LoggerHandler = logback XML <logger>
        """.trimIndent()
    }

    @JvmStatic
    fun <T : Any> getLogger(clazz: KClass<T>): LoggerApi {
        return LoggerApiFactory.getLogger(clazz.java.name)
    }


    @JvmStatic
    fun <T> getLogger(clazz: Class<T>): LoggerApi {
        return LoggerApiFactory.getLogger(clazz.name)
    }


    @JvmStatic
    fun getLogger(loggerName: String): LoggerApi {
        return LoggerApiFactory.getLogger(loggerName)
    }

}