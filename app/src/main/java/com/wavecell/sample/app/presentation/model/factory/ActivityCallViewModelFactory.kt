package com.wavecell.sample.app.presentation.model.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eght.voice.sdk.Voice
import com.wavecell.sample.app.extensions.cast
import com.wavecell.sample.app.presentation.model.ActivityCallViewModel
import javax.inject.Inject

class ActivityCallViewModelFactory @Inject constructor(
    private val voice: Voice
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ActivityCallViewModel(voice).cast()
    }
}