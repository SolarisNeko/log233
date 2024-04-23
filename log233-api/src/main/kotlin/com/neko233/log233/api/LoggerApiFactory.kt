package com.neko233.log233.api

import com.neko233.log233.api.logger.LoggerApiByCoroutine
import kotlin.reflect.KClass

/**
 * 日志工厂
 *
 * @author SolarisNeko
 * Date on 2024-01-01
 * */
object LoggerApiFactory {

    @JvmStatic
    fun <T : Any> getLogger(clazz: KClass<T>): LoggerApi {
        return getLogger(clazz.java.name)
    }


    @JvmStatic
    fun <T> getLogger(clazz: Class<T>): LoggerApi {
        return getLogger(clazz.name)
    }


    @JvmStatic
    fun getLogger(loggerName: String): LoggerApi {
        // lazy init
        if (Log233Manager.instance.isNotInit()) {
            Log233Manager.instance.init()
        }

        // create logger
        return Log233Manager.instance.createLoggerOrDefault(loggerName) {
            LoggerApiByCoroutine.create(loggerName)
        }
    }


}