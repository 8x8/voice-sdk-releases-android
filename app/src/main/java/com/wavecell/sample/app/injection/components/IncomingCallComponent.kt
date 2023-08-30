package com.wavecell.sample.app.injection.components

import com.wavecell.sample.app.injection.modules.IncomingCallModule
import com.wavecell.sample.app.presentation.view.activity.ActivityIncomingCall
import dagger.Subcomponent

@Subcomponent(modules = [IncomingCallModule::class])
interface IncomingCallComponent {
    fun inject(target: ActivityIncomingCall)
}