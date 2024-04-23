package com.neko233.log233.api.extension

import com.neko233.log233.api.LoggerApi
import com.neko233.log233.api.LoggerApiFactory
import java.util.concurrent.ConcurrentHashMap


@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Log233Annotation {

    companion object {
        /**
         * 接口自定日志
         */
        val <reified T : Log233ExtensionObjApi> T.logger233: LoggerApi
            inline get() = LoggerApiFactory.getLogger(T::class.java)
    }
}

interface Log233ExtensionObjApi {
    companion object {
        // 使用一个静态 map 来存储和复用 LoggerApi 实例
        private val loggerApiCache = ConcurrentHashMap<Class<*>, LoggerApi>()
    }

    /**
     * 获取与调用类关联的 LoggerApi 实例。
     * 如果缓存中已存在对应实例，则直接返回缓存的实例；
     * 否则，使用 LoggerApiFactory 为调用类创建一个新的 LoggerApi 实例，并存入缓存中。
     *
     * @return LoggerApi 与调用类关联的 LoggerApi 实例。
     */
    fun getClassLoggerApi(): LoggerApi {
        return loggerApiCache.computeIfAbsent(this.javaClass) {
            LoggerApiFactory.getLogger(this.javaClass)
        }
    }

}