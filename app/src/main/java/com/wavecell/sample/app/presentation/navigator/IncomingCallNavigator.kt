package com.wavecell.sample.app.presentation.navigator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.wavecell.sample.app.presentation.view.activity.ActivityCall

class IncomingCallNavigator constructor(private val activity: AppCompatActivity) {

    fun navigateToActivityCall() {
        val intent = Intent(activity, ActivityCall::class.java)
        activity.startActivity(intent)
        finish()
    }

    fun finish() {
        activity.finish()
    }
}