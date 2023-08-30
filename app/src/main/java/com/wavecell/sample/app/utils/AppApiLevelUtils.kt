package com.wavecell.sample.app.utils

import android.os.Build

object AppApiLevelUtils {

    @JvmStatic
    fun hasAtLeastOreoMr1(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1

    @JvmStatic
    fun hasAtLeastS(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    @JvmStatic
    fun hasAtLeastT(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
}