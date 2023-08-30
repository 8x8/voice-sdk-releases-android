package com.wavecell.sample.app.presentation.view.activity

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.wavecell.sample.app.R
import com.wavecell.sample.app.utils.AppApiLevelUtils

object PermissionProcessor {
    fun ensurePermissions(activity: AppCompatActivity) {
        // Demo app, not going to check or show rationale for requesting
        val notificationPermissions = if (AppApiLevelUtils.hasAtLeastT()) {
            arrayOf(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            arrayOf()
        }
        val phonePermissions = if (AppApiLevelUtils.hasAtLeastS()) {
            arrayOf(Manifest.permission.READ_PHONE_NUMBERS, Manifest.permission.READ_PHONE_STATE)
        } else {
            arrayOf(Manifest.permission.READ_PHONE_STATE)
        }
        val audioPermissions = arrayOf(Manifest.permission.RECORD_AUDIO)

        val permissions = notificationPermissions + phonePermissions + audioPermissions

        ActivityCompat.requestPermissions(activity, permissions, PERMISSIONS_REQUEST_CODE)
    }

    fun processPermissionResult(activity: AppCompatActivity, anchorView: View, requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            grantResults
                .zip(permissions)
                .filter { it.first == PackageManager.PERMISSION_DENIED }
                .map { it.second }
                .firstOrNull()
                ?.let { permission ->
                    when(permission) {
                        Manifest.permission.READ_PHONE_NUMBERS -> R.string.phone_numbers_permission_required
                        Manifest.permission.READ_PHONE_STATE -> R.string.phone_state_permission_required
                        Manifest.permission.RECORD_AUDIO -> R.string.audio_permission_required
                        else -> null
                    }?.let { message ->
                        Snackbar
                            .make(anchorView, message, Snackbar.LENGTH_LONG)
                            .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                                    super.onDismissed(transientBottomBar, event)
                                    activity.finish()
                                }
                            })
                            .also {
                                it.view.setBackgroundColor(Color.RED)
                                it.show()
                            }
                    }
                }
        }
    }

    private const val PERMISSIONS_REQUEST_CODE = 99
}