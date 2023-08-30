package com.wavecell.sample.app

import android.app.Application
import android.app.NotificationManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import com.wavecell.sample.app.constants.Constants.LOG_PREFIX
import com.wavecell.sample.app.injection.SampleAppComponentContainer
import com.wavecell.sample.app.injection.SampleAppComponentHolder
import com.wavecell.sample.app.injection.components.SampleAppComponent
import com.wavecell.sample.app.log.AppComponentTag
import com.wavecell.sample.app.log.AppFeatureTag
import com.wavecell.sample.app.log.AppLog
import com.wavecell.sample.app.log.AppTag
import com.wavecell.sample.app.log.WavecellVoiceLogListener
import com.wavecell.sample.app.presentation.notifications.NotificationChannelManager
import com.wavecell.sample.app.utils.UncaughtExceptionHandler
import javax.inject.Inject

class WavecellApplication : Application() {

    @Inject
    lateinit var uncaughtExceptionHandler: UncaughtExceptionHandler

    @Inject
    lateinit var wavecellVoiceLogListener: WavecellVoiceLogListener

    override fun onCreate() {
        super.onCreate()

        initInjector()
        injectDependencies()

        createNotificationChannels()

        setupPushToken()
        setupInstanceId()

        setupUncaughtExceptionHandler()

        DebugConfig.init(this)
    }

    private fun setupPushToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                AppLog.e(TAG, "$LOG_PREFIX failed to get PN_TOKEN", task.exception)
                return@OnCompleteListener
            }

            // Save new FCM registration token
            pnToken = task.result
            AppLog.i(TAG, "$LOG_PREFIX PN_TOKEN: $pnToken")
        })
    }

    private fun setupInstanceId() {
        FirebaseInstallations.getInstance().id.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                AppLog.e(TAG, "$LOG_PREFIX failed to get instance id", task.exception)
                return@OnCompleteListener
            }

            // Save instance id
            instanceId = task.result
            AppLog.i(TAG, "$LOG_PREFIX Instance ID: $instanceId")
        })
    }

    private fun injectDependencies() {
        val appComponent = componentHolder.getComponent(SampleAppComponent::class.java, this)
        appComponent.inject(this)
        AppLog.wavecellVoiceLogListener = wavecellVoiceLogListener
    }

    private fun initInjector() {
        componentHolder = SampleAppComponentContainer(this)
    }

    private fun setupUncaughtExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler)
    }

    private fun createNotificationChannels() {
        NotificationChannelManager().initChannels(this, this.getSystemService(NotificationManager::class.java))
    }

    companion object {
        var pnToken: String = ""
        var instanceId: String = ""
        lateinit var componentHolder: SampleAppComponentHolder
        private val TAG = AppTag.make(AppFeatureTag.SAMPLE_APPLICATION, AppComponentTag.UI, WavecellApplication::class.java)
    }

    object IntentAction {
        const val PRESENT_IN_CALL_ACTION = "${BuildConfig.APPLICATION_ID}.PRESENT_IN_CALL_ACTION"
        const val PRESENT_INCOMING_CALL_ACTION = "${BuildConfig.APPLICATION_ID}.PRESENT_INCOMING_CALL"
        const val PRESENT_DIALING_CALL_ACTION = "${BuildConfig.APPLICATION_ID}.DIAL_CALL"
        const val NOTIFICATION_DIALING_HANGUP_CALL_ACTION = "${BuildConfig.APPLICATION_ID}.DIALING_HANGUP_CALL"
        const val ONGOING_ACTIVITY_LAUNCH_ACTION = "${BuildConfig.APPLICATION_ID}.LAUNCH_CALL_UI"
        const val INCOMING_ACTIVITY_LAUNCH_ACTION = "${BuildConfig.APPLICATION_ID}.INCOMING_LAUNCH_CALL_UI"
        const val NOTIFICATION_ANSWER_CALL_ACTION = "${BuildConfig.APPLICATION_ID}.ANSWER_CALL"
        const val NOTIFICATION_REJECT_CALL_ACTION = "${BuildConfig.APPLICATION_ID}.DECLINE_CALL"
        const val NOTIFICATION_CONNECTED_HANGUP_CALL_ACTION = "${BuildConfig.APPLICATION_ID}.CONNECTED_HANGUP_CALL"
        const val MUTE_INCOMING_CALL_ACTION = "${BuildConfig.APPLICATION_ID}.MUTE_INCOMING_CALL"
    }
    object IntentExtra {
        const val CALL_UNIQUE_ID = "CALL_UNIQUE_ID"
        const val CALLEE_ID = "callee_id"
        const val CALL_NOTIFICATION_EXTRA_CHANNEL = "call_notification_extra"
    }

    object IntentCodes {
        const val CALL_REQUEST_CODE = 1524
        const val CALL_USER_INTERFACE_REQUEST_CODE = 3124
    }

}