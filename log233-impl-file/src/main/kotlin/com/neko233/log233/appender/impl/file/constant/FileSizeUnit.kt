package com.neko233.log233.appender.impl.file.constant

/**
 * 文件大小单位
 */
enum class FileSizeUnit(
    val bytes: Long
) {
    B(1L),              // 1字节
    KB(1024L),          // 1KB = 1024字节
    MB(1024L * 1024),   // 1MB = 1024 * 1024字节
    GB(1024L * 1024 * 1024), // 1GB = 1024 * 1024 * 1024字节
    TB(1024L * 1024 * 1024 * 1024), // 1TB = 1024 * 1024 * 1024 * 1024字节
    PB(1024L * 1024 * 1024 * 1024 * 1024), // 1PB = 1024 * 1024 * 1024 * 1024 * 1024字节
    EB(1024L * 1024 * 1024 * 1024 * 1024 * 1024); // 1EB = 1024^6字节

    companion object {
        // 根据字符串获取对应的枚举类型和大小
        fun fromString(sizeStr: String): Pair<Long, FileSizeUnit> {
            val numberPart = sizeStr.filter { it.isDigit() }
            val unitPart = sizeStr.filter { it.isLetter() }.uppercase()

            val number = numberPart.toLongOrNull() ?: throw NumberFormatException("Invalid number format: $numberPart")
            val enumValue = entries.firstOrNull { it.name == unitPart }
                ?: throw IllegalArgumentException("Unsupported file size unit: $unitPart")

            return Pair(number * enumValue.bytes, enumValue)
        }
    }
}
