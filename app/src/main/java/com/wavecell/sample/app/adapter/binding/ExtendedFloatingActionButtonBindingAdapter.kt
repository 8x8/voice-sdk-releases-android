package com.wavecell.sample.app.adapter.binding

import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.wavecell.sample.app.R
import com.wavecell.sample.app.extensions.isValidDisplayName
import com.wavecell.sample.app.extensions.isValidUserId

@BindingAdapter(value = ["validateDisplayName", "validateUserId", "isRegistering"], requireAll = false)
fun ExtendedFloatingActionButton.areInputsValid(displayName: String?, userId: String?, isRegistering: Boolean?) {
    val isDisplayNameValid = displayName?.isValidDisplayName() ?: false
    val isUserValid = userId?.isValidUserId() ?: false
    val registering = isRegistering ?: true
    val allValid = isDisplayNameValid && isUserValid && !registering
    isEnabled = allValid

    val backColor = if (allValid) R.color.md_white_1000 else R.color.md_white_1000_20
    setBackgroundColor(ContextCompat.getColor(context, backColor))
}