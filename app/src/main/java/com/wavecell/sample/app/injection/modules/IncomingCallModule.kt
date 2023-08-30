package com.wavecell.sample.app.injection.modules

import androidx.appcompat.app.AppCompatActivity
import com.wavecell.sample.app.presentation.callback.IncomingCallClickListener
import com.wavecell.sample.app.presentation.model.IncomingCallBottomSheetViewModel
import com.wavecell.sample.app.presentation.navigator.IncomingCallNavigator
import dagger.Module
import dagger.Provides

@Module
class IncomingCallModule(private val activity: AppCompatActivity) {


    /**
     * Navigator
     */

    @Provides
    fun providesNavigator(): IncomingCallNavigator {
        return IncomingCallNavigator(activity)
    }


    /**
     * Binds
     */

    @Provides
    fun providesIncomingCallClickListener(viewModel: IncomingCallBottomSheetViewModel): IncomingCallClickListener {
        return viewModel
    }
}