package com.wavecell.sample.app.extensions

import com.eght.voice.sdk.model.InboundCallPath

val InboundCallPath.title: String
    get() {
        return when(this) {
            InboundCallPath.VOIP -> "VOIP"
            InboundCallPath.PSTN -> "PSTN"
            InboundCallPath.VOIP_PSTN_FALLBACK -> "VOIP -> PSTN"
        }
    }

val InboundCallPath.index: Int
    get() {
        return when(this) {
            InboundCallPath.VOIP -> 0
            InboundCallPath.PSTN -> 1
            InboundCallPath.VOIP_PSTN_FALLBACK -> 2
        }
    }