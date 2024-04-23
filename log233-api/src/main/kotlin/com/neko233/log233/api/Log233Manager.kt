package com.neko233.log233.api

import com.neko233.easyxml.XML
import com.neko233.easyxml.data.XmlObject
import com.neko233.log233.api.appender.AppenderApi
import com.neko233.log233.api.config.structs.AppenderConfig
import com.neko233.log233.api.config.structs.LoggerConfig
import com.neko233.log233.api.constant.Log233Constant
import com.neko233.log233.api.handler.LoggerHandler
import com.neko233.log233.api.logger.LoggerApiByCoroutine
import com.neko233.log233.api.utils.XmlUtils
import java.io.File
import java.net.URI
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.function.Function
import java.util.stream.Collectors


/**
 * 日志管理器
 *
 * @author SolarisNeko
 * Date on 2024-01-01
 * */
class Log233Manager private constructor() {

    // 是否初始化
    private val isInit = AtomicBoolean(false)

    // 日志记录器 className
    private lateinit var loggerClassName: String

    // logger root 配置
    private lateinit var loggerHandlerRootConfig: LoggerConfig

    // logger extra 配置
    private lateinit var loggerHandlerRoot: LoggerHandler

    // 所有日志器配置 <loggerName, Logger>
    private val loggerNameToConfigMap = HashMap<String, LoggerConfig>()
    private var loggerNameToHandlerMap: MutableMap<String, LoggerHandler> = HashMap()

    // 所有追写器配置 <appenderName, Appender>
    private val appenderNameToConfigMap = HashMap<String, AppenderConfig>()
    private var appenderNameToHandlerMap: MutableMap<String, AppenderApi> = HashMap()


    companion object {
        @JvmStatic
        val instance by lazy {
            Log233Manager()
        }
    }

    fun isInit(): Boolean {
        return isInit.get()
    }

    fun isNotInit(): Boolean {
        return !isInit()
    }

    @Synchronized
    fun init() {
        if (!isInit.compareAndSet(false, true)) {
            return
        }

        val configFilePath =
            System.getProperty(Log233Constant.CONFIG_FILE_ENV_KEY, Log233Constant.CONFIG_FILE_DEFAULT_PATH)

        val defaultFlag = configFilePath == Log233Constant.CONFIG_FILE_DEFAULT_PATH
        val isInClassPath = configFilePath.startsWith("classpath:")

        // 读取 resources/
        val xmlUri: URI? = if (isInClassPath) {
            // 从类路径中加载配置文件
            val resourcePath = configFilePath.substring("classpath:".length)
            Log233Manager::class.java.classLoader.getResource(resourcePath)?.toURI()
        } else {
            // 处理为普通文件路径
            File(configFilePath).toURI()
        }

        if (xmlUri == null) {
            throw IllegalArgumentException("Configuration file not found: $configFilePath")
        }


        println("[Log233] current use config path = ${xmlUri.path}")

// 这里需要做特殊处理, 可能是在 jar 中的文件
        val xmlPath = if (defaultFlag) {
            // 从类路径中加载的资源
            val resourcePath = configFilePath.substring("classpath:".length)
            val inputStream = Log233Manager::class.java.classLoader.getResourceAsStream(resourcePath)
                ?: throw IllegalArgumentException("Configuration file not found in classpath: $resourcePath")

            // 创建临时文件并写入资源内容, 快照配置
            val tempFile = File.createTempFile("temp", ".xml")
            tempFile.deleteOnExit()
            tempFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            tempFile.absolutePath
        } else {
            // 从文件系统中加载的资源
            Paths.get(xmlUri)
                .toAbsolutePath()
                .toString()
        }


        // 获取全局替换参数后的 XML 内容
        val xml = XmlUtils.getXmlContentWithReplaceGlobalArgs(xmlPath, "globalArgs")

        val xmlObject = XML.parseToObject(xml)

        var loggerHandlerRoot: XmlObject? = null
        val loggerNodeList = LinkedList<XmlObject>()
        val appenderNodeList = LinkedList<XmlObject>()

        for (child in xmlObject.children) {
            // loggerClassName
            if (child.nodeName.equals("loggerClassName", true)) {
                this.loggerClassName = child.nodeValue
                continue
            }
            // logger root
            if (child.nodeName.equals("loggerHandlerRoot", true)) {
                loggerHandlerRoot = child
                continue
            }
            // logger
            if (child.nodeName.equals("loggerHandler", true)) {
                loggerNodeList.add(child)
                continue
            }
            // appender
            if (child.nodeName.equals("appender", true)) {
                appenderNodeList.add(child)
                continue
            }
        }

        // logger config
        this.loggerHandlerRootConfig = LoggerConfig.fromRoot(loggerHandlerRoot!!)
        val loggerNameMap = loggerNodeList.stream()
            .map {
                LoggerConfig.fromHandler(it)
            }
            .collect(Collectors.toMap({ it.name }, Function.identity()))
        loggerNameToConfigMap.putAll(loggerNameMap)

        // appender config
        val appenderNameMap = appenderNodeList.stream()
            .map {
                AppenderConfig.from(it)
            }
            .collect(Collectors.toMap({ it.name }, Function.identity()))
        appenderNameToConfigMap.putAll(appenderNameMap)

        initAppender()
        initLogger()
    }

