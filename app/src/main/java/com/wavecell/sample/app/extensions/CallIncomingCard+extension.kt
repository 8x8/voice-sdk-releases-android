package com.wavecell.sample.app.extensions

import androidx.databinding.BindingAdapter
import com.eght.voice.sdk.model.VoiceCall
import com.wavecell.sample.app.custom.cards.CallIncomingCard

@BindingAdapter("shouldDisplayIncoming")
fun CallIncomingCard.shouldDisplayIncoming(voiceCall: VoiceCall?) {
    call = voiceCall
}