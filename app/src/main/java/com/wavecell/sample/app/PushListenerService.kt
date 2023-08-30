package com.wavecell.sample.app

import com.eght.voice.sdk.Voice
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.wavecell.sample.app.constants.Constants
import com.wavecell.sample.app.injection.components.SampleAppComponent
import com.wavecell.sample.app.log.AppComponentTag
import com.wavecell.sample.app.log.AppFeatureTag
import com.wavecell.sample.app.log.AppLog
import com.wavecell.sample.app.log.AppTag
import com.wavecell.sample.app.log.WavecellVoiceLogListener
import com.wavecell.sample.app.presentation.model.factory.TokenRefreshHandler
import com.wavecell.sample.app.presentation.notifications.AppCallHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

class PushListenerService : FirebaseMessagingService() {

    @Inject
    lateinit var voice: Voice

    @Inject
    lateinit var appCallHandler: AppCallHandler

    @Inject
    lateinit var voiceLogListener: WavecellVoiceLogListener

    @Inject
    lateinit var tokenRefreshHandler: TokenRefreshHandler

    @Inject
    lateinit var pushTokenRefreshHandler: PushTokenRefreshHandler

    private val coroutineScope = CoroutineScope(Dispatchers.Default) + SupervisorJob()
    private var callReportJob: Job? = null
    private var callMetricJob: Job? = null

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val data = message.data
        AppLog.d(TAG, "(onMessageReceived) notification message: $data")
        if (voice.isVoiceNotification(data)) {
            appCallHandler.handle(data)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        AppLog.d(TAG, "device push token changed to: $token")
        pushTokenRefreshHandler.onPushTokenChanged(token)
    }

    override fun onCreate() {
        inject()
        super.onCreate()

        initVoiceSDK()
    }

    override fun onDestroy() {
        super.onDestroy()

        cancelCollectionJobs()
    }

    private fun inject() {
        WavecellApplication.componentHolder.getComponent(SampleAppComponent::class.java, this)
        .inject(this)
    }

    private fun initVoiceSDK() {
        voice.init(application)
        tokenRefreshHandler.startTokenRefreshEventCollection(voice)
        voiceLogListener.startLogCollection(voice)

        launchCollectionJobs()
    }

    private fun launchCollectionJobs() {
        callReportJob?.cancel()
        callMetricJob?.cancel()

        callReportJob = coroutineScope.launch {
            voice.voiceCallReport.collect { report ->
                AppLog.d(TAG, "${Constants.LOG_PREFIX} [report] call report received: $report")
            }
        }
        callMetricJob = coroutineScope.launch {
            voice.callMetricEvents.collect { event ->
                AppLog.d(TAG, "${Constants.LOG_PREFIX} [metric] event received: $event")
            }
        }
    }

    private fun cancelCollectionJobs() {
        callReportJob?.cancel()
        callMetricJob?.cancel()
        callReportJob = null
        callMetricJob = null
    }

    companion object {
        private val TAG = AppTag.make(AppFeatureTag.SAMPLE_APPLICATION, AppComponentTag.NOTIFICATION, PushListenerService::class.java)
    }
}

