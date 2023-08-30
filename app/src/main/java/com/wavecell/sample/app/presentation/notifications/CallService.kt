package com.wavecell.sample.app.presentation.notifications

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import com.eght.voice.sdk.Voice
import com.eght.voice.sdk.model.VoiceCall
import com.wavecell.sample.app.WavecellApplication
import com.wavecell.sample.app.constants.Constants
import com.wavecell.sample.app.constants.Constants.NOTIFICATION_PREFIX
import com.wavecell.sample.app.injection.components.SampleAppComponent
import com.wavecell.sample.app.log.AppComponentTag
import com.wavecell.sample.app.log.AppFeatureTag
import com.wavecell.sample.app.log.AppLog
import com.wavecell.sample.app.log.AppTag
import com.wavecell.sample.app.log.WavecellVoiceLogListener
import com.wavecell.sample.app.models.CallList
import com.wavecell.sample.app.presentation.model.factory.TokenRefreshHandler
import com.wavecell.sample.app.presentation.navigator.CallUILauncher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import kotlin.random.Random


class CallService : Service() {

    companion object {
        private val TAG = AppTag.make(AppFeatureTag.SAMPLE_APPLICATION, AppComponentTag.UI, CallService::class.java)
    }

    private val notificationId = Random.nextInt(10000, 99999)
    private val isAlive = AtomicBoolean(false)

    @Inject
    lateinit var callUILauncher: CallUILauncher

    @Inject
    lateinit var voice: Voice

    @Inject
    lateinit var appCallNotificationFactory: AppCallNotificationFactory

    @Inject
    lateinit var callNotificationPendingIntentProvider: CallNotificationPendingIntentProvider

    @Inject
    lateinit var notificationManager: NotificationManagerCompat

    @Inject
    lateinit var voiceLogListener: WavecellVoiceLogListener

    @Inject
    lateinit var tokenRefreshHandler: TokenRefreshHandler


    private val coroutineScope = CoroutineScope(Dispatchers.Default) + SupervisorJob()
    private val callStateJobs = mutableMapOf<UUID, Job>()
    private var callReportJob: Job? = null
    private var callMetricJob: Job? = null

    /**
     * Lifecycle
     */

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onCreate() {
        inject()
        super.onCreate()
        isAlive.set(true)

        initVoiceSDK()
    }

    override fun onDestroy() {
        // we need to clear the service in case we're not stopping it, but the OS
        clearService()
        super.onDestroy()
    }

    private fun inject() {
        val appComponent = WavecellApplication.componentHolder.getComponent(SampleAppComponent::class.java, this)
        appComponent.inject(this)
    }


    /**
     * Service Implementation
     */

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        when (intent.action) {
            WavecellApplication.IntentAction.PRESENT_DIALING_CALL_ACTION -> {
                val callUniqueId = intent.getSerializableExtra(WavecellApplication.IntentExtra.CALL_UNIQUE_ID) as UUID
                val call = voice.calls.find { it.uuid == callUniqueId }
                AppLog.v(TAG, "$NOTIFICATION_PREFIX Application started service to show dialing call notification for callUniqueId: $callUniqueId")
                call?.let { handleDialingAction(it) } ?: stop()
            }
            WavecellApplication.IntentAction.NOTIFICATION_CONNECTED_HANGUP_CALL_ACTION, WavecellApplication.IntentAction.NOTIFICATION_DIALING_HANGUP_CALL_ACTION -> {
                val callUniqueId = intent.getSerializableExtra(WavecellApplication.IntentExtra.CALL_UNIQUE_ID) as UUID
                val call = voice.calls.find { it.uuid == callUniqueId }
                AppLog.v(TAG, "$NOTIFICATION_PREFIX Application started service to hangup dialed call for callUniqueId: $callUniqueId")
                call?.let { endCall(it) } ?: stop()
            }
            WavecellApplication.IntentAction.PRESENT_INCOMING_CALL_ACTION -> {
                val call = voice.calls.find { it.isIncoming }
                call?.let {
                    handleIncomingAction(call, false)
                } ?: stop()
            }
            WavecellApplication.IntentAction.NOTIFICATION_ANSWER_CALL_ACTION -> {
                val callUniqueId = intent.getSerializableExtra(WavecellApplication.IntentExtra.CALL_UNIQUE_ID) as UUID
                val call = voice.calls.find { it.uuid == callUniqueId }
                AppLog.v(TAG, "$NOTIFICATION_PREFIX Application started service to answer incoming call for callUniqueId: $callUniqueId")
                call?.let {
                    answerCall(call)
                    terminateServiceForNoCallIfNeeded(call)
                } ?: stop()
            }
            WavecellApplication.IntentAction.NOTIFICATION_REJECT_CALL_ACTION -> {
                val callUniqueId = intent.getSerializableExtra(WavecellApplication.IntentExtra.CALL_UNIQUE_ID) as UUID
                val call = voice.calls.find { it.uuid == callUniqueId }
                AppLog.v(TAG, "$NOTIFICATION_PREFIX Application started service to hangup dialed call for callUniqueId: $callUniqueId")
                call?.let {
                    rejectCall(call)
                    terminateServiceForNoCallIfNeeded(call)
                } ?: stop()
            }
            WavecellApplication.IntentAction.MUTE_INCOMING_CALL_ACTION -> {
                val call = voice.calls.find { it.isIncoming }
                call?.let {
                    handleIncomingAction(call, true)
                } ?: stop()
            }
            WavecellApplication.IntentAction.PRESENT_IN_CALL_ACTION -> {
                voice.calls.let {
                    if(it.isNotEmpty()) {
                        callUILauncher.launchInCallUI()
                    }
                }
            }
            else -> {
                stop()
            }
        }

