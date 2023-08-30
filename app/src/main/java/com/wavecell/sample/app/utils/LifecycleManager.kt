package com.wavecell.sample.app.utils

import android.app.Application
import com.eght.voice.sdk.Voice
import com.eght.voice.sdk.model.VoiceCallState
import com.wavecell.sample.app.log.WavecellVoiceLogListener
import com.wavecell.sample.app.presentation.model.factory.TokenRefreshHandler
import com.wavecell.sample.app.proximity.CallProximitySensor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

class LifecycleManager constructor(private val proximitySensor: CallProximitySensor,
                       private val application: Application,
                       private val voiceLogListener: WavecellVoiceLogListener,
                       private val tokenRefreshHandler: TokenRefreshHandler,
                       private val voice: Voice) {

    private val coroutineScope = CoroutineScope(Dispatchers.Default) + SupervisorJob()
    private var callStateJob: Job? = null
    private var callAudioOptionJob: Job? = null

    /**
     * Lifecycle
     */

    fun onLogin() {
        voice.init(application)
        tokenRefreshHandler.startTokenRefreshEventCollection(voice)
        voiceLogListener.startLogCollection(voice)
        subscribeToVoiceCallUpdates()
        subscribeToVoiceCallAudioOptionUpdates()

    }

    fun onLogout() {
        callStateJob?.cancel()
        callAudioOptionJob?.cancel()
        callStateJob = null
        callAudioOptionJob = null
    }


    /**
     * Subscriptions
     */

    private fun subscribeToVoiceCallUpdates() {
        callStateJob?.cancel()
        callStateJob = coroutineScope.launch {
            voice.voiceCallState
                .filterIsInstance<VoiceCallState.Updated>()
                .collect {
                    proximitySensor.onCallUpdated()
                }
        }
    }

    private fun subscribeToVoiceCallAudioOptionUpdates() {
        callAudioOptionJob?.cancel()
        callAudioOptionJob = coroutineScope.launch {
            voice.callAudioOptionUpdates
                .collect { proximitySensor.onAudioChanged(it) }
        }
    }
}