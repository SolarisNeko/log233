package com.neko233.log233.api.utils

object StackTraceUtil {

    /**
     * 获取调用日志方法的堆栈信息。
     * ps: 注意执行的线程, 不能异步后调用
     *
     * @param depth 堆栈深度。默认为2，通常0是getStackTrace，1是getCallSite，2是调用日志方法的位置。
     * @return 返回堆栈信息字符串。
     */
    @JvmStatic
    fun getCallPositionStr(depth: Int = 2): String {
        // 当前线程元素
        val stackTraceElements = Thread.currentThread().stackTrace

        return if (stackTraceElements.size > depth) {
            val element = stackTraceElements[depth]
            "${element.className}.${element.methodName}:${element.lineNumber}"
        } else {
            "Stack depth is too shallow."
        }
    }

    @JvmStatic
    fun getAllStackTrace(depth: Int = 2): List<String> {
        val stackTraceElements = Thread.currentThread().stackTrace
        return stackTraceElements
            .filterIndexed { index, _ -> index >= depth }
            .map {
                "${it.className}.${it.methodName}:${it.lineNumber}"
            }
    }
}
