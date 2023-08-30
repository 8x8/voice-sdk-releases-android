package com.wavecell.sample.app.extensions

import android.os.SystemClock
import android.widget.Chronometer
import androidx.databinding.BindingAdapter

@BindingAdapter("chrono")
fun Chronometer.setChrono(startTime: Long) {
    if (startTime > 0L) {
        stop()
        base = (startTime * 1000) - (System.currentTimeMillis() - SystemClock.elapsedRealtime())
        start()
    }
}