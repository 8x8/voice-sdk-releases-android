package com.wavecell.sample.app.presentation.model.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eght.voice.sdk.Voice
import com.wavecell.sample.app.TokenVerifier
import com.wavecell.sample.app.extensions.cast
import com.wavecell.sample.app.log.FileLogWriter
import com.wavecell.sample.app.presentation.model.ActivityMainViewModel
import com.wavecell.sample.app.utils.LifecycleManager
import com.wavecell.sample.app.utils.SampleAppPreferences
import javax.inject.Inject

class ActivityMainViewModelFactory @Inject constructor(
    private val voice: Voice,
    private val logWriter: FileLogWriter,
    private val lifecycleManager: LifecycleManager,
    private val tokenNeedsRefresh: TokenVerifier,
    private val tokenRefreshHandler: TokenRefreshHandler,
    private val sampleAppPreferences: SampleAppPreferences
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ActivityMainViewModel(voice, logWriter, lifecycleManager, tokenNeedsRefresh, tokenRefreshHandler, sampleAppPreferences).cast()
    }
}