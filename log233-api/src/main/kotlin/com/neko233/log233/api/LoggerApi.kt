package com.neko233.log233.api

import com.neko233.log233.api.constant.LogLevelEnum

@JvmDefaultWithCompatibility
interface LoggerApi {

    /**
     * 获取 logger 名字
     */
    fun getLoggerName(): String

    /**
     * 业务日志
     * @param uniqueName 唯一key | 一般作为数据表名
     * @param obj 业务对象 ｜ 需要是简单的结构, 不允许有树/森林, 否则容易有 bug
     */
    fun logBusiness(
        uniqueName: String,
        obj: Any,
    )

    /**
     * 是否能够使用某个日志等级
     */
    fun isOpenLogLevel(levelEnum: LogLevelEnum): Boolean

    fun isNotOpenLogLevel(levelEnum: LogLevelEnum): Boolean {
        return !isOpenLogLevel(levelEnum)
    }


    fun log(
        levelEnum: LogLevelEnum,
        messageTemplate: String,
        args: Array<out Any?>
    )

    fun debug(
        msg: String,
        vararg args: Any?,
    ) {
        val levelEnum = LogLevelEnum.DEBUG
        if (isNotOpenLogLevel(levelEnum)) {
            return
        }
        log(levelEnum, msg, args)
    }

    fun info(
        msg: String,
        vararg args: Any?,
    ) {
        val levelEnum = LogLevelEnum.INFO
        if (isNotOpenLogLevel(levelEnum)) {
            return
        }
        log(levelEnum, msg, args)
    }

    fun warn(
        msg: String,
        vararg args: Any?,
    ) {
        val levelEnum = LogLevelEnum.WARN
        if (isNotOpenLogLevel(levelEnum)) {
            return
        }
        log(levelEnum, msg, args)
    }

    fun error(
        msg: String,
        vararg args: Any?,
    ) {
        val levelEnum = LogLevelEnum.ERROR
        if (isNotOpenLogLevel(levelEnum)) {
            return
        }
        log(levelEnum, msg, args)
    }

    // Debug level logging

    fun debugLazy(
        msg: String,
        lazyArgs: () -> Array<Any?>,
    ) {
        if (isNotOpenLogLevel(LogLevelEnum.DEBUG)) {
            return
        }
        debug(msg, *lazyArgs())
    }

    // Info level logging

    fun infoLazy(
        msg: String,
        lazyArgs: () -> Array<Any?>,
    ) {
        if (isNotOpenLogLevel(LogLevelEnum.INFO)) {
            return
        }
        info(msg, *lazyArgs())
    }

    // Warn level logging
    fun warnLazy(
        msg: String,
        lazyArgs: () -> Array<Any?>,
    ) {
        if (isNotOpenLogLevel(LogLevelEnum.WARN)) {
            return
        }
        warn(msg, *lazyArgs())
    }

    // Error level logging
    fun errorLazy(
        msg: String,
        lazyArgs: () -> Array<Any?>,
    ) {
        if (isNotOpenLogLevel(LogLevelEnum.ERROR)) {
            return
        }
        error(msg, *lazyArgs())
    }


}