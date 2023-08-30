package com.wavecell.sample.app.presentation.callback

import com.eght.voice.sdk.model.VoiceCallAudioOption

interface VoiceCallAudioListener {

    fun onVoiceCallAudioOptionSelected(audioOption: VoiceCallAudioOption)
}