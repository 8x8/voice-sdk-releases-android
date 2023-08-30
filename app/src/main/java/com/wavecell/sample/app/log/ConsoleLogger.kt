package com.wavecell.sample.app.log

import android.util.Log
import com.wavecell.sample.app.models.SampleAppLog
import com.wavecell.sample.app.models.SampleAppLogLevel

class ConsoleLogger {
    fun log(logLevel: SampleAppLogLevel, logData: SampleAppLog) {
        var safeTag: String = logData.logTagInfo
        if (safeTag.length > MAX_LOGCAT_TAG_LENGTH) {
            safeTag = safeTag.substring(0, MAX_LOGCAT_TAG_LENGTH)
        }
        val logMessage = getLogMessage(logData)
        var throwable: Throwable? = null
        if (logData.throwable!= null) {
            throwable = logData.throwable
        }
        when (logLevel) {
            SampleAppLogLevel.ERROR -> if (throwable == null) {
                Log.e(safeTag, logMessage)
            } else {
                Log.e(safeTag, logMessage, throwable)
            }

            SampleAppLogLevel.WARNING -> if (throwable == null) {
                Log.w(safeTag, logMessage)
            } else {
                Log.w(safeTag, logMessage, throwable)
            }

            SampleAppLogLevel.INFO -> if (throwable == null) {
                Log.i(safeTag, logMessage)
            } else {
                Log.i(safeTag, logMessage, throwable)
            }

            SampleAppLogLevel.DEBUG -> if (throwable == null) {
                Log.d(safeTag, logMessage)
            } else {
                Log.d(safeTag, logMessage, throwable)
            }

            SampleAppLogLevel.VERBOSE -> if (throwable == null) {
                Log.v(safeTag, logMessage)
            } else {
                Log.v(safeTag, logMessage, throwable)
            }

            else -> if (throwable == null) {
                Log.v(safeTag, logMessage)
            } else {
                Log.v(safeTag, logMessage, throwable)
            }
        }
    }

    private fun getLogMessage(logEntryData: SampleAppLog): String {
        return logEntryData.logClassName + logEntryData.logThreadInfo + logEntryData.logMessage
    }

    companion object {
        private const val MAX_LOGCAT_TAG_LENGTH = 23
    }
}