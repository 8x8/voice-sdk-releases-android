package com.wavecell.sample.app.presentation.notifications

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.wavecell.sample.app.WavecellApplication
import com.wavecell.sample.app.constants.Constants
import com.wavecell.sample.app.log.AppComponentTag
import com.wavecell.sample.app.log.AppFeatureTag
import com.wavecell.sample.app.log.AppLog
import com.wavecell.sample.app.log.AppTag
import java.util.UUID

class CallNotificationPendingIntentProvider(private val context: Context) {

    companion object {
        private val TAG = AppTag.make(AppFeatureTag.SAMPLE_APPLICATION, AppComponentTag.NOTIFICATION, CallNotificationPendingIntentProvider::class.java)
    }

    fun getPendingIntent(action: CallPendingIntentType, callUniqueId: UUID? = null): PendingIntent {
        return when (action) {
            CallPendingIntentType.FULL_SCREEN_INCOMING_CALL_LAUNCH -> {
                getLaunchPendingIntent(CallPendingIntentType.FULL_SCREEN_INCOMING_CALL_LAUNCH)
            }
            CallPendingIntentType.FULL_SCREEN_IN_CALL_LAUNCH -> {
                getLaunchPendingIntent(CallPendingIntentType.FULL_SCREEN_IN_CALL_LAUNCH)
            }
            else -> {
                AppLog.d(TAG, "${Constants.NOTIFICATION_PREFIX} Get pending intent for action $action and UniqueId: $callUniqueId")
                getNotificationActionPendingIntent(action, callUniqueId)
            }
        }
    }

    private fun getNotificationActionPendingIntent(action: CallPendingIntentType, callUniqueId: UUID?): PendingIntent {
        val intent = Intent(getIntentAction(action))
        val bundle = Bundle()
        callUniqueId?.let {
            bundle.putSerializable(WavecellApplication.IntentExtra.CALL_UNIQUE_ID, callUniqueId)
            intent.putExtras(bundle)
            AppLog.d(TAG, "${Constants.NOTIFICATION_PREFIX} prepare pending intent for action $action and" + " UniqueId: $callUniqueId")
        }
        return getServicePendingIntent(intent)
    }

    private fun getLaunchPendingIntent(fullScreenInCallLaunch: CallPendingIntentType): PendingIntent {
        val launchFullScreenIntent = Intent(getIntentAction(fullScreenInCallLaunch))
        launchFullScreenIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        return PendingIntent.getActivity(context, WavecellApplication.IntentCodes.CALL_REQUEST_CODE,
                launchFullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    private fun getIntentAction(action: CallPendingIntentType): String {
        return when (action) {
            CallPendingIntentType.INCOMING_CALL_ANSWER -> WavecellApplication.IntentAction.NOTIFICATION_ANSWER_CALL_ACTION
            CallPendingIntentType.INCOMING_CALL_DECLINE -> WavecellApplication.IntentAction.NOTIFICATION_REJECT_CALL_ACTION
            CallPendingIntentType.DIALING_CALL_HANGUP -> WavecellApplication.IntentAction.NOTIFICATION_DIALING_HANGUP_CALL_ACTION
            CallPendingIntentType.ONGOING_CALL_HANGUP -> WavecellApplication.IntentAction.NOTIFICATION_CONNECTED_HANGUP_CALL_ACTION
            CallPendingIntentType.FULL_SCREEN_IN_CALL_LAUNCH -> WavecellApplication.IntentAction.ONGOING_ACTIVITY_LAUNCH_ACTION
            CallPendingIntentType.FULL_SCREEN_INCOMING_CALL_LAUNCH -> WavecellApplication.IntentAction.INCOMING_ACTIVITY_LAUNCH_ACTION
        }
    }

    private fun getServicePendingIntent(intent: Intent): PendingIntent {
        return PendingIntent.getForegroundService(context, WavecellApplication.IntentCodes.CALL_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }
}

enum class CallPendingIntentType {
    INCOMING_CALL_ANSWER, INCOMING_CALL_DECLINE, DIALING_CALL_HANGUP, ONGOING_CALL_HANGUP, FULL_SCREEN_IN_CALL_LAUNCH, FULL_SCREEN_INCOMING_CALL_LAUNCH
}
