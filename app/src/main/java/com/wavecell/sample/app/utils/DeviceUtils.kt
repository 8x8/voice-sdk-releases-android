package com.wavecell.sample.app.utils

import android.os.Build
import com.wavecell.sample.app.BuildConfig

object DeviceUtils {

    val OPERATING_SYSTEM: String
        get() = "Android " + Build.VERSION.SDK_INT

    val DEVICE_MODEL: String
        get() = "${Build.MANUFACTURER} ${Build.MODEL}"

    val APP_VERSION: String
        get() = BuildConfig.VERSION_NAME

    val APPLICATION_ID: String
        get() = BuildConfig.APPLICATION_ID

    val APP_VERSION_INFO: String
        get() = StringBuilder("version: ")
                .append(BuildConfig.VERSION_NAME)
                .append(" (")
                .append(BuildConfig.VERSION_CODE)
                .append(")")
                .toString()

}