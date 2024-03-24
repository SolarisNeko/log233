package com.neko233.log233.api.appender

import com.neko233.log233.api.config.structs.AppenderConfig
import com.neko233.log233.api.structs.MessageObj

/**
 * 追写器 API
 *
 * @author SolarisNeko
 * Date on 2024-01-01
 * */
interface AppenderApi {

    // 名字
    fun getName(): String

    /**
     * 通过配置初始化
     * @param kvMap xml 中的每一个 childNode value
     * <appender name="console" className="">
     *     <maxFileSize>100MB</maxFileSize>
     *     <useHistoryFileZip>false</useHistoryFileZip>
     * </appender>
     *
     * = mapOf(
     *  "maxFileSize" to "100MB",
     *  "useHistoryFileZip" to "false"
     * )
     */
    fun initByConfig(appenderConfig: AppenderConfig)

    /**
     * 追加新的消息
     */
    fun appendMessage(messageObj: MessageObj)


    /**
     * 业务日志
     *
     * @param uniqueName 唯一名字
     * @param obj 业务对象
     */
    fun logBusiness(
        uniqueName: String,
        obj: Any,
    )


}