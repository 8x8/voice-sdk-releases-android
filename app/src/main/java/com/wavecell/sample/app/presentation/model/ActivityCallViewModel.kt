package com.wavecell.sample.app.presentation.model

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableLong
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eght.voice.sdk.Voice
import com.eght.voice.sdk.model.CallActionCompletionStatus
import com.eght.voice.sdk.model.VoiceCall
import com.eght.voice.sdk.model.VoiceCallAudioOption
import com.eght.voice.sdk.model.VoiceCallState
import com.wavecell.sample.app.constants.Constants
import com.wavecell.sample.app.log.AppComponentTag
import com.wavecell.sample.app.log.AppFeatureTag
import com.wavecell.sample.app.log.AppLog
import com.wavecell.sample.app.log.AppTag
import com.wavecell.sample.app.presentation.callback.ActiveCallClickListener
import com.wavecell.sample.app.presentation.callback.ActiveCallClickOption
import com.wavecell.sample.app.presentation.callback.VoiceCallAudioListener
import com.wavecell.sample.app.presentation.event.ActiveCallEvent
import kotlinx.coroutines.launch
import javax.inject.Inject


class ActivityCallViewModel @Inject constructor(
        private val voice: Voice
): ViewModel(),
        DefaultLifecycleObserver,
        VoiceCallAudioListener,
        ActiveCallClickListener {

    companion object {
        private val TAG = AppTag.make(AppFeatureTag.SAMPLE_APPLICATION, AppComponentTag.UI, ActivityCallViewModel::class.java)
    }

    var event = MutableLiveData<ActiveCallEvent>()

    var activeCall: VoiceCall? = null
        set(value) {
            field = value
            isPeerOnHold.set(value?.isPeerOnHold ?: false)
            isMuted.set(value?.isMuted ?: false)
            startTime.set(value?.callStartTime ?: 0)
            peerName.set(value?.displayName ?: "Unknown")
            avatarUrl.set(value?.avatarUrl)
            onAudioOption.set(voice.currentAudioOption)
        }

    var incomingCall = ObservableField<VoiceCall?>()
    var onHoldCall = ObservableField<VoiceCall?>()
    var isPeerOnHold = ObservableBoolean(false)
    var isMuted = ObservableBoolean(false)
    var startTime = ObservableLong(0)
    var peerName = ObservableField<String>()
    var avatarUrl = ObservableField<String>()
    var onAudioOption = ObservableField(VoiceCallAudioOption.EARPIECE)


    /**
     * Lifecycle
     */

    init {
        viewModelScope.launch {
            voice.voiceCallState.collect(this@ActivityCallViewModel::onVoiceCallStateUpdated)
        }
        viewModelScope.launch {
            voice.callAudioOptionUpdates.collect(this@ActivityCallViewModel::onAudioChanged)
        }
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        getCurrentAudioOption()
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        activeCall = voice.voiceCall
        AppLog.i(TAG, "Active call has uuid ${activeCall?.uuid}")

        onHoldCall.set(voice.calls.firstOrNull { it.isPeerOnHold })
    }

    /**
     * Call Actions
     */

    private fun onHangup() {
        activeCall?.hangup() ?: AppLog.e(TAG, "${Constants.LOG_PREFIX} no active call present")
    }

    private fun onHold() {
        activeCall?.let {
            if(it.hold() == CallActionCompletionStatus.FAILED) {
                AppLog.e(TAG, "${Constants.LOG_PREFIX} failed to put the call on hold")
            }
        } ?: AppLog.e(TAG, "${Constants.LOG_PREFIX} no active call present")
    }

    private fun onResume() {
        activeCall?.let {
            if(it.resume() == CallActionCompletionStatus.FAILED) {
                AppLog.e(TAG, "${Constants.LOG_PREFIX} failed to resume the call")
            }
        } ?: AppLog.e(TAG, "${Constants.LOG_PREFIX} no active call present")
    }

    private fun onMute() {
        activeCall?.mute() ?: AppLog.e(TAG, "${Constants.LOG_PREFIX} no active call present")
    }

    private fun onUnmute() {
        activeCall?.unmute() ?: AppLog.e(TAG, "${Constants.LOG_PREFIX} no active call present")
    }

    private fun getCurrentAudioOption() {
        onAudioOption.set(voice.currentAudioOption)
    }


    private fun onAudioChanged(audioOption: VoiceCallAudioOption) {
        AppLog.d(TAG, "${Constants.LOG_PREFIX} received audio option: $audioOption")
        onAudioOption.set(audioOption)
    }


    /**
     * Active Call Click Option Listener Implementation
     */

    override fun onOptionSelected(option: ActiveCallClickOption) {
        when(option) {
            ActiveCallClickOption.HANG_UP -> onHangup()
            ActiveCallClickOption.HOLD -> {
                if (activeCall?.isPeerOnHold == true) {
                    onResume()
                } else {
                    onHold()
                }
            }
            ActiveCallClickOption.MUTE -> {
                isMuted.set(!isMuted.get())
                if (activeCall?.isMuted == true) {
                    onUnmute()
                    return
                }
                onMute()
            }
            ActiveCallClickOption.SPEAKER -> {
                val options = voice.availableAudioOptions
                AppLog.d(TAG, "---> switch audio option - available options: $options")
                if (options.contains(VoiceCallAudioOption.BLUETOOTH)) {
                    event.value = ActiveCallEvent.ShowAudioOptions(voice.currentAudioOption)
                    return
                }
                voice.toggleAudioOption()
            }
        }
    }


    /**
     * Voice Call Audio Listener Implementation
     */

    override fun onVoiceCallAudioOptionSelected(audioOption: VoiceCallAudioOption) {
        AppLog.i(TAG, "---> [audio] (onVoiceCallAudioOptionSelected) audio option selected: $audioOption")
        voice.setVoiceAudioOption(audioOption)
    }


    /**
     * Voice Call Update Listener Implementation
     */

    private fun onVoiceCallStateUpdated(voiceCallState: VoiceCallState) {
        when(voiceCallState) {
            is VoiceCallState.Added -> {
                AppLog.d(TAG, "---> [call] (onCallAdded) added call: ${voiceCallState.call.uuid}")
                incomingCall.set(voiceCallState.call)
            }
            is VoiceCallState.Failed -> {
                AppLog.e(TAG, "---> [call] (onCallFailed) ${voiceCallState.exception} active calls available: ${voice.hasActiveCalls}")
                event.value = ActiveCallEvent.ShowToast(voiceCallState.exception.message.toString())
                if (!voice.hasActiveCalls) {
                    event.value = ActiveCallEvent.FinishActivity
                }
            }
            is VoiceCallState.HoldUpdated -> {
                AppLog.d(TAG, "---> [call] (onCallHoldUpdated) call: ${voiceCallState.call?.uuid}")
                onHoldCall.set(voiceCallState.call)
            }
            is VoiceCallState.Removed -> {
                AppLog.d(TAG, "---> [call] (onCallRemoved) removed call: ${voiceCallState.call.uuid}")
                incomingCall.get()?.let {
                    if (voiceCallState.call.uuid == it.uuid) {
                        incomingCall.set(null)
                    }
                }
                onHoldCall.get()?.let {
                    if(voiceCallState.call.uuid == onHoldCall.get()?.uuid) {
                        onHoldCall.set(null)
                    }
                }
                if (!voice.hasActiveCalls) {
                    event.value = ActiveCallEvent.FinishActivity
                }
            }
            is VoiceCallState.Updated -> {
                AppLog.d(TAG, "---> [call] (onCallUpdated) updated call: ${voiceCallState.call.uuid}, call quality: ${voiceCallState.call.callQuality.name}")
                incomingCall.get()?.let {
                    if (voiceCallState.call.uuid == it.uuid) {
                        if (!voiceCallState.call.isIncoming) {
                            incomingCall.set(null)
                            if (voiceCallState.call.isOngoing) {
                                AppLog.d(TAG, "---> [call] (onCallUpdated) saving incoming call")
                                activeCall = voiceCallState.call
                            }
                        }
                        return
                    }
                }
                if (!voiceCallState.call.isIncoming) {
                    AppLog.d(TAG, "---> [call] (onCallUpdated) saving outgoing call")
                    activeCall = voiceCallState.call
                }
            }
        }
    }

    fun onEndingCallOnHold() {
        onHoldCall.get()?.hangup() ?:  AppLog.e(TAG, "${Constants.LOG_PREFIX} not able to end the call on hold")
    }

    fun onRetrieveCallOnHold() {
        onHoldCall.get()?.resume() ?: AppLog.e(TAG, "${Constants.LOG_PREFIX} not able to end the call on hold")
    }

}
