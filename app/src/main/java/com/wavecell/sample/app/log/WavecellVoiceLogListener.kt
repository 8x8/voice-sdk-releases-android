package com.wavecell.sample.app.log

import com.eght.voice.sdk.Voice
import com.eght.voice.sdk.logging.VoiceLog
import com.wavecell.sample.app.BuildConfig
import com.wavecell.sample.app.extensions.toSampleAppLog
import com.wavecell.sample.app.models.SampleAppLog
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class WavecellVoiceLogListener(private val logWriter: FileLogWriter, private val consoleLogger: ConsoleLogger, coroutineDispatcher: CoroutineDispatcher) {
    private val coroutineScope = CoroutineScope(coroutineDispatcher)
    private var logCollectionJob: Job? = null

    companion object {
        private val TAG = AppTag.make(AppFeatureTag.SAMPLE_APPLICATION, AppComponentTag.LOG_DATA, WavecellVoiceLogListener::class.java)
    }
    fun startLogCollection(voice: Voice) {
        logCollectionJob?.let {
            AppLog.i(TAG, "Log data collection has already started")
        } ?: run {
            logCollectionJob = coroutineScope.launch {
                AppLog.i(TAG, "Starting collection of log data from SDK")
                voice.loggedData.collect(this@WavecellVoiceLogListener::onLog)
            }
        }
    }

    private fun onLog(log: VoiceLog) {
        val sampleLog = log.toSampleAppLog()
        /* enable for logging SDK logs on console*/
        //if(BuildConfig.DEBUG) {
            // consoleLogger.log(sampleLog.logLevel, sampleLog)
        //}
        logWriter.log(sampleLog)
    }

    fun onApplicationLog(log: SampleAppLog) {
        logWriter.log(log)
    }
}