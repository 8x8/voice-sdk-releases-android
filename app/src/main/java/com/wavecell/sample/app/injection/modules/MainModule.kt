package com.wavecell.sample.app.injection.modules

import androidx.appcompat.app.AppCompatActivity
import com.wavecell.sample.app.presentation.navigator.Navigator
import dagger.Module
import dagger.Provides

@Module
class MainModule(private val activity: AppCompatActivity) {


    /**
     * Navigator
     */

    @Provides
    fun provideNavigator() = Navigator(activity)
}