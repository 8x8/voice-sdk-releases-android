package com.wavecell.sample.app.log

import android.util.Log
import com.wavecell.sample.app.BuildConfig
import com.wavecell.sample.app.models.SampleAppLog
import com.wavecell.sample.app.models.SampleAppLogLevel

object AppLog {
        var wavecellVoiceLogListener : WavecellVoiceLogListener?=  null
        val consoleLogger: ConsoleLogger by lazy { ConsoleLogger() }

        fun e(tag: AppTag, message: String) {
            log(tag, SampleAppLogLevel.ERROR, message)
        }

        fun e(tag: AppTag, message: String, throwable: Throwable?) {
            log(tag, SampleAppLogLevel.ERROR, message, throwable)
        }

        fun w(tag: AppTag, message: String) {
            log(tag, SampleAppLogLevel.WARNING, message)
        }

        fun w(tag: AppTag, message: String, throwable: Throwable?) {
            log(tag, SampleAppLogLevel.WARNING, message, throwable)
        }

        fun i(tag: AppTag, message: String) {
            log(tag, SampleAppLogLevel.INFO, message)
        }

        fun d(tag: AppTag, message: String) {
            log(tag, SampleAppLogLevel.DEBUG, message)
        }

        fun d(tag: AppTag, message: String, throwable: Throwable?) {
            log(tag, SampleAppLogLevel.DEBUG, message, throwable)
        }

        fun v(tag: AppTag, message: String, throwable: Throwable?) {
            log(tag, SampleAppLogLevel.VERBOSE, message, throwable)
        }

        fun v(tag: AppTag, message: String) {
            log(tag, SampleAppLogLevel.VERBOSE, message)
        }


        private fun log(
            tag: AppTag,
            logLevel: SampleAppLogLevel,
            message: String,
            throwable: Throwable? = null
        ) {
            val timestamp = System.currentTimeMillis()
            val logLevelInfo: SampleAppLogLevel = logLevel
            val logTagInfo: String = generateLogTagInfo(tag, logLevel)
            val threadInfo: String = generateThreadInfo()
            val className: String = tag.className
            val logEntryData = SampleAppLog(
                className,
                timestamp,
                logLevelInfo,
                threadInfo,
                logTagInfo,
                message,
                throwable
            )

            writeLogsToConsole(logLevel, logEntryData)
            writeLogsToFile(logEntryData)

        }

    private fun writeLogsToFile(logEntryData: SampleAppLog) {
        wavecellVoiceLogListener?.let {
            wavecellVoiceLogListener?.onApplicationLog(logEntryData)
        } ?: run {
            Log.e(TAG, "Unable to put application log to e ")
        }
    }

    private fun writeLogsToConsole(logLevel: SampleAppLogLevel, logEntryData: SampleAppLog) {
        if (BuildConfig.DEBUG) {
            consoleLogger.log(logLevel, logEntryData)
        }
    }

    private fun generateLogTagInfo(tag: AppTag, logLevel: SampleAppLogLevel): String {
            var logTag: String = tag.featureTag
            if (tag.componentTag != null) {
                logTag += "." + tag.componentTag
            }
            if (logLevel == SampleAppLogLevel.DEBUG || logLevel == SampleAppLogLevel.VERBOSE) {
                logTag += "." + tag.className
            }
            return logTag
        }

        private fun generateThreadInfo() = "[" + Thread.currentThread().name + "] "

        private val TAG = AppTag::class.java.simpleName
}