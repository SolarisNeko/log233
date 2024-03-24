package com.neko233.log233.appender.impl.file.utils

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object FileZipUtils {

    /**
     * 压缩单个文件或目录到ZIP文件
     *
     * @param sourceFilePath 源文件或目录的路径
     * @param zipFilePath    压缩文件的输出路径
     */
    @JvmStatic
    fun zipFile(
        sourceFilePath: String,
        zipFilePath: String
    ) {
        val sourceFile = File(sourceFilePath)
        ZipOutputStream(FileOutputStream(zipFilePath)).use { zipOut ->
            compressFile(sourceFile, zipOut, sourceFile.name)
        }
    }

    /**
     * 递归压缩文件或目录
     *
     * @param file   要压缩的文件或目录
     * @param zipOut ZIP输出流
     * @param name   在ZIP文件中的条目名称
     */
    private fun compressFile(
        file: File,
        zipOut: ZipOutputStream,
        name: String
    ) {
        if (file.isHidden) return  // 忽略隐藏文件或目录

        if (file.isDirectory) {
            // 如果是目录，则递归压缩目录下的文件和子目录
            file.listFiles()?.forEach { childFile ->
                compressFile(childFile, zipOut, "$name/${childFile.name}")
            }
        } else {
            // 如果是文件，则直接压缩
            FileInputStream(file).use { fis ->
                val zipEntry = ZipEntry(name)
                zipOut.putNextEntry(zipEntry)
                fis.copyTo(zipOut, 1024)
                zipOut.closeEntry()
            }
        }
    }
}
