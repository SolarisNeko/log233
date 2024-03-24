package com.neko233.log233.api.logger


import com.neko233.log233.api.Log233Manager
import com.neko233.log233.api.LoggerApi
import com.neko233.log233.api.constant.LogLevelEnum
import com.neko233.log233.api.handler.LoggerHandler
import com.neko233.log233.api.structs.MessageObj
import com.neko233.log233.api.utils.LogFormatUtils
import com.neko233.log233.api.utils.StackTraceUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

/**
 * 基于滚动文件的日志实现。
 * 日志文件将根据日期和大小自动滚动。
 *
 * @param loggerName 日志器名字
 */
class LoggerApiByCoroutine
private constructor(
    private val loggerName: String,
) : LoggerApi {

    // 协程作用域，用于异步日志写入
    // 创建一个单线程的调度器
    private val ioDispatcher = Executors.newCachedThreadPool().asCoroutineDispatcher()
    private val coroutineScope = CoroutineScope(ioDispatcher)


    companion object {
        @JvmStatic
        fun create(loggerName: String): LoggerApi {
            return LoggerApiByCoroutine(loggerName)
        }

        private val cache: ConcurrentHashMap<String, Boolean> = ConcurrentHashMap()

    }

    override fun getLoggerName(): String {
        TODO("Not yet implemented")
    }

    override fun isOpenLogLevel(levelEnum: LogLevelEnum): Boolean {
        return cache.computeIfAbsent(loggerName) {
            val loggerMap = Log233Manager.instance.getLoggerHandlerWithoutRoot()
            for ((name, logger) in loggerMap) {
                if (loggerName.isNotBlank() && !loggerName.startsWith(name)) {
                    continue
                }
                if (logger.levelEnum.level >= levelEnum.level) {
                    return@computeIfAbsent true
                }
            }

            // root
            val root = Log233Manager.instance.getLoggerHandlerRoot()
            if (root.levelEnum.level >= levelEnum.level) {
                return@computeIfAbsent true
            }
            return@computeIfAbsent false
        }
    }

    /**
     * 内部日志记录函数。
     *
     * @param level 日志级别（如DEBUG, INFO, WARN, ERROR）
     * @param msgTemplate 日志消息模板
     * @param args 日志消息参数
     */
    override fun log(
        levelEnum: LogLevelEnum,
        messageTemplate: String,
        args: Array<out Any?>
    ) {
        val curTimeMs = System.currentTimeMillis()

        val threadName = Thread.currentThread().name
        val callPositionStr = StackTraceUtil.getCallPositionStr(4)

        // 格式化消息
        val (message, throwable) = LogFormatUtils.formatLogArgs(messageTemplate, args)

        // 业务线程中
        val messageObj = MessageObj.create(
            curTimeMs,
            threadName,
            callPositionStr,
            levelEnum,
            message,
            throwable
        )

        // async
        coroutineScope.launch {
            writeLogToLogger(messageObj) { logger, messageObj ->
                logger.logMessage(messageObj)
            }

        }
    }

    private fun writeLogToLogger(
        messageObj: MessageObj,
        action: (logger: LoggerHandler, messageObj: MessageObj) -> Unit
    ) {
        val loggerHandlerRoot = Log233Manager.instance.getLoggerHandlerRoot()
        val loggerMap = Log233Manager.instance.getLoggerHandlerWithoutRoot()

        for ((name, logger) in loggerMap) {
            // 空 = 布过滤
            if (this.loggerName.isNotBlank()) {
                if (!this.loggerName.startsWith(name)) {
                    continue
                }
            }
            try {
                action.invoke(logger, messageObj)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }

        // root
        try {
            action.invoke(loggerHandlerRoot, messageObj)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }


    override fun logBusiness(
        uniqueName: String,
        obj: Any,
    ) {
        // async
        coroutineScope.launch {
            val loggerHandlerRoot = Log233Manager.instance.getLoggerHandlerRoot()
            val loggerMap = Log233Manager.instance.getLoggerHandlerWithoutRoot()

            for ((name, logger) in loggerMap) {
                if (!loggerName.startsWith(name)) {
                    continue
                }
                try {
                    logger.logBusiness(uniqueName, obj)
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }

            // root
            try {
                loggerHandlerRoot.logBusiness(uniqueName, obj)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }


}
