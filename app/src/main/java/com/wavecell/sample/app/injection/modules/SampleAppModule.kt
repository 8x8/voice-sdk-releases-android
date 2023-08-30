package com.wavecell.sample.app.injection.modules

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.work.WorkManager
import com.eght.voice.sdk.Voice
import com.wavecell.sample.app.ApiInterface
import com.wavecell.sample.app.GetTokenUseCase
import com.wavecell.sample.app.TokenRemoteDataSource
import com.wavecell.sample.app.TokenVerifier
import com.wavecell.sample.app.injection.scopes.SampleApplicationScope
import com.wavecell.sample.app.log.ConsoleLogger
import com.wavecell.sample.app.log.FileLogWriter
import com.wavecell.sample.app.log.WavecellVoiceLogListener
import com.wavecell.sample.app.network.RetrofitClientInstance
import com.wavecell.sample.app.presentation.model.factory.TokenRefreshHandler
import com.wavecell.sample.app.presentation.navigator.ToastNavigator
import com.wavecell.sample.app.proximity.CallProximitySensor
import com.wavecell.sample.app.proximity.ProximityLock
import com.wavecell.sample.app.utils.LifecycleManager
import com.wavecell.sample.app.utils.SampleAppAccountPreferences
import com.wavecell.sample.app.utils.SampleAppPreferences
import com.wavecell.sample.app.utils.UncaughtExceptionHandler
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import javax.inject.Named

@Module
internal class SampleAppModule(private val context: Context) {

    @Provides
    fun provideWorkManager(): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    fun provideApplicationContext(): Context {
        return context
    }

    @Provides
    fun provideApplication(): Application {
        return context.applicationContext as Application
    }

    @Provides
    fun provideVoice() = Voice.getInstance()

    @Provides
    protected fun provideSampleDataUtil(@Named("SampleAppDataUtil") sharedPreferences:
                                          SharedPreferences): SampleAppPreferences {
        return SampleAppPreferences(sharedPreferences)
    }

    @Provides
    @Named("SampleAppDataUtil")
    protected fun provideSampleDataUtilPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("SampleAppData", Context.MODE_PRIVATE)
    }

    @Provides
    protected fun provideSampleAppAccountDataUtil(@Named("SampleAppAccountDataUtil") sharedPreferences:
                                        SharedPreferences): SampleAppAccountPreferences {
        return SampleAppAccountPreferences(sharedPreferences)
    }

    @Provides
    @Named("SampleAppAccountDataUtil")
    protected fun provideSampleAccountDataUtilPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("SampleAppAccountData", Context.MODE_PRIVATE)
    }

    @Provides
    fun provideFileLogWriter(context: Context,
                             voice: Voice): FileLogWriter =
            FileLogWriter(context, voice)

    @Provides
    fun provideUncaughtExceptionHandler(logWriter: FileLogWriter): UncaughtExceptionHandler =
            UncaughtExceptionHandler(logWriter)

    @Provides
    fun provideLifecycleManager(proximitySensor: CallProximitySensor, voice: Voice, application: Application, voiceLogListener: WavecellVoiceLogListener, tokenRefreshHandler: TokenRefreshHandler) =
            LifecycleManager(proximitySensor, application, voiceLogListener, tokenRefreshHandler, voice)


    /**
     * Proximity Sensor
     */

    @Provides
    fun provideProximityLock(context: Context) =
            ProximityLock(context)

    @Provides
    fun provideCallProximitySensor(proximityLock: ProximityLock, voice: Voice) =
            CallProximitySensor(proximityLock, voice)


    @Provides
    fun ProvidesRetrofitClientInstance() = RetrofitClientInstance().getRetrofitInstance()

    @Provides
    fun providesTokenApi(retrofitClientInstance: Retrofit) : ApiInterface {
        return retrofitClientInstance.create(ApiInterface::class.java)
    }

    @Provides
    fun provideGetTokenUseCase(tokenRemoteDataSource: TokenRemoteDataSource): GetTokenUseCase {
        return GetTokenUseCase(Dispatchers.Default, tokenRemoteDataSource)
    }

    @Provides
    fun providesTokenRemoteDataSource(tokenApi: ApiInterface, sampleAppPreferences: SampleAppPreferences) = TokenRemoteDataSource(tokenApi, sampleAppPreferences)

    @Provides
    fun provideToastNavigator() = ToastNavigator(context)

    @Provides
    fun provideTokenNeedsRefresh(sampleAppPreferences: SampleAppPreferences) = TokenVerifier(sampleAppPreferences)

    @Provides
    @SampleApplicationScope
    fun providesTokenRefreshHandler(workManager: WorkManager, toastNavigator: ToastNavigator) = TokenRefreshHandler(workManager, toastNavigator, Dispatchers.Default)

    @Provides
    @SampleApplicationScope
    fun provideVoiceLogListener(logWriter: FileLogWriter): WavecellVoiceLogListener = WavecellVoiceLogListener(logWriter, ConsoleLogger(), Dispatchers.Default)
}