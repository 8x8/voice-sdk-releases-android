package com.wavecell.sample.app.log

import android.content.Context
import com.wavecell.sample.app.log.FileLogWriter.Companion.ARCHIVED_PATTERN
import com.wavecell.sample.app.log.FileLogWriter.Companion.LOG_FILENAME
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*
import java.util.zip.ZipInputStream


object FileLoggingExtractor {

    @Throws(IOException::class)
    fun extractLogsToFileStream(context: Context, destinationFileStream: OutputStream) {
        //write all archives first

        val logsDirectory = File(context.cacheDir, "logs")
        val currentLogFile = File(logsDirectory, LOG_FILENAME)
        currentLogFile.parentFile?.listFiles(StartsWithFilenameFilter(ARCHIVED_PATTERN))?.let { logFiles ->
            Arrays.sort(logFiles) //sorting lexicographically. A file towards the end of the array means is more recent
            for (file in logFiles) {
                FileInputStream(file).use { fileInputStream ->
                    ZipInputStream(fileInputStream).use { zipStream ->
                        zipStream.nextEntry
                        IOUtils.copy(zipStream, destinationFileStream)
                    }
                }
                //NOP
            }
        }
        FileInputStream(currentLogFile).use {
            currentLogFileStream -> IOUtils.copy(currentLogFileStream, destinationFileStream)
        }
    }
}