package com.neko233.log233.api.config.structs

import com.neko233.easyxml.data.XmlObject
import java.util.stream.Collectors

/**
 * 追写器配置
 *
 * @author SolarisNeko
 * Date on 2024-01-01
 * */
class AppenderConfig {

    // 名字
    var name: String = ""

    // 反射用的类名
    var className: String = ""

    // 配置
    var kvMap: Map<String, String> = HashMap()


    companion object {
        @JvmStatic
        fun from(xmlObj: XmlObject): AppenderConfig {
            return AppenderConfig().apply {

                this.name = (xmlObj.getAttribute("name") ?: "").trim()
                if (name.isBlank()) {
                    throw IllegalArgumentException("appender.name is blank. appender name = ${name}")
                }

                this.className = (xmlObj.getAttribute("className") ?: "").trim()
                if (className.isBlank()) {
                    throw IllegalArgumentException("className is blank. appender name = ${name}")
                }

                val children = xmlObj.children
                this.kvMap = children
                    .stream()
                    .collect(Collectors.toMap({ it.nodeName }, { it.nodeValue }, {v1,v2 -> v2}))
            }
        }
    }


}