package com.wavecell.sample.app.presentation.callback

import com.wavecell.sample.app.models.VoiceAccount

interface VoiceAccountSelectionListener {

    fun onAccountSelected(account: VoiceAccount)
}