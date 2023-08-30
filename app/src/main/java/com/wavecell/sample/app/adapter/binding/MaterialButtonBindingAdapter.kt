package com.wavecell.sample.app.adapter.binding

import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.google.android.material.button.MaterialButton
import com.wavecell.sample.app.R
import com.wavecell.sample.app.extensions.isValidUserId

@BindingAdapter(value = ["validateUserId"], requireAll = false)
fun MaterialButton.areInputsValidToPlaceCall(userId: String?) {
    val isUserValid = userId?.isValidUserId() ?: false
    isEnabled = isUserValid

    val backColor = if (isUserValid) R.color.colorAccent else R.color.md_white_1000_20
    setBackgroundColor(ContextCompat.getColor(context, backColor))
}