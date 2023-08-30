package com.wavecell.sample.app.extensions

import androidx.databinding.BindingAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

@BindingAdapter("showOrHide")
fun FloatingActionButton.showOrHide(shouldShow: Boolean) {
    if (shouldShow) show() else hide()
}

