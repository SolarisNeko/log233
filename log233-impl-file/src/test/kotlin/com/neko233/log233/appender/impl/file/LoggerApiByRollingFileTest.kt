//package com.neko233.log233.appender.impl.file
//
//import com.neko233.log233.api.structs.MessageObj
//
//
//import org.mockito.Mockito
//import org.mockito.Mockito.`when`
//import java.io.File
//import java.nio.file.Files
//import kotlin.test.BeforeTest
//import kotlin.test.Test
//
//class LoggerApiByRollingFileTest {
//
//    private lateinit var loggerApi: LoggerApiByRollingFile
//    private lateinit var logDirectory: File
//    private val maxSizePerFileInBytes = 1024L // 1KB for testing
//
//    @BeforeTest
//    fun setUp() {
//        loggerApi = LoggerApiByRollingFile()
//
//        // Set up a temporary directory for logging
//        logDirectory = Files.createTempDirectory("logTest").toFile()
//        loggerApi.initYourConfig(
//            mapOf(
//                "logFileName" to "${logDirectory.path}/testLog_dateTime_count.log",
//                "sameFileMaxCount" to "3",
//                "maxSizePerFile" to "1KB"
//            )
//        )
//    }
//
//    @Test
//    fun testLogRolling() {
//        // Assuming the log message size is 512 bytes
//        val logMessage = "Test Message".padEnd(512, ' ')
//        val messageObj = Mockito.mock(MessageObj::class.java)
//        `when`(messageObj.toString()).thenReturn(logMessage)
//
//        // Write logs until rolling occurs
//        repeat((maxSizePerFileInBytes / logMessage.toByteArray().size * 3).toInt()) {
//            loggerApi.appendMessage(messageObj)
//        }
//
//        // Check if more than one log file has been created, indicating rolling has occurred
//        val logFiles = logDirectory.listFiles { _, name -> name.startsWith("testLog") }
//        assert(logFiles != null && logFiles.size > 1) { "Log rolling did not occur as expected." }
//
//        // Cleanup after test
//        logDirectory.deleteRecursively()
//    }
//}
