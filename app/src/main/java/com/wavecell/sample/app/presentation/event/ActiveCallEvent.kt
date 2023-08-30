package com.wavecell.sample.app.presentation.event

import com.eght.voice.sdk.model.VoiceCallAudioOption

sealed class ActiveCallEvent {
    class ShowToast(val message: String): ActiveCallEvent()
    object FinishActivity: ActiveCallEvent()
    class ShowAudioOptions(val audioOption: VoiceCallAudioOption): ActiveCallEvent()
}
