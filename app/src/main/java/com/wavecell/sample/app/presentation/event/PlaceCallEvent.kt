package com.wavecell.sample.app.presentation.event

import androidx.annotation.StringRes
import java.util.UUID

sealed class PlaceCallEvent {
    data class Success(val callId: UUID, val callee: String): PlaceCallEvent()
    data class Failure(@StringRes val resId: Int): PlaceCallEvent()
}
