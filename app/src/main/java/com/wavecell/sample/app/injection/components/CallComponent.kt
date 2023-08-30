package com.wavecell.sample.app.injection.components

import com.wavecell.sample.app.custom.bottomsheets.AudioOptionsBottomSheet
import com.wavecell.sample.app.injection.modules.CallModule
import com.wavecell.sample.app.presentation.view.activity.ActivityCall
import dagger.Subcomponent

@Subcomponent(modules = [CallModule::class])
interface CallComponent {
    fun inject(call: ActivityCall)
    fun inject(audioOptionsBottomSheet: AudioOptionsBottomSheet)
}