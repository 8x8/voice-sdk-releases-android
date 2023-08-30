package com.wavecell.sample.app.models

import com.eght.voice.sdk.model.VoiceCall
import com.wavecell.sample.app.extensions.toCallItem
import com.wavecell.sample.app.extensions.toRowViewModel
import com.wavecell.sample.app.presentation.model.RowRecentCallViewModel

object CallList {

    private val callLogs : ArrayList<CallItem> = ArrayList()

    val list: List<RowRecentCallViewModel>
        get() = callLogs.map { it.toRowViewModel() }.reversed()

    fun add(callItem: VoiceCall) {
        callLogs.add(callItem.toCallItem())
    }

    fun clearCallLogs() {
        callLogs.clear()
    }
}