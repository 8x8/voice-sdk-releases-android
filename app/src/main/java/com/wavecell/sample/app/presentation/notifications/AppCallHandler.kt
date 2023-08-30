package com.wavecell.sample.app.presentation.notifications

import android.app.Application
import android.content.Context
import android.content.Intent
import com.eght.voice.sdk.Voice
import com.eght.voice.sdk.model.CallAction
import com.eght.voice.sdk.model.VoiceCallResult
import com.wavecell.sample.app.WavecellApplication
import com.wavecell.sample.app.constants.Constants
import com.wavecell.sample.app.log.AppComponentTag
import com.wavecell.sample.app.log.AppFeatureTag
import com.wavecell.sample.app.log.AppLog
import com.wavecell.sample.app.log.AppTag
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AppCallHandler(
    private val voice: Voice,
    private val appCallNotificationFactory: AppCallNotificationFactory,
    private val application: Application,
    coroutineDispatcher: CoroutineDispatcher
) {
    private val coroutineScope = CoroutineScope(coroutineDispatcher)
    private var callActionJob: Job? = null

    companion object {
        private val TAG = AppTag.make(AppFeatureTag.SAMPLE_APPLICATION, AppComponentTag.UI, AppCallHandler::class.java)
        private const val PREFIX = "[notification]"
    }

    fun handle(data: MutableMap<String, String>) {
        val connectingCallNotification = appCallNotificationFactory.createConnectingCallNotification()

        callActionJob?.cancel()
        callActionJob = coroutineScope.launch {
            voice.callActions.collect {  callAction ->
                AppLog.i(TAG, "Received call action $callAction")
                when(callAction) {
                    CallAction.PRESENT_INCOMING_CALL -> showIncomingCallNotificationAndUI(application)
                    CallAction.MUTE_INCOMING_CALL -> muteIncomingCallNotification(application)
                }
            }
        }

        when(val callResult = voice.receiveCall(data, connectingCallNotification)) {
            is VoiceCallResult.Failure -> {
                AppLog.e(TAG, "${Constants.LOG_PREFIX} $PREFIX (handle) incoming call error: ${callResult.voiceCall?.uuid}", callResult.error)
            }
            is VoiceCallResult.Success -> {
                AppLog.d(TAG, "${Constants.LOG_PREFIX} $PREFIX (handle) incoming call success from ${callResult.voiceCall.displayName}")
            }
        }
    }

    private fun muteIncomingCallNotification(context: Context) {
        val callIntent = Intent(context, CallService::class.java)
        callIntent.action = WavecellApplication.IntentAction.MUTE_INCOMING_CALL_ACTION
        context.startService(callIntent)
    }

    private fun showIncomingCallNotificationAndUI(context: Context) {
        AppLog.v(TAG, "${Constants.NOTIFICATION_PREFIX} Application is ready to display " + "Incoming Call notification")
        val intent = Intent(context, CallService::class.java)
        intent.action = WavecellApplication.IntentAction.PRESENT_INCOMING_CALL_ACTION
        context.startForegroundService(intent)
    }
}
