package com.neko233.log233.appender.impl.file

import com.neko233.log233.api.appender.AbstractAppenderApi
import com.neko233.log233.api.structs.MessageObj
import com.neko233.log233.appender.impl.file.constant.FileSizeUnit
import com.neko233.log233.appender.impl.file.utils.FileUtilsForLog233
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.nio.file.Paths
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.atomic.AtomicLong

/**
 * 滚动文件追写器
 */
class AppenderApiByRollingFile : AbstractAppenderApi() {

    // 日志文件存储目录
    private lateinit var logDirectory: String

    // 最大文件大小
    private var maxSizePerFile: Long = 10

    // 最大文件大小（单位：MB）
    private var maxFileSizeUnit: FileSizeUnit = FileSizeUnit.MB

    // 最大文件大小（单位：字节）
    private val maxFileSizeInBytes = maxSizePerFile * 1024 * 1024

    // 标记是否已经记录过文件数量限制日志
    private var hasLoggedFileLimit = false

    // 日期格式化工具
    private lateinit var dateFormatter: DateFormat

    // 日志文件格式
    private lateinit var fileSimpleNamePattern: String

    // 同一个文件, 最多使用的 {count}
    private var sameFileMaxCount = 1

    /**
     * 状态部分
     */
    // 当前日志文件
    @Volatile
    private var currentLogFile: File? = null

    // 当前文件大小（单位：字节），使用原子类确保线程安全
    private var fileSizeInBytes = AtomicLong(0)


    companion object {

        // 协程作用域，用于异步写日志
        private val coroutineScope = CoroutineScope(Dispatchers.IO)
    }

    override fun initYourConfig(kvMap: Map<String, String>) {
// 初始化配置
        val logFileName = kvMap["logFileName"]
        if (logFileName.isNullOrBlank()) {
            throw IllegalArgumentException("logFileName is blank")
        }


        val keyValue = kvMap.getOrDefault("sameFileMaxCount", "1")

        try {
            val number = keyValue.toInt()
                ?: throw IllegalArgumentException("Value is null or not a number")

            if (number >= 1) {
                this.sameFileMaxCount = number
            } else {
                // 默认只允许一个文件
                this.sameFileMaxCount = 1
            }
        } catch (e: Exception) {
            throw IllegalArgumentException("Error: 'sameFileMaxCount' error. number str = ${keyValue}")
        }


        val dateTimeFormat = kvMap.getOrDefault("dateTimeFormat", "yyyy-MM-dd")

        // 将给定的日志目录路径转换为绝对路径
        val logFile = File(logFileName)

        // 目录
        this.logDirectory = logFile.parentFile.absolutePath
        // 单个文件名
        this.fileSimpleNamePattern = logFile.name

        this.dateFormatter = SimpleDateFormat(dateTimeFormat)


        val maxSizePerFile = kvMap["maxSizePerFile"]
        if (maxSizePerFile != null) {
            val (fileSize, sizeUnit) = FileSizeUnit.fromString(maxSizePerFile)
            this.maxSizePerFile = fileSize
            this.maxFileSizeUnit = sizeUnit
        }



        rotateLogFile()
    }

    override fun appendMessage(messageObj: MessageObj) {
        // 异步写日志
        coroutineScope.launch {
            val currentLogFile0 = currentLogFile
                ?: return@launch
            // 格式化日志消息
            val logMessage = messageObj.toString()

            checkRollOver(logMessage)

            FileUtilsForLog233.createFileIfNotExists(currentLogFile0)
            currentLogFile0.appendText(logMessage + "\n")
        }
    }


    /**
     * 检查是否需要滚动
     */

    private fun checkRollOver(message: String) {
        synchronized(this) {
            // 检查是否需要滚动日志文件
            val isNotSameDay = !isSameDay(currentLogFile!!)
            val overSize = fileSizeInBytes.get() + message.toByteArray().size > maxFileSizeInBytes
            if (currentLogFile == null || overSize || isNotSameDay) {
                rotateLogFile()
            }
            fileSizeInBytes.addAndGet(message.toByteArray().size.toLong())
        }
    }


    private fun rotateLogFile() {
        synchronized(this) {
            val datePart = dateFormatter.format(Date()) // 获取当前日期，用于构建文件名

            var count = 1 // 开始的文件序号
            var logFile: File? = null // 初始化日志文件变量

            // 循环检查是否需要创建新的日志文件
            while (count <= this.sameFileMaxCount) {
                // 构建日志文件名
                val fileName = fileSimpleNamePattern
                    .replace("dateTime", datePart)
                    .replace("count", count.toString())

                val nextLogFile = Paths.get(logDirectory, fileName).toAbsolutePath().normalize().toFile()


                // 检查文件是否存在以及大小是否未超过限制
                if (!nextLogFile.exists() || nextLogFile.length() < maxFileSizeInBytes) {
                    // 确定新的日志文件
                    logFile = nextLogFile
                    hasLoggedFileLimit = false // 重置日志数量限制标记
                    break // 退出循环
                }

                count++ // 文件序号递增，检查下一个文件
            }

            // 根据循环结果处理日志文件变量
            if (logFile != null) {
                // 更新当前日志文件
                currentLogFile = logFile
                // 更新当前日志文件大小
                fileSizeInBytes.set(currentLogFile!!.length())
            } else if (!hasLoggedFileLimit) {
                // 达到文件数量限制，且未记录过限制日志，则输出日志
                println(
                    """
                ------------------------------ log233 ----------------------------
                Error 已达到日志文件数量上限 $sameFileMaxCount。
                ------------------------------ /log233 ----------------------------
            """.trimIndent()
                )

                // 设置已记录文件数量限制日志的标记
                hasLoggedFileLimit = true
            }
        }
    }


    private fun isSameDay(logFile: File): Boolean {
        // 判断文件的最后修改日期是否是当前日期
        val lastModified = Date(logFile.lastModified())
        val currentDate = Date()
        return dateFormatter.format(lastModified) == dateFormatter.format(currentDate)
    }

    override fun logBusiness(
        uniqueName: String,
        obj: Any,
    ) {
        // 业务日志方法
        // ignored
    }
}
