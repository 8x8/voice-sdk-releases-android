package com.wavecell.sample.app.presentation.navigator

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.wavecell.sample.app.WavecellApplication
import com.wavecell.sample.app.log.AppComponentTag
import com.wavecell.sample.app.log.AppFeatureTag
import com.wavecell.sample.app.log.AppLog
import com.wavecell.sample.app.log.AppTag

class CallUILauncher(private val context: Context) {
    fun launchIncomingCallUI() {
        val intent = Intent(WavecellApplication.IntentAction.INCOMING_ACTIVITY_LAUNCH_ACTION)
       startActivity(intent)
    }

    fun launchInCallUI() {
        val intent = Intent(WavecellApplication.IntentAction.ONGOING_ACTIVITY_LAUNCH_ACTION)
        startActivity(intent)
    }

    private fun startActivity(intent: Intent) {
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        // Use pending intent to bring activity to foreground. startActivity()
        // doesn't always work (Example: press home screen and use startActivity() within 5 seconds).
        val pendingIntent = PendingIntent.getActivity(context.applicationContext, WavecellApplication.IntentCodes.CALL_USER_INTERFACE_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        try {
            pendingIntent.send()
        } catch (e: PendingIntent.CanceledException) {
            AppLog.w(TAG, "Unable to send pending intent, falling back to direct activity launch", e)
            context.startActivity(intent) // Use startActivity() as a backup.
        }
    }

    companion object {
        private val TAG = AppTag.make(AppFeatureTag.SAMPLE_APPLICATION, AppComponentTag.UI, CallUILauncher::class.java)
    }
}