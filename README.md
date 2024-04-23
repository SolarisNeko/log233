# log233


English Doc [English Doc](./README-en)

中文文档 [中文文档](./README)

## 要求

- JDK 8+

## 介绍

log233 是一个高性能的日志框架，结合了以下特点：

1. 类似 SLF4J 日志门面标准，提供灵活的日志管理。
2. 类似 Logback 功能，优化日志的异步处理与文件滚动。
3. 提供协程支持，增强并发日志写入性能。

License 为 Apache-2.0。

## 下载

### Maven

```xml
<dependency>
    <groupId>com.neko233</groupId>
    <artifactId>log233</artifactId>
    <version>0.0.3</version>
</dependency>
```

### Gradle

```kotlin
implementation("com.neko233:log233:0.0.3")
```

## 初衷 & 痛点

log233 旨在解决传统日志系统在高并发环境下的性能瓶颈和可扩展性限制。

通过类似 SLF4J 和 Logback 的相似, 降低使用者的学习成本.

并提供了除了传统 SLF4J 日志以外, 【统一业务日志】API, 应用于业务级

并利用 Kotlin 的协程特性， log233 能够提供更高效的日志处理机制，减少日志写入对应用性能的影响。

## 使用说明

### 依赖

#### Maven

```xml
<!-- 默认使用 druid 作为数据源 -->
<dependency>
    <groupId>com.neko233</groupId>
    <artifactId>log233-all</artifactId>
    <version>0.0.3</version>
</dependency>
```

#### Gradle

```kotlin
implementation("com.neko233:log233-all:0.0.3")
```

## 配置

### 资源/xml

在资源文件中，可以配置全局参数、日志类、处理器和附加器。以下是一个配置示例：

```xml
<configuration>

    <!-- 全局替换参数 -->
    <globalArgs>
        <demo>1</demo>
        <!-- <comment>1</comment> -->
        <logDir>../logs</logDir>
    </globalArgs>

    <!-- 日志类 -->
    <loggerClassName>com.neko233.log233.api.logger.LoggerApiByCoroutine</loggerClassName>

    <!-- root logger | 影响全部 -->
    <loggerHandlerRoot level="INFO">
        <appender name="console"/>
        <appender name="rollingFileAppender"/>
    </loggerHandlerRoot>

    <loggerHandler name="demo" level="INFO">
        <appender name="console"/>
        <!-- <appender name="rollingFileAppender"/> -->
    </loggerHandler>

    <appender name="console" className="com.neko233.log233.api.appender.imp.AppenderApiByConsole">
    </appender>

    <!-- Rolling File Appender -->
    <appender name="rollingFileAppender" className="com.neko233.log233.appender.impl.file.AppenderApiByRollingFile">
        <logFileName>${logDir}/app-{dateTime}-{count}.log</logFileName>
         <dateTimeFormat>yyyy-MM-dd</dateTimeFormat> 
         <maxHistoryFileCount>30</maxHistoryFileCount> 
         <maxFileSize>100MB</maxFileSize> 
        <!-- <useHistoryFileZip>false</useHistoryFileZip> -->
    </appender>

</configuration>
```

## 代码示例
### 自定义配置文件
```kotlin
// 设置为 resource/log233-dev.xml 为配置文件
Log233.setLoggerConfig("log233-dev.xml", true)

```

### LoggerApi

使用 log233 进行日志记录的示例代码如下：

```kotlin
import com.neko233.log233.api.Log233Manager
import com.neko233.log233.api.LoggerApiFactory
import com.neko233.log233.api.constant.Log233Constant
import kotlin.test.Test

class Log233Test {

    val logger = LoggerApiFactory.getLogger(Log233Manager::class)

    @Test
    fun test() {
        // 设置配置文件
        System.setProperty(Log233Constant.CONFIG_FILE_ENV_KEY, "classpath:log233-default.xml");

        logger.debug("hello world. arg = {}", "1")
        logger.info("hello world. arg = {}", "1")
        logger.warn("hello world. arg = {}", null)
        logger.error("hello world. arg = {}", null, Throwable())

        Thread.sleep(1000)
    }
}
```

这段代码展示了如何初始化 log233 日志器并进行各级别的日志记录。

以上是对 log233 文档的完善。如果有特定的部分需要进一步细化或解释，请告知。

# 扩展
## 推荐的占位符格式
不推荐使用 ${key}, 因为全局 log233 参数已经占用了这个替换符
推荐
```text
<yourKey>/path/to/your-file-{dateTimeFormat}-{demo}
```

## 自定义你的 Appender

```kotlin

import com.neko233.log233.api.appender.AbstractAppenderApi
import com.neko233.log233.api.structs.MessageObj
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat

class AppenderApiByDiy : AbstractAppenderApi() {

    override fun initYourConfig(kvMap: Map<String, String>) {
        // todo init your appender 通过自定义参数
    }

    override fun appendMessage(messageObj: MessageObj) {
        val dateTimeStr = dateFormatter.format(messageObj.createTimeMs)

        val message = messageObj.message
        val threadName = messageObj.threadName
        val level = messageObj.level.name
        val callPositionStr = messageObj.callPositionStr
        val throwable: Throwable? = messageObj.throwable

        // todo your log 

    }


    override fun logBusiness(
        uniqueName: String,
        obj: Any
    ) {
        // nothing
    }
}
```