        return START_NOT_STICKY
    }


    /**
     * Notification Handlers
     */

    private fun handleDialingAction(call: VoiceCall) {
        showDialingCallNotification(call)
        observeVoiceCallStatus(call)
    }

    private fun handleIncomingAction(call: VoiceCall, silently: Boolean) {
        showIncomingCallNotification(call, silently)

        observeVoiceCallStatus(call)
        if (voice.calls.size == 1) {
            callUILauncher.launchIncomingCallUI()
        }
    }

    private fun observeVoiceCallStatus(call: VoiceCall) {
        AppLog.d(TAG, "---> [call] (observeVoiceCallStatus) for call with uuid ${call.uuid}")
        callStateJobs[call.uuid]?.cancel()
        callStateJobs[call.uuid] = coroutineScope.launch {
            voice
                .voiceCallStateForId(call.uuid, this)
                .collect { onCallUpdated(it.call) }
        }
    }

    /**
     * Notifications Displays
     */

    private fun showDialingCallNotification(call: VoiceCall) {
        val notification = appCallNotificationFactory.createDialingCallNotification(call)
        startForeground(notificationId, notification)
    }

    private fun showOngoingCallNotification(call: VoiceCall) {
        val notification = appCallNotificationFactory.createOngoingCallNotification(call)
        startForeground(notificationId, notification)
    }

    private fun showIncomingCallNotification(call: VoiceCall, silently: Boolean) {
        val notification = appCallNotificationFactory.createIncomingCallNotification(call, silently)
        startForeground(notificationId, notification)
    }


    /**
     * Call Actions
     */

    private fun endCall(call: VoiceCall) {
        AppLog.v(TAG, "$NOTIFICATION_PREFIX call to handup fetched $call")
        call.hangup()
    }

    private fun answerCall(call: VoiceCall) {
        AppLog.d(TAG, "---> [call] (answerCall) answered call with uuid: ${call.uuid}")
        AppLog.v(TAG, "$NOTIFICATION_PREFIX call to answer fetched $call")
        call.accept()
    }

    private fun rejectCall(call: VoiceCall) {
        AppLog.v(TAG, "$NOTIFICATION_PREFIX call to reject fetched $call")
        call.reject()
    }


    /**
     * Call Update Listener Implementation
     */

    private fun onCallUpdated(call: VoiceCall) {
        AppLog.d(TAG, "---> [call] (onCallUpdated) onGoing: ${call.isOngoing}")
        if(call.isDisconnected) {
            CallList.add(call)
        }

        if (!voice.hasActiveCalls) {
            AppLog.d(TAG, "$NOTIFICATION_PREFIX no more call, remove notification")
            stop()
        } else {
            when {
                call.isIncoming -> {
                    showIncomingCallNotification(call, false)
                }
                call.isOngoing -> {
                    AppLog.d(TAG, "$NOTIFICATION_PREFIX now we have an ongoing call $call")
                    showOngoingCallNotification(call)
                }
                call.isDisconnected -> {
                    AppLog.d(TAG, "$NOTIFICATION_PREFIX disconnected $call")
                    voice.calls.firstOrNull { it.isOngoing || it.isPeerOnHold }?.let {
                        AppLog.d(TAG, "$NOTIFICATION_PREFIX now we have an ongoing call after one disconnected $it")
                        showOngoingCallNotification(it)
                    } ?: stop()
                }
            }
        }
    }


    /**
     * Helpers
     */

    private fun stop() {
        // onDestroy may not be called, so make sure we clear everything here as well
        clearService()
        stopSelf()
    }

    private fun terminateServiceForNoCallIfNeeded(call: VoiceCall) {
        if (call.isDisconnected || call.isFailed) {
            stop()
        }
    }

    private fun clearService() {
        cancelCollectionJobs()
        cancelPendingIntents()
        stopForeground(true)
    }

    private fun cancelPendingIntents() {
        CallPendingIntentType.values()
                .map { callNotificationPendingIntentProvider.getPendingIntent(it) }
                .forEach(PendingIntent::cancel)
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
        callStateJobs.values.forEach { it.cancel() }
        callStateJobs.clear()
        callReportJob?.cancel()
        callMetricJob?.cancel()
        callReportJob = null
        callMetricJob = null
    }
}
