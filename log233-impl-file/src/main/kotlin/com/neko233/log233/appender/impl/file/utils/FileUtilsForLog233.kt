package com.neko233.log233.appender.impl.file.utils

import java.io.File

object FileUtilsForLog233 {


    @JvmStatic
    fun createFileIfNotExists(currentLogFile0: File) {
        if (!currentLogFile0.exists()) {
            currentLogFile0.parentFile.mkdirs()
            currentLogFile0.createNewFile()
        }
    }
}