    private fun initLogger() {
        this.loggerNameToHandlerMap = this.loggerNameToConfigMap.values
            .stream()
            .map {
                val newInstance = LoggerHandler.from(it, appenderNameToHandlerMap)
                newInstance
            }
            .collect(Collectors.toMap({ it.loggerNamePrefixMatcher }, { it }))

        this.loggerHandlerRoot = LoggerHandler.from(this.loggerHandlerRootConfig, appenderNameToHandlerMap)
    }

    private fun initAppender() {
        this.appenderNameToHandlerMap = this.appenderNameToConfigMap.values
            .stream()
            .map {
                val className = it.className
                val newInstance = try {
                    Class.forName(className).getConstructor().newInstance()
                } catch (e: Exception) {
                    throw RuntimeException(
                        "[Log233] appender className can not create obj. className = ${className}",
                        e
                    )
                }
                if (newInstance !is AppenderApi) {
                    throw RuntimeException("[Log233] appender className must implements interface ${AppenderApi::class.java.simpleName}. className = ${className}")
                }
                val appenderApi = newInstance as AppenderApi
                appenderApi.initByConfig(it)
                appenderApi
            }
            .collect(Collectors.toMap({ it.getName() }, { it }))
    }

    fun getloggerHandlerRootConfig(): LoggerConfig {
        return loggerHandlerRootConfig
    }

    fun getAllLoggerHandlerConfig(): List<LoggerConfig> {
        return ArrayList(loggerNameToConfigMap.values)
    }


    fun getAllAppenderConfig(): List<AppenderConfig> {
        return ArrayList(appenderNameToConfigMap.values)
    }

    fun getLoggerHandlerRoot(): LoggerHandler {
        return loggerHandlerRoot
    }

    fun getLoggerHandlerWithoutRoot(): MutableMap<String, LoggerHandler> {
        return loggerNameToHandlerMap
    }

    /**
     * 创建日志
     */
    fun createLoggerOrDefault(
        loggerName: String,
        defaultLoggerCreator: () -> LoggerApi
    ): LoggerApi {
        val className = loggerClassName
        try {
            val clazz = Class.forName(className)
            val constructor = clazz.getDeclaredConstructor(String::class.java)
            constructor.isAccessible = true
            return constructor.newInstance(loggerName) as LoggerApi
        } catch (e: Exception) {
            val seeLoggerName = LoggerApiByCoroutine::class.java.name
            println("[log233] error create logger by your diy className = ${className}. you need a loggerName: String constructor! @see ${seeLoggerName}")
            e.printStackTrace()
            return defaultLoggerCreator.invoke()
        }
    }
}