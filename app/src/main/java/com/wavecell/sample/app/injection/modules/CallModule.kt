package com.wavecell.sample.app.injection.modules

import androidx.appcompat.app.AppCompatActivity
import com.wavecell.sample.app.presentation.callback.VoiceCallAudioListener
import com.wavecell.sample.app.presentation.model.ActivityCallViewModel
import com.wavecell.sample.app.presentation.navigator.Navigator
import dagger.Module
import dagger.Provides

@Module
class CallModule(private val activity: AppCompatActivity) {

    /**
     * Navigator
     */

    @Provides
    fun provideNavigator() = Navigator(activity)


    /**
     * View Models callback
     */

    @Provides
    fun provideActivityCallCallback(viewModel: ActivityCallViewModel): VoiceCallAudioListener {
        return viewModel
    }
}