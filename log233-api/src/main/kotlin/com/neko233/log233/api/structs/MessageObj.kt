package com.neko233.log233.api.structs

import com.neko233.log233.api.constant.LogLevelEnum
import java.text.SimpleDateFormat
import java.util.*

// 消息对象，用于封装日志信息
class MessageObj private constructor(
    // 创建时间（毫秒）
    val createTimeMs: Long,
    // 线程名称
    val threadName: String,
    // 调用位置字符串
    val callPositionStr: String,
    // 日志级别
    val level: LogLevelEnum,
    // 日志消息
    val message: String,
// 异常对象，如果有的话
    val throwable: Throwable?
) {
    companion object {
        // 静态 create 方法用于构造 MessageObj 实例
        @JvmStatic
        fun create(
            curTimeMs: Long, // 当前时间（毫秒）
            threadName: String, // 线程名称
            callPositionStr: String, // 调用位置字符串
            level: LogLevelEnum, // 日志级别
            message: String, // 日志消息
            throwable: Throwable? // 异常对象，如果有的话
        ): MessageObj {
            return MessageObj(
                curTimeMs,
                threadName,
                callPositionStr,
                level,
                message,
                throwable,
            )
        }
    }

    // 将日志对象转换为字符串格式
    override fun toString(): String {
        // 日志格式为：时间戳 [线程名] 日志级别 - 消息 - 调用位置 - 异常信息（如果存在）
        val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            .format(Date(createTimeMs))
        val throwableStr = throwable?.let { "\n${it.stackTraceToString()}" } ?: ""
        return "$dateStr [$threadName] $level $callPositionStr - $message $throwableStr"
    }
}
