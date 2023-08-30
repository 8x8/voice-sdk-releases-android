package com.wavecell.sample.app.presentation.event

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class SingleEventBus<T> {
    private val eventChannel = Channel<T>(capacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    val events: Flow<T> = eventChannel.receiveAsFlow()

    suspend fun sendEvent(event: T) {
        eventChannel.send(event)
    }
}