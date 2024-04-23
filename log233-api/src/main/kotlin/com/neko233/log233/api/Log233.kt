package com.neko233.log233.api

import com.neko233.log233.api.constant.Log233Constant
import java.io.File
import kotlin.reflect.KClass

object Log233 {

    /**
     * 设置 logger 配置
     */
    @JvmStatic
    fun setLoggerConfig(
        resourceFilePath: String = "log233.xml",
        fromResourcesFlag: Boolean = true
    ) {
        val absolutePath: String = if (fromResourcesFlag) {
            val resourceUrl = Log233::class.java.classLoader.getResource(resourceFilePath)!!
            val resourceFile = File(resourceUrl.toURI())
            resourceFile.absolutePath
        } else {
            resourceFilePath
        }

        println("[Log233] current config path = $absolutePath")
        System.setProperty(Log233Constant.CONFIG_FILE_ENV_KEY, absolutePath)
    }

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