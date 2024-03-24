package com.neko233.log233.api.config.structs

import com.neko233.easyxml.data.XmlObject
import com.neko233.log233.api.constant.LogLevelEnum
import com.neko233.log233.api.utils.XmlUtils
import java.util.stream.Collectors

/**
 * 日志配置
 *
 * @author SolarisNeko
 * Date on 2024-01-01
 * */
class LoggerConfig {

    // 名字
    var name: String = ""


    var rootFlag: Boolean = false

    // 日志等级
    var levelEnum: LogLevelEnum = LogLevelEnum.DEBUG

    // 消息格式
    var messageFormat: String = ""

    // 追写器名字
    var appenderNameSet = HashSet<String>()

    companion object {
        @JvmStatic
        fun fromHandler(xmlForLogger: XmlObject): LoggerConfig {
            return LoggerConfig().apply {
                this.rootFlag = false
                this.name = (xmlForLogger.getAttribute("name") ?: "").trim()
                if ("root".equals(this.name, ignoreCase = true)) {
                    throw RuntimeException("can not use name = ${this.name} for <loggerHandler> ")
                }

                val levelName = (xmlForLogger.getAttribute("level") ?: "").trim().uppercase()
                if (levelName.isBlank()) {
                    throw RuntimeException("can not use level = ${this.name} for <loggerHandler> ")
                }
                this.levelEnum = LogLevelEnum.valueOf(levelName)

                this.messageFormat = XmlUtils.getOneChildNodeByName(xmlForLogger, "messageFormat")
                    ?.nodeValue
                    ?: ""

                val appenderNameList = XmlUtils.getAllChildNodeByName(xmlForLogger, "appender")
                    .stream()
                    .map { it.getAttribute("name") ?: "" }
                    .collect(Collectors.toList())
                this.appenderNameSet.addAll(appenderNameList)

            }
        }

        fun fromRoot(xmlForLogger: XmlObject): LoggerConfig {
            return LoggerConfig().apply {
                this.rootFlag = true
                this.name = ""

                val levelName = (xmlForLogger.getAttribute("level") ?: "").trim().uppercase()
                if (levelName.isBlank()) {
                    throw RuntimeException("can not use level = ${this.name} for <loggerHandler> ")
                }
                this.levelEnum = LogLevelEnum.valueOf(levelName)

                this.messageFormat = XmlUtils.getOneChildNodeByName(xmlForLogger, "messageFormat")
                    ?.nodeValue
                    ?: ""

                val appenderNameList = XmlUtils.getAllChildNodeByName(xmlForLogger, "appender")
                    .stream()
                    .map { it.getAttribute("name") ?: "" }
                    .collect(Collectors.toList())
                this.appenderNameSet.addAll(appenderNameList)

            }
        }
    }


}