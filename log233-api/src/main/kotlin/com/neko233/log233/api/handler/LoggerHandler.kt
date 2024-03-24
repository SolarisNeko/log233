package com.neko233.log233.api.handler

import com.neko233.log233.api.appender.AppenderApi
import com.neko233.log233.api.config.structs.LoggerConfig
import com.neko233.log233.api.constant.LogLevelEnum
import com.neko233.log233.api.structs.MessageObj
import java.util.stream.Collectors

/**
 * 日志处理器
 */
class LoggerHandler {


    // logger 名字前缀匹配 | 空 = 所有都匹配
    var loggerNamePrefixMatcher: String = ""
        private set

    // 消息格式
    var messageFormat: String = ""
        private set

    // 日志等级
    var levelEnum: LogLevelEnum = LogLevelEnum.DEBUG

    // 追写器名字
    var appenderNameSet = HashSet<String>()

    // 追写器 ref
    var appenderList: List<AppenderApi> = emptyList()
        private set

    companion object {
        @JvmStatic
        fun from(
            config: LoggerConfig,
            appenderNameToHandlerMap: Map<String, AppenderApi>
        ): LoggerHandler {
            return LoggerHandler().apply {
                this.loggerNamePrefixMatcher = config.name
                this.levelEnum = config.levelEnum
                this.messageFormat = config.messageFormat
                this.appenderNameSet = config.appenderNameSet
                this.appenderList = this.appenderNameSet
                    .stream()
                    .map { appenderNameToHandlerMap.get(it) }
                    .collect(Collectors.toList())
                    .filterNotNull()
            }
        }

    }

    fun isHandlerLevel(level: LogLevelEnum): Boolean {
        return this.levelEnum.level <= level.level
    }

    fun isNotHandlerLevel(level: LogLevelEnum): Boolean {
        return !isHandlerLevel(level)
    }

    fun logMessage(messageObj: MessageObj) {
        if (isNotHandlerLevel(messageObj.level)) {
            return
        }
        for (appenderApi in appenderList) {
            appenderApi.appendMessage(messageObj)
        }
    }

    fun logBusiness(
        uniqueName: String,
        obj: Any
    ) {
        for (appenderApi in appenderList) {
            appenderApi.logBusiness(uniqueName, obj)
        }
    }
}