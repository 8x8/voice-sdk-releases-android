package com.wavecell.sample.app.presentation.navigator

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.eght.voice.sdk.Voice
import com.wavecell.sample.app.R

class ToastNavigator(private val context: Context) {

    fun showInvalidTokenError() {
        showToast(R.string.invalid_token)
    }
    fun showTokenRefreshFailed() {
        showToast(R.string.token_refresh_failed)
    }
    fun showTokenUpdateFailed() {
        showToast(R.string.token_update_failed)
    }

    fun showTokenUpdatedOnSDKSuccessfully() {
        showToast(R.string.token_update_success)
    }

    fun showPushTokenUpdateFailed() {
        showToast(R.string.push_token_update_failed)
    }

    fun showPushTokenUpdatedOnSDKSuccessfully() {
        showToast(R.string.push_token_update_success)
    }

    private fun showToast(message: Int) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }
}