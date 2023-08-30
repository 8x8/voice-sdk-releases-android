package com.wavecell.sample.app.presentation.notifications

import android.content.Context
import androidx.core.app.NotificationCompat
import com.wavecell.sample.app.R
import com.wavecell.sample.app.constants.Constants
import com.wavecell.sample.app.log.AppComponentTag
import com.wavecell.sample.app.log.AppFeatureTag
import com.wavecell.sample.app.log.AppLog
import com.wavecell.sample.app.log.AppTag
import java.util.UUID


class CallNotificationActionFactory constructor(
        private val context: Context,
        private val pendingIntentProvider: CallNotificationPendingIntentProvider
) {

    companion object {
        private val TAG = AppTag.make(AppFeatureTag.SAMPLE_APPLICATION, AppComponentTag.NOTIFICATION, CallNotificationActionFactory::class.java)
    }

    fun createDialingHangupAction(callUUID: UUID): NotificationCompat.Action {
        AppLog.d(TAG, "${Constants.NOTIFICATION_PREFIX} Creating Dialing Hangup action for call with" +
                " UniqueId: $callUUID")
        val intent = pendingIntentProvider.getPendingIntent(CallPendingIntentType
                .DIALING_CALL_HANGUP, callUUID)
        return NotificationCompat.Action.Builder(0, context.getString(R.string.hang_up), intent)
                .setSemanticAction(NotificationCompat.Action.SEMANTIC_ACTION_NONE)
                .setShowsUserInterface(false)
                .build()
    }

    fun createAnswerAction(callUUID: UUID): NotificationCompat.Action {
        val intent = pendingIntentProvider.getPendingIntent(CallPendingIntentType
                .INCOMING_CALL_ANSWER, callUUID)
        return NotificationCompat.Action.Builder(0, context.getString(R.string.answer), intent)
                .setSemanticAction(NotificationCompat.Action.SEMANTIC_ACTION_NONE)
                .setShowsUserInterface(true)
                .build()
    }

    fun createRejectAction(callUUID: UUID): NotificationCompat.Action {
        val intent = pendingIntentProvider.getPendingIntent(CallPendingIntentType
                .INCOMING_CALL_DECLINE, callUUID)
        return NotificationCompat.Action.Builder(0, context.getString(R.string.reject), intent)
                .setSemanticAction(NotificationCompat.Action.SEMANTIC_ACTION_NONE)
                .setShowsUserInterface(false)
                .build()
    }

    fun createActiveCallHangupAction(uuid: UUID): NotificationCompat.Action {
        val intent = pendingIntentProvider.getPendingIntent(CallPendingIntentType
                .ONGOING_CALL_HANGUP, uuid)
        return NotificationCompat.Action.Builder(0, context.getString(R.string.hang_up), intent)
                .setSemanticAction(NotificationCompat.Action.SEMANTIC_ACTION_NONE)
                .setShowsUserInterface(false)
                .build()
    }


}
