package com.wavecell.sample.app.extensions

import android.widget.EditText
import androidx.databinding.BindingAdapter

@BindingAdapter("shouldRequestFocus")
fun EditText.onRequestFocus(shouldFocus: Boolean) {
    if (shouldFocus) requestFocus() else clearFocus()
}