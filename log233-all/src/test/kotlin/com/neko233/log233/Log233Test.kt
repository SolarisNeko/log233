package com.neko233.log233

import com.neko233.log233.api.Log233Manager
import com.neko233.log233.api.LoggerApiFactory
import com.neko233.log233.api.constant.Log233Constant
import kotlin.test.Test

class Log233Test {


    @Test
    fun test() {
        // set your config
        System.setProperty(Log233Constant.CONFIG_FILE_ENV_KEY, "classpath:log233-default.xml");


        val logger = LoggerApiFactory.getLogger(Log233Manager::class)

        logger.debug("hello world. arg = {}", "1")
        logger.info("hello world. arg = {}", "1")
        logger.warn("hello world. arg = {}", null)
        logger.error("hello world. arg = {}", null, Throwable())


        Thread.sleep(1000)
    }
}