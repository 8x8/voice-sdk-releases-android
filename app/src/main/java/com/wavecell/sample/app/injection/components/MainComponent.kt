package com.wavecell.sample.app.injection.components

import com.wavecell.sample.app.custom.bottomsheets.PlaceCallBottomSheet
import com.wavecell.sample.app.injection.modules.MainModule
import com.wavecell.sample.app.presentation.view.activity.ActivityMain
import dagger.Subcomponent

@Subcomponent(modules = [MainModule::class])
interface MainComponent {
    fun inject(main: ActivityMain)
    fun inject(placeCallBottomSheet: PlaceCallBottomSheet)
}
