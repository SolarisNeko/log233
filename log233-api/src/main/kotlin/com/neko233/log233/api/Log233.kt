package com.neko233.log233.api

import com.neko233.log233.api.constant.Log233Constant
import java.io.File

/**
 * 日志工厂
 *
 * @author SolarisNeko
 * Date on 2024-01-01
 * */
object Log233 {

    /**
     * 设置 logger 配置
     */
    @JvmStatic
    fun <T : Any> setLoggerConfig(
        resourceFilePath: String = "log233.xml",
        fromResourcesFlag: Boolean = true
    ) {
        val absolutePath: String = if (fromResourcesFlag) {
            val resourceUrl = Log233::class.java.classLoader.getResource(resourceFilePath)!!
            val resourceFile = File(resourceUrl.toURI())
            resourceFile.absolutePath
        } else {
            resourceFilePath
        }

        println("[Log233] current config path = $absolutePath")
        System.setProperty(Log233Constant.CONFIG_FILE_ENV_KEY, absolutePath)
    }


}