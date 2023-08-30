package com.wavecell.sample.app.injection.components

import com.wavecell.sample.app.custom.bottomsheets.AccountsBottomSheet
import com.wavecell.sample.app.injection.modules.RegisterModule
import com.wavecell.sample.app.presentation.view.activity.ActivityRegister
import dagger.Subcomponent
import javax.inject.Singleton

@Singleton
@Subcomponent(modules = [RegisterModule::class])
interface RegisterComponent {
    fun inject(usersBottomSheet: AccountsBottomSheet)
    fun inject(activity: ActivityRegister)
}
