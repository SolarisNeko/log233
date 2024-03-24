package com.neko233.log233.api.appender.imp

import com.neko233.log233.api.appender.AbstractAppenderApi
import com.neko233.log233.api.structs.MessageObj
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat

class AppenderApiByConsole : AbstractAppenderApi() {

    // 日期格式化
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS")

    override fun initYourConfig(kvMap: Map<String, String>) {

    }

    override fun appendMessage(messageObj: MessageObj) {
        val dateTimeStr = dateFormatter.format(messageObj.createTimeMs)

        val message = messageObj.message
        val threadName = messageObj.threadName
        val level = messageObj.level.name
        val callPositionStr = messageObj.callPositionStr
        val throwable: Throwable? = messageObj.throwable

        val logBuilder = StringBuilder()
            .append(dateTimeStr)
            .append(" ")
            .append(level)
            .append(" ")
            .append(threadName)
            .append(" ")
            .append(callPositionStr)
            .append(" - ")
            .append(message)

        // 如果存在throwable，追加其堆栈信息
        if (throwable != null) {
            val sw = StringWriter()
            val pw = PrintWriter(sw)
            throwable.printStackTrace(pw)
            logBuilder.append("\n").append(sw.toString())
        }

        println(logBuilder.toString())

    }


    override fun logBusiness(
        uniqueName: String,
        obj: Any
    ) {
        // nothing
    }
}