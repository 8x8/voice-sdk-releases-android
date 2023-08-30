package com.wavecell.sample.app.presentation.navigator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.wavecell.sample.app.custom.bottomsheets.AccountsBottomSheet
import com.wavecell.sample.app.presentation.view.activity.ActivityMain

class RegisterNavigator(private val activity: AppCompatActivity) {

    fun navigateToActivityMain() {
        val intent = Intent(activity, ActivityMain::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        activity.startActivity(intent)
        activity.finish()
    }

    fun showAccountsBottomSheet() {
        val fragmentTag = AccountsBottomSheet.NAME
        val supportFragment = activity.supportFragmentManager
        AccountsBottomSheet().apply {
            show(supportFragment, fragmentTag)
        }
    }
}