package com.wavecell.sample.app.presentation.model.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eght.voice.sdk.Voice
import com.wavecell.sample.app.extensions.cast
import com.wavecell.sample.app.presentation.model.ActivityIncomingCallViewModel
import com.wavecell.sample.app.utils.SampleAppPreferences
import javax.inject.Inject

class ActivityIncomingCallViewModelFactory @Inject constructor(private val voice: Voice,
                                                               private val sampleAppPreferences: SampleAppPreferences
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ActivityIncomingCallViewModel(voice, sampleAppPreferences).cast()
    }
}