package com.neko233.log233.api.appender

import com.neko233.log233.api.config.structs.AppenderConfig
import com.neko233.log233.api.structs.MessageObj

abstract class AbstractAppenderApi : AppenderApi {

    private var appenderName: String = ""

    override fun getName(): String {
        return appenderName
    }

    override fun initByConfig(appenderConfig: AppenderConfig) {
        this.appenderName = appenderConfig.name
        if (appenderName.isBlank()) {
            throw IllegalArgumentException("appenderName is blank")
        }

        initYourConfig(appenderConfig.kvMap)
    }

    abstract fun initYourConfig(kvMap: Map<String, String>)

    abstract override fun appendMessage(messageObj: MessageObj)


    abstract override fun logBusiness(
        uniqueName: String,
        obj: Any
    )
}