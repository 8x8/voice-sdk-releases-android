package com.wavecell.sample.app.presentation.model.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eght.voice.sdk.Voice
import com.wavecell.sample.app.PushTokenRefreshHandler
import com.wavecell.sample.app.extensions.cast
import com.wavecell.sample.app.presentation.model.PlaceCallBottomSheetViewModel
import com.wavecell.sample.app.utils.SampleAppAccountPreferences
import com.wavecell.sample.app.utils.SampleAppPreferences
import javax.inject.Inject

class PlaceCallBottomSheetViewModelFactory @Inject constructor(
    private val voice: Voice,
    private val preferences: SampleAppPreferences,
    private val sampleAppAccountPreferences: SampleAppAccountPreferences,
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PlaceCallBottomSheetViewModel(voice, preferences, sampleAppAccountPreferences).cast()
    }
}