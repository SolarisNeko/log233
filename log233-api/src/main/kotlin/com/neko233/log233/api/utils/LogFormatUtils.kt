package com.neko233.log233.api.utils

object LogFormatUtils {

    /**
     * 格式化日志消息，替换占位符{}为实际的参数值。
     * args 可以比 {} 数量多一个, 多出来的一个存放 Throwable
     *
     * @param messageTemplate 日志消息模板，包含占位符{}。
     * @param args 参数列表，将替换模板中的占位符。
     * @return 格式化后的日志消息。
     */
    @JvmStatic
    fun formatLogArgs(
        messageTemplate: String,
        args: Array<out Any?>
    ): Pair<String, Throwable?> {
        var throwable: Throwable? = null
        val stringBuilder = StringBuilder()
        var argIndex = 0
        var lastIndex = 0
        var index = messageTemplate.indexOf("{}")

        while (index >= 0 && argIndex < args.size) {
            stringBuilder.append(messageTemplate, lastIndex, index)

            // 替换
            stringBuilder.append(args[argIndex]?.toString() ?: "null")

            // Skip past the {}
            lastIndex = index + 2
            index = messageTemplate.indexOf("{}", lastIndex)
            argIndex++
        }

        if (lastIndex < messageTemplate.length) {
            stringBuilder.append(messageTemplate.substring(lastIndex))
        }

        // 超出 {} 数量的, 最后一个参数作为 Throwable 丢入
        if ((argIndex + 1) <= args.size) {
            val obj = args[argIndex]
            if (obj != null) {
                if (obj is Throwable) {
                    throwable = obj
                }
            }
        }

        val message = stringBuilder.toString()
        return Pair(message, throwable)
    }
}
