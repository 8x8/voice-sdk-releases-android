package com.wavecell.sample.app.utils

import com.wavecell.sample.app.log.FileLogWriter
import com.wavecell.sample.app.models.SampleAppLog
import com.wavecell.sample.app.models.SampleAppLogLevel

class UncaughtExceptionHandler(private val logWriter: FileLogWriter): Thread.UncaughtExceptionHandler {

    override fun uncaughtException(t: Thread, e: Throwable) {
        val className = this.javaClass.simpleName
        val timestamp = System.currentTimeMillis()
        val logLevel = SampleAppLogLevel.ERROR
        val threadInfo = Thread.currentThread().name
        val tagInfo = "Sample App Uncaught Exception"
        val message = e.localizedMessage ?: "An error has occurred"
        logWriter.log(SampleAppLog(className, timestamp, logLevel, threadInfo, tagInfo, message, e))
    }
}