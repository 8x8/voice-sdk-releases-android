package com.wavecell.sample.app.extensions

import com.eght.voice.sdk.model.VoiceCall
import com.wavecell.sample.app.models.CallItem
import com.wavecell.sample.app.presentation.model.CallType

fun VoiceCall.toCallItem(): CallItem {
    val name = displayName ?: "Unknown"
    val callType = if(direction == VoiceCall.Direction.INBOUND) CallType.INBOUND else CallType.OUTBOUND
    val endTime = System.currentTimeMillis()
    val startTime = if (callStartTime <= 0L) endTime else callStartTime * 1000
    val userId = phoneNumber ?: ""
    return CallItem(name, userId, avatarUrl, endTime, startTime, callType, uuid)
}