package com.wavecell.sample.app.extensions

import com.eght.voice.sdk.logging.VoiceLog
import com.wavecell.sample.app.models.SampleAppLog

internal fun VoiceLog.toSampleAppLog() = SampleAppLog(logClassName,
        timestamp,
        logLevel.name.toSampleAppLogLevel(),
        logThreadInfo,
        logTagInfo,
        logMessage,
        throwable)