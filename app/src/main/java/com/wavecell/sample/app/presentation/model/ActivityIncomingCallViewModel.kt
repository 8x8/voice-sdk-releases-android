package com.wavecell.sample.app.presentation.model

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eght.voice.sdk.Voice
import com.eght.voice.sdk.model.VoiceCall
import com.wavecell.sample.app.log.AppComponentTag
import com.wavecell.sample.app.log.AppFeatureTag
import com.wavecell.sample.app.log.AppLog
import com.wavecell.sample.app.log.AppTag
import com.wavecell.sample.app.presentation.event.SingleEventBus
import com.wavecell.sample.app.utils.SampleAppPreferences
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import java.util.UUID

class ActivityIncomingCallViewModel(
        private val voice: Voice,
        private val sampleAppPreferences: SampleAppPreferences
): ViewModel(), DefaultLifecycleObserver
{


    companion object {
        private val TAG = AppTag.make(AppFeatureTag.SAMPLE_APPLICATION, AppComponentTag.UI, ActivityIncomingCallViewModel::class.java)
    }
    /**
     * Properties
     */

    private val _displayName = MutableStateFlow("")
    val displayName = _displayName.asStateFlow()

    private val _userId = MutableStateFlow("")
    val userId = _userId.asStateFlow()

    private val _incomingCall = MutableStateFlow<VoiceCall?>(null)
    val incomingCall = _incomingCall.asStateFlow()

    val finishEvent = SingleEventBus<Unit>()

    private val callStateJobs = mutableMapOf<UUID, Job>()
    private var incomingCallJob: Job? = null


    /**
     * Lifecycle Observers
     */


    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        loadUserInfo()
        listenToIncomingCallUpdates()
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        val voiceCall = voice.voiceCall
        AppLog.v(TAG, "---> [call] (resume) is call with uuid ${voiceCall?.uuid} incoming ${voiceCall?.isIncoming}")
        initVoiceCall(voiceCall)
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        callStateJobs.values.forEach { it.cancel() }
        callStateJobs.clear()
    }

            /**
     * Init
     */

    private fun loadUserInfo() {
        _userId.value = sampleAppPreferences.userId
        _displayName.value = sampleAppPreferences.userName
    }

    private fun listenToIncomingCallUpdates() {
        incomingCallJob?.cancel()
        incomingCallJob = viewModelScope.launch {
            incomingCall
                .filterNotNull()
                .collect {
                    if (!it.isIncoming) {
                        finishEvent.sendEvent(Unit)
                    }
            }
        }
    }

    private fun initVoiceCall(voiceCall: VoiceCall?) {
        voiceCall?.let {
            if (it.isIncoming) {
                _incomingCall.value = it
            } else if (it.isOngoing || it.isPeerOnHold) {
                _incomingCall.value = null
                viewModelScope.launch { finishEvent.sendEvent(Unit) }
            }
            callStateJobs[it.uuid]?.cancel()
            callStateJobs[it.uuid] = viewModelScope.launch {
                voice
                    .voiceCallStateForId(it.uuid, viewModelScope)
                    .collect { voiceCallState ->
                        onCallUpdated(voiceCallState.call)
                    }
            }
        } ?: run {
            _incomingCall.value = null
            viewModelScope.launch { finishEvent.sendEvent(Unit) }
        }
    }

    /**
     * Single Call Update Listener Implementation
     */

    private fun onCallUpdated(call: VoiceCall) {
        AppLog.v(TAG, "---> [call] (onCallUpdated) call: ${call.displayName}, incoming: ${call.isIncoming}")
        if (!call.isIncoming) {
            _incomingCall.value = null
            viewModelScope.launch { finishEvent.sendEvent(Unit) }
        } else {
            _incomingCall.value = call
        }
    }
}
