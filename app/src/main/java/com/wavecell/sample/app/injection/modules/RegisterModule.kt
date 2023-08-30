package com.wavecell.sample.app.injection.modules

import androidx.appcompat.app.AppCompatActivity
import com.eght.voice.sdk.Voice
import com.wavecell.sample.app.GetTokenUseCase
import com.wavecell.sample.app.presentation.callback.VoiceAccountSelectionListener
import com.wavecell.sample.app.presentation.model.AccountsBottomSheetViewModel
import com.wavecell.sample.app.presentation.model.ActivityRegisterViewModel
import com.wavecell.sample.app.presentation.navigator.RegisterNavigator
import com.wavecell.sample.app.utils.SampleAppAccountPreferences
import com.wavecell.sample.app.utils.SampleAppPreferences
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class RegisterModule(private val activity: AppCompatActivity) {

    /**
     * Navigator
     */

    @Provides
    fun providesNavigator(): RegisterNavigator {
        return RegisterNavigator(activity)
    }

    /**
     * View Models
     */

    @Provides
    @Singleton
    fun provideActivityRegisterViewModel(voice: Voice,
                                         sampleAppPreferences: SampleAppPreferences,
                                         sampleAppAccountPreferences: SampleAppAccountPreferences,
                                         getTokenUseCase: GetTokenUseCase
    ): ActivityRegisterViewModel {
        return ActivityRegisterViewModel(voice, sampleAppPreferences, sampleAppAccountPreferences, getTokenUseCase)
    }


    @Provides
    fun provideUsersBottomSheetViewModel(accountSelectionListener: VoiceAccountSelectionListener,
    sampleAppPreferences: SampleAppAccountPreferences): AccountsBottomSheetViewModel {
        return AccountsBottomSheetViewModel(accountSelectionListener, sampleAppPreferences)
    }


    /**
     * Voice Account Selection Listener
     */

    @Provides
    @Singleton
    fun provideActivityRegisterViewModelCallback(
            viewModel: ActivityRegisterViewModel
    ): VoiceAccountSelectionListener {
        return viewModel
    }
}