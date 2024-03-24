package com.neko233.log233.api.utils

import com.neko233.easyxml.data.XmlObject
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.File
import java.util.*
import java.util.stream.Collectors
import javax.xml.parsers.DocumentBuilderFactory

object XmlUtils {

    /**
     * 获取单个子节点
     */
    fun getOneChildNodeByName(
        xmlObj: XmlObject,
        childNodeName: String
    ): XmlObject? {
        return xmlObj.childrenNodes
            .stream()
            .filter {
                Objects.equals(it.nodeName, childNodeName)
            }
            .findFirst()
            .orElse(null)
    }

    /**
     * 获取所有子节点
     */
    fun getAllChildNodeByName(
        xmlObj: XmlObject,
        childNodeName: String
    ): List<XmlObject> {
        return xmlObj.childrenNodes
            .stream()
            .filter {
                Objects.equals(it.nodeName, childNodeName)
            }
            .collect(Collectors.toList())
    }


    /**
     * 读取全局参数并替换文件中所有的占位符
     */
    fun replaceXmlPlaceholders(
        xmlFilePath: String,
        globalArgRootNodeName: String = "globalArgs"
    ): String {
        // 加载和解析XML文件
        val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val document = docBuilder.parse(File(xmlFilePath))
        val root = document.documentElement

        // 提取<globalArgs>中的键值对
        val globalArgs = mutableMapOf<String, String>()
        val globalArgsNodes = (root.getElementsByTagName(globalArgRootNodeName).item(0) as Element).childNodes
        for (i in 0 until globalArgsNodes.length) {
            val node = globalArgsNodes.item(i)
            if (node.nodeType == Node.ELEMENT_NODE) {
                val element = node as Element
                globalArgs[element.tagName] = element.textContent
            }
        }

        // 将整个XML文件转换为字符串
        val xmlContent = File(xmlFilePath).readText()

        // 替换占位符
        val replacedContent = globalArgs.entries
            .fold(xmlContent) { content, entry ->
                content.replace("\${${entry.key}}", entry.value)
            }

        // 输出或保存结果
        return replacedContent
    }
}