package com.wavecell.sample.app.proximity

import android.content.Context
import android.os.PowerManager
import java.util.concurrent.TimeUnit

class ProximityLock(context: Context) {

    companion object {
        private const val PROXIMITY_SCREEN_WAKELOCK = "SAMPLE_APP:PROXIMITY_SCREEN_WAKE_LOCK"
        private const val WAKELOCK_FLAG_RELEASE_ALWAYS = 0
    }

    private val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    private val wakeLock = powerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK,
            PROXIMITY_SCREEN_WAKELOCK)

    fun enable() {
        if(!wakeLock.isHeld) {
            wakeLock.acquire(TimeUnit.MINUTES.toMillis(30))
        }
    }

    fun disable() {
        if (wakeLock.isHeld) {
            wakeLock.release(WAKELOCK_FLAG_RELEASE_ALWAYS)
        }
    }
}