package com.wavecell.sample.app.extensions

import android.view.View
import androidx.databinding.BindingAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior

@BindingAdapter("visibleOrGone")
fun View.visibleOrGone(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

@BindingAdapter("initRecentCallsBottomSheet")
fun View.initRecentCallsBottomSheet(state: Int) {
    val behavior: BottomSheetBehavior<View> = BottomSheetBehavior.from(this)
    behavior.state = state
}

@BindingAdapter("peek")
fun View.setPeekHeight(height: Int) {
    val behavior: BottomSheetBehavior<View> = BottomSheetBehavior.from(this)
    behavior.state = BottomSheetBehavior.STATE_EXPANDED
    behavior.peekHeight = height
}

@BindingAdapter("onLongClick")
fun View.setLongClick(listener: Runnable) {
    this.setOnLongClickListener {
        listener.run()
        true
    }
}

val View.yPosition: Int
    get() {
        val locations = IntArray(2)
        getLocationOnScreen(locations)
        return locations.last()
    }
