package com.wavecell.sample.app.presentation.notifications

import android.app.Notification
import android.content.Context
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.eght.voice.sdk.model.VoiceCall
import com.wavecell.sample.app.R
import com.wavecell.sample.app.WavecellApplication
import com.wavecell.sample.app.constants.Constants
import com.wavecell.sample.app.log.AppComponentTag
import com.wavecell.sample.app.log.AppFeatureTag
import com.wavecell.sample.app.log.AppLog
import com.wavecell.sample.app.log.AppTag
import java.util.concurrent.TimeUnit

class AppCallNotificationFactory(
        private val context: Context,
        private val callNotificationPendingIntentProvider: CallNotificationPendingIntentProvider,
        private val callNotificationActionFactory: CallNotificationActionFactory) {

    companion object {
        private val TAG = AppTag.make(AppFeatureTag.SAMPLE_APPLICATION, AppComponentTag.NOTIFICATION, AppCallNotificationFactory::class.java)
    }

    fun createOngoingCallNotification(voiceCall: VoiceCall): Notification {
        val title = voiceCall.displayName ?: "Unknown"
        val callStartTimeInSeconds = voiceCall.callStartTime

        val builder = NotificationCompat.Builder(context, CallNotificationExtraType.ONGOING.channelId)
                .setPriority(CallNotificationExtraType.ONGOING.priority)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOngoing(true)
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                .setContentTitle(title)
                .setContentText(context.getString(R.string.android_calls_touch_to_return_to_call))
                .setSmallIcon(R.drawable.ic_phone_solid_notification)
                .setColor(ContextCompat.getColor(context, R.color.dodger_blue))
                .setExtras(createExtraBundle(CallNotificationExtraType.ONGOING))
                .addAction(callNotificationActionFactory.createActiveCallHangupAction(voiceCall
                        .uuid))
                .setContentIntent(callNotificationPendingIntentProvider.getPendingIntent(CallPendingIntentType.FULL_SCREEN_IN_CALL_LAUNCH))
                .setUsesChronometer(true)

        callStartTimeInSeconds.let {
            builder.setWhen(TimeUnit.SECONDS.toMillis(it))
        }

        return builder.build()
    }

    fun createDialingCallNotification(voiceCall: VoiceCall): Notification {
        val uuid = voiceCall.uuid
        val title = voiceCall.displayName ?: "Unknown"

        AppLog.d(TAG, "${Constants.NOTIFICATION_PREFIX} Creating Dialing Hangup action for call with" +
                " UniqueId: $uuid")
        val builder = NotificationCompat.Builder(context, CallNotificationExtraType.DIALING.channelId)
                .setPriority(CallNotificationExtraType.DIALING.priority)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOngoing(true)
                .setAutoCancel(false)
                .setContentTitle(title)
                .setContentText(context.getString(R.string.android_dialing))
                .setSmallIcon(R.drawable.ic_phone_solid_notification)
                .setColor(ContextCompat.getColor(context, R.color.dodger_blue))
                .setExtras(createExtraBundle(CallNotificationExtraType.DIALING))
                .setOnlyAlertOnce(true)
                .addAction(callNotificationActionFactory.createDialingHangupAction(uuid))
                .setContentIntent(callNotificationPendingIntentProvider.getPendingIntent(CallPendingIntentType.FULL_SCREEN_IN_CALL_LAUNCH))

        return builder.build()
    }

    fun createIncomingCallNotification(voiceCall: VoiceCall, silently: Boolean): Notification {
        val uuid = voiceCall.uuid
        val title = voiceCall.displayName ?: "Unknown"
        AppLog.d(TAG, "${Constants.NOTIFICATION_PREFIX} Creating Dialing Hangup action for call with" +
                " UniqueId: $uuid and callerId $title")

        val notificationExtraType = if(silently) CallNotificationExtraType.INCOMINGLOW else
            CallNotificationExtraType.INCOMINGHIGH

        val builder = NotificationCompat.Builder(context, CallNotificationExtraType.INCOMINGHIGH.channelId)
                .setPriority(notificationExtraType.priority)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOngoing(true)
                .setAutoCancel(false)
                .setContentTitle(title)
                .setContentText(context.getString(R.string.incoming_call_ellipsis))
                .setSmallIcon(R.drawable.ic_phone_solid_notification)
                .setColor(ContextCompat.getColor(context, R.color.dodger_blue))
                .setExtras(createExtraBundle(CallNotificationExtraType.INCOMINGHIGH))
                .setOnlyAlertOnce(/* since we may be showing and hiding this notification, we don't want the ringtone to stop */ false)
                .setDefaults(NotificationCompat.DEFAULT_LIGHTS)
                .addAction(callNotificationActionFactory.createAnswerAction(uuid))
                .addAction(callNotificationActionFactory.createRejectAction(uuid))
                .setContentIntent(callNotificationPendingIntentProvider.getPendingIntent(CallPendingIntentType.FULL_SCREEN_INCOMING_CALL_LAUNCH))
                .setFullScreenIntent(callNotificationPendingIntentProvider.getPendingIntent(CallPendingIntentType.FULL_SCREEN_INCOMING_CALL_LAUNCH), true)

        return builder.build()
    }

    fun createConnectingCallNotification(): Notification {
        val builder = NotificationCompat.Builder(context, CallNotificationExtraType.CONNECTING.channelId)
                .setPriority(CallNotificationExtraType.CONNECTING.priority)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                .setOngoing(true)
                .setAutoCancel(false)
                .setContentTitle(context.getString(R.string.connecting_ellipsis))
                .setContentText(context.getString(R.string.incoming_call_ellipsis))
                .setSmallIcon(android.R.drawable.stat_notify_sync)
                .setColor(ContextCompat.getColor(context, R.color.dodger_blue))
                .setExtras(createExtraBundle(CallNotificationExtraType.CONNECTING))
                .setOnlyAlertOnce(true)


        return builder.build()
    }

    private fun createExtraBundle(type: CallNotificationExtraType): Bundle {
        val bundle = Bundle()
        bundle.putString(WavecellApplication.IntentExtra.CALL_NOTIFICATION_EXTRA_CHANNEL, type.channelId)
        return bundle
    }

    sealed class CallNotificationExtraType(val priority: Int, val channelId: String) {
        object INCOMINGHIGH : CallNotificationExtraType(NotificationManagerCompat.IMPORTANCE_HIGH, NotificationChannelId.ONGOING_CALLS)
        object INCOMINGLOW : CallNotificationExtraType(NotificationManagerCompat.IMPORTANCE_LOW, NotificationChannelId.BACKGROUND_PROCESSING)
        object CONNECTING : CallNotificationExtraType(NotificationManagerCompat.IMPORTANCE_NONE, NotificationChannelId.BACKGROUND_PROCESSING)
        object ONGOING : CallNotificationExtraType(NotificationManagerCompat.IMPORTANCE_HIGH, NotificationChannelId.ONGOING_CALLS)
        object DIALING : CallNotificationExtraType(NotificationManagerCompat.IMPORTANCE_HIGH, NotificationChannelId.ONGOING_CALLS)
    }
}
