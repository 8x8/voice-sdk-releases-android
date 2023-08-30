package com.wavecell.sample.app.models

data class SampleAppLog(val logClassName: String,
                       val timestamp: Long,
                       val logLevel: SampleAppLogLevel,
                       val logThreadInfo: String?,
                       val logTagInfo: String,
                       val logMessage: String,
                       val throwable: Throwable? = null) {
}