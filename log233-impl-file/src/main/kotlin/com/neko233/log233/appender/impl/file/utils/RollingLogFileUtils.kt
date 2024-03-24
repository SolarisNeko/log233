package com.neko233.log233.appender.impl.file.utils

import com.neko233.log233.appender.impl.file.structs.ParsedFileName

object RollingLogFileUtils {

    fun parseLogFileName(logFileName: String): ParsedFileName? {
        // 修改正则表达式以匹配可选的dateFormat和afterIndex
        val regex = Regex("^(.+)\\{dateFormat}?(.*)\\{index}?(.+)$")
        val matchResult = regex.find(logFileName)

        return matchResult?.let {
            val (beforeDateFormat, dateFormat, afterIndex) = it.destructured
            val simpleName = beforeDateFormat + (afterIndex ?: "")
            ParsedFileName(simpleName, dateFormat ?: "", afterIndex ?: "")
        }
    }
}
