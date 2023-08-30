package com.wavecell.sample.app.extensions

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wavecell.sample.app.R

fun AppCompatActivity.toast(message: String?, length: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, message, length).show()
}

val AppCompatActivity.screenHeight: Int
    get() {
        val metrics = DisplayMetrics()
        this.windowManager.defaultDisplay.getMetrics(metrics)
        return metrics.heightPixels
    }

fun AppCompatActivity.copyToClipboard(text: String) {
    val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboardManager.setPrimaryClip(ClipData.newPlainText(getString(R.string.app_name), text))
}