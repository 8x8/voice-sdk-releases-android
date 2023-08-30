package com.wavecell.sample.app.injection.modules

import android.app.Application
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.eght.voice.sdk.Voice
import com.wavecell.sample.app.presentation.navigator.CallUILauncher
import com.wavecell.sample.app.presentation.notifications.AppCallHandler
import com.wavecell.sample.app.presentation.notifications.AppCallNotificationFactory
import com.wavecell.sample.app.presentation.notifications.CallNotificationActionFactory
import com.wavecell.sample.app.presentation.notifications.CallNotificationPendingIntentProvider
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers

@Module
class NotificationModule {

    @Provides
    fun provideAppCallHandler(voice: Voice, appCallNotificationFactory:
    AppCallNotificationFactory, application: Application
    ) =
            AppCallHandler(voice, appCallNotificationFactory, application, Dispatchers.Default)

    @Provides
    fun provideAppCallNotificationFactory(context: Context,
                                          callNotificationPendingIntentProvider:
                                          CallNotificationPendingIntentProvider,
                                          callNotificationActionFactory: CallNotificationActionFactory) =
            AppCallNotificationFactory(context, callNotificationPendingIntentProvider, callNotificationActionFactory)

    @Provides
    fun provideAppCallNotificationPendingIntentProvider(context: Context) =
            CallNotificationPendingIntentProvider(context)

    @Provides
    protected fun provideNotificationManagerCompat(context: Context) = NotificationManagerCompat.from(context)

    @Provides
    protected fun providesCallUILauncher(context: Context) = CallUILauncher(context)

    @Provides
    fun callNotificationActionFactory(context: Context, callNotificationPendingIntentProvider: CallNotificationPendingIntentProvider) =
            CallNotificationActionFactory(context, callNotificationPendingIntentProvider)
}
