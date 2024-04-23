
# log233

English Doc [English Doc](./README-en)

中文文档 [中文文档](./README)

## Requirements

- JDK 8 or higher

## Introduction

log233 is a high-performance logging framework that incorporates the following features:

1. Similar to the SLF4J logging facade standard, it provides flexible log management.
2. Comparable to Logback in functionality, optimizing asynchronous logging and file rolling.
3. Supports coroutines, enhancing concurrent log writing performance.

The license is Apache-2.0.

## Download

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

## Motivation & Pain Points

log233 is designed to address the performance bottlenecks and scalability limitations of traditional logging systems in high-concurrency environments.

By emulating SLF4J and Logback, it reduces the learning curve for users.

Besides the traditional SLF4J logging, log233 provides a "unified business log" API for business-level logging.

Utilizing Kotlin's coroutine features, log233 offers a more efficient logging mechanism, reducing the impact of log writing on application performance.

## Usage Instructions

### Dependencies

#### Maven

```xml
<!-- Defaults to using Druid as the data source -->
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

## Configuration

### Resources/xml

In the resource file, global parameters, log classes, handlers, and appenders can be configured. Here is a configuration example:

```xml
<configuration>

    <!-- Global replacement parameters -->
    <globalArgs>
        <demo>1</demo>
        <logDir>../logs</logDir>
    </globalArgs>

    <!-- Logger class -->
    <loggerClassName>com.neko233.log233.api.logger.LoggerApiByCoroutine</loggerClassName>

    <!-- Root logger configuration, affecting all logs -->
    <loggerHandlerRoot level="INFO">
        <appender name="console"/>
        <appender name="rollingFileAppender"/>
    </loggerHandlerRoot>

    <loggerHandler name="demo" level="INFO">
        <appender name="console"/>
    </loggerHandler>

    <appender name="console" className="com.neko233.log233.api.appender.imp.AppenderApiByConsole">
    </appender>

    <!-- Rolling File Appender -->
    <appender name="rollingFileAppender" className="com.neko233.log233.appender.impl.file.AppenderApiByRollingFile">
        <logFileName>${logDir}/app-{dateTime}-{count}.log</logFileName>
        <dateTimeFormat>yyyy-MM-dd</dateTimeFormat> 
        <maxHistoryFileCount>30</maxHistoryFileCount> 
        <maxFileSize>100MB</maxFileSize> 
    </appender>

</configuration>
```

## Code Example

### diy your config
```kotlin
// config file =  resource/log233-dev.xml 
Log233.setLoggerConfig("log233-dev.xml", true)

```

### LoggerApi

Here is an example code for logging with log233:

```kotlin
import com.neko233.log233.api.Log233Manager
import com.neko233.log233.api.LoggerApiFactory
import com.neko233.log233.api.constant.Log233Constant
import kotlin.test.Test

class Log233Test {

    val logger = LoggerApiFactory.getLogger(Log233Manager::class)

    @Test
    fun test() {
        // Set the configuration file
        System.setProperty(Log233Constant.CONFIG_FILE_ENV_KEY, "classpath:log233-default.xml");

        logger.debug("hello world. arg = {}", "1")
        logger.info("hello world. arg = {}", "1")
        logger.warn("hello world. arg = {}", null)
        logger.error("hello world. arg = {}", null, Throwable())

        Thread.sleep(1000)
    }
}
```

This code demonstrates how to initialize the log233 logger and perform logging at various levels.

The detailed documentation of log233 is now enhanced. For the Chinese version of this document, please refer to `./README-cn.md`.

This is intended for use in a `README.md` file on GitHub. If any specific parts need further elaboration or explanation, please let me know.


# Extensions
## Recommended Placeholder Format

Using `${key}` is not recommended as it is already used by global log233 parameters. Instead, it is advised to use the following format:

```text
<yourKey>/path/to/your-file-{dateTimeFormat}-{demo}
```

This format avoids conflicts with the internal substitution mechanism of log233 and ensures that custom parameters are distinct and easily identifiable.

## Customizing Your Appender

To extend log233 with a custom appender, you can inherit from `AbstractAppenderApi` and implement its methods. Here's an example in Kotlin:

```kotlin
import com.neko233.log233.api.appender.AbstractAppenderApi
import com.neko233.log233.api.structs.MessageObj
import java.text.SimpleDateFormat

class AppenderApiByDiy : AbstractAppenderApi() {

    override fun initYourConfig(kvMap: Map<String, String>) {
        // Initialize your appender with custom parameters
    }

    override fun appendMessage(messageObj: MessageObj) {
        // Format the date and time of the log entry
        val dateTimeStr = dateFormatter.format(messageObj.createTimeMs)

        // Extract log message details
        val message = messageObj.message
        val threadName = messageObj.threadName
        val level = messageObj.level.name
        val callPositionStr = messageObj.callPositionStr
        val throwable: Throwable? = messageObj.throwable

        // Implement your logging logic here
        // For example, log to a file, database, or external service
    }

    override fun logBusiness(
        uniqueName: String,
        obj: Any
    ) {
        // Implement business-specific logging if needed
    }
}
```

In this example, `initYourConfig` is where you can initialize the appender with custom settings, and `appendMessage` is where you define how the log message is processed and stored. The `logBusiness` method can be used for business-specific logging, if needed. This framework allows for flexible log handling, catering to the specific needs of your application.