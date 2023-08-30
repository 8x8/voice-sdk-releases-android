package com.wavecell.sample.app.extensions

import androidx.databinding.BindingAdapter
import com.eght.voice.sdk.model.VoiceCall
import com.wavecell.sample.app.custom.cards.CallOnHoldCard
import com.wavecell.sample.app.presentation.model.ActivityCallViewModel

@BindingAdapter("shouldDisplayOnHold", "viewModel")
fun CallOnHoldCard.shouldDisplayOnHold(voiceCall: VoiceCall?, viewModel: ActivityCallViewModel) {
    call = voiceCall
    this.viewModel = viewModel
}