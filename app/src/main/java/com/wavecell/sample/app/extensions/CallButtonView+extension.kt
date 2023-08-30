package com.wavecell.sample.app.extensions

import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.eght.voice.sdk.model.VoiceCallAudioOption
import com.wavecell.sample.app.R
import com.wavecell.sample.app.custom.views.CallButtonView
import com.wavecell.sample.app.presentation.callback.ActiveCallClickListener
import com.wavecell.sample.app.presentation.callback.ActiveCallClickOption
import com.wavecell.sample.app.presentation.callback.IncomingCallClickListener
import com.wavecell.sample.app.presentation.callback.IncomingCallClickOption


@BindingAdapter("onHold")
fun CallButtonView.isCallOnHold(isPeerOnHold: Boolean) {
    title = if (isPeerOnHold) "On Hold" else "Hold"
    iconResId = if (isPeerOnHold) R.drawable.play else R.drawable.call_hold
    backColor = ContextCompat.getColor(context, if (isPeerOnHold) R.color.colorAccent else R.color.colorPrimaryLight)
}

@BindingAdapter("onMute")
fun CallButtonView.isCallMuted(isMuted: Boolean) {
    title = if (isMuted) "Muted" else "Mute"
    iconResId = if (isMuted) R.drawable.call_mic_off else R.drawable.call_mic_on
    backColor = ContextCompat.getColor(context, if (isMuted) R.color.colorPrimaryLight else R.color.colorAccent)
}

@BindingAdapter("onSpeaker")
fun CallButtonView.isCallOnSpeaker(audioOption: VoiceCallAudioOption) {
    val pair = when(audioOption) {
        VoiceCallAudioOption.EARPIECE -> Pair("Earpiece", R.drawable.speaker_phone)
        VoiceCallAudioOption.SPEAKER -> Pair("Speaker", R.drawable.call_speaker)
        VoiceCallAudioOption.BLUETOOTH -> Pair("Bluetooth", R.drawable.bluetooth)
    }
    title = pair.first
    iconResId = pair.second
}

@BindingAdapter("onActiveCallButtonClick", "activeCallClickOption")
fun addOnActiveCallClick(button: CallButtonView, listener: ActiveCallClickListener, option: ActiveCallClickOption) {
    button.setOnFabClickListener {
        listener.onOptionSelected(option)
    }
}

@BindingAdapter("onIncomingCallButtonClick", "onIncomingCallClickOption")
fun addOnIncomingCallClick(button: CallButtonView, listener: IncomingCallClickListener, option: IncomingCallClickOption) {
    button.setOnFabClickListener {
        if (option == IncomingCallClickOption.ACCEPT) {
            listener.onAccept()
        } else {
            listener.onReject()
        }
    }
}