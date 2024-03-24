# log233

# 要求

JDK 8+

## 介绍

log233 =

1. SLF4J
2. Logback
3. 

License 为 Apache-2.0

## Download

### Maven

```xml

<dependency>
    <groupId>com.neko233</groupId>
    <artifactId>log233</artifactId>
    <version>0.0.1</version>
</dependency>

```

### Gradle

```kotlin
implementation("com.neko233:log233:0.0.1")
```

## 初衷 & 痛点

# Use

Dependency

## maven

```xml
<!-- default use druid as DataSource -->
<dependency>
    <groupId>com.neko233</groupId>
    <artifactId>log233-all</artifactId>
    <version>0.0.1</version>
</dependency>
```

## gradle

```kotlin
implementation("com.neko233:log233-all:0.0.1")

```

# Config
## Resource/xml
```xml
<configuration>

    <!-- 全局替换参数 -->
    <globalArgs>
        <demo>1</demo>
        <!--        <comment>1</comment>-->
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
        <!--        <appender name="rollingFileAppender"/>-->
    </loggerHandler>

    <appender name="console" className="com.neko233.log233.api.appender.imp.AppenderApiByConsole">
    </appender>


    <!-- Rolling File Appender -->
    <appender name="rollingFileAppender" className="com.neko233.log233.appender.impl.file.AppenderApiByRollingFile">
        <logFileName>${logDir}/app.log</logFileName>

        <!--        <maxHistoryFileCount>30</maxHistoryFileCount>-->
        <!--        <maxFileSize>100MB</maxFileSize>-->
        <!--        <useHistoryFileZip>false</useHistoryFileZip>-->
    </appender>

</configuration>

```


# Code
## LoggerApi
```kotlin
import com.neko233.log233.api.Log233Manager
import com.neko233.log233.api.LoggerApiFactory
import com.neko233.log233.api.constant.Log233Constant
import kotlin.test.Test

class Log233Test {

    val logger = LoggerApiFactory.getLogger(Log233Manager::class)

    @Test
    fun test() {
        // set your config 
        System.setProperty(Log233Constant.CONFIG_FILE_ENV_KEY, "classpath:log233-default.xml");


        logger.debug("hello world. arg = {}", "1")
        logger.info("hello world. arg = {}", "1")
        logger.warn("hello world. arg = {}", null)
        logger.error("hello world. arg = {}", null, Throwable())


        Thread.sleep(1000)
    }
}
```