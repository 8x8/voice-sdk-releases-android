package com.wavecell.sample.app.presentation.model

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.eght.voice.sdk.model.VoiceCallAudioOption
import com.wavecell.sample.app.presentation.callback.VoiceCallAudioListener
import javax.inject.Inject

class AudioOptionsBottomSheetViewModel @Inject constructor(
        private val delegate: VoiceCallAudioListener
) {

    var shouldCollapse = ObservableBoolean(false)
    var onBluetoothAvailable = ObservableBoolean(false)
    var onAudioOptionSelected = ObservableField(VoiceCallAudioOption.EARPIECE)


    fun onAudioSelected(option: VoiceCallAudioOption) {
        onAudioOptionSelected.set(option)
        onAudioOptionSelected.get()?.let {
            delegate.onVoiceCallAudioOptionSelected(it)
        }
        shouldCollapse.set(true)
    }
}