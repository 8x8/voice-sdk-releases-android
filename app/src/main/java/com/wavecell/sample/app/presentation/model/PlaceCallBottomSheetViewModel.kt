package com.wavecell.sample.app.presentation.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eght.voice.sdk.Voice
import com.eght.voice.sdk.model.VoiceCallResult
import com.eght.voice.sdk.model.VoiceContact
import com.wavecell.sample.app.R
import com.wavecell.sample.app.constants.Constants
import com.wavecell.sample.app.log.AppComponentTag
import com.wavecell.sample.app.log.AppFeatureTag
import com.wavecell.sample.app.log.AppLog
import com.wavecell.sample.app.log.AppTag
import com.wavecell.sample.app.presentation.event.PlaceCallEvent
import com.wavecell.sample.app.presentation.event.SingleEventBus
import com.wavecell.sample.app.utils.SampleAppAccountPreferences
import com.wavecell.sample.app.utils.SampleAppPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class PlaceCallBottomSheetViewModel(
        private val voice: Voice,
        private val preferences: SampleAppPreferences,
        private val sampleAppAccountPreferences: SampleAppAccountPreferences
        ): ViewModel() {

    companion object {
        private val TAG = AppTag.make(AppFeatureTag.SAMPLE_APPLICATION, AppComponentTag.PLACING_CALL, PlaceCallBottomSheetViewModel::class.java)
    }

    val shouldCollapse = MutableStateFlow(false)
    val calleeIdText = MutableStateFlow("")
    val accountIdText = MutableStateFlow("")
    val onRequestFocus = MutableStateFlow(false)

    val placeCallEvent = SingleEventBus<PlaceCallEvent>()

    /**
     * Click Listeners
     */

    fun onClose() {
        shouldCollapse.value = true
    }

    fun makeCall() {
        val userId = calleeIdText.value

        if (userId.isEmpty()) {
            AppLog.e(TAG, "Invalid user id")
            viewModelScope.launch { placeCallEvent.sendEvent(PlaceCallEvent.Failure(R.string.place_call_failure)) }
            return
        }

        val accountId = accountIdText.value.trim()
        val calleeId = if (accountId.isBlank()) userId else "$userId!$accountId"

        if(userId == voice.userId && (accountId.isBlank() || accountId == sampleAppAccountPreferences.accountId)) {
            viewModelScope.launch { placeCallEvent.sendEvent(PlaceCallEvent.Failure(R.string.calling_self_error)) }
            return
        }

        preferences.recentCallAccountId = accountId
        val contact = VoiceContact(calleeId, userId, "", calleeId)
        AppLog.d(TAG, "${Constants.LOG_PREFIX} [call] calling $userId")

        viewModelScope.launch {
            when(val result = voice.placeCall(contact)) {
                is VoiceCallResult.Failure -> {
                    AppLog.e(TAG, "${Constants.LOG_PREFIX} (onFailure) call failed", result.error)
                    placeCallEvent.sendEvent(PlaceCallEvent.Failure(R.string.place_call_failure))
                }
                is VoiceCallResult.Success -> {
                    AppLog.d(TAG, "${Constants.LOG_PREFIX} (onSuccess) success outgoing call: ${result.voiceCall}")
                    shouldCollapse.value = true
                    placeCallEvent.sendEvent(PlaceCallEvent.Success(result.voiceCall.uuid, calleeIdText.value))
                }
            }
        }
    }
}
