package com.wavecell.sample.app.adapter.binding

import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout
import com.wavecell.sample.app.extensions.isValidAccountId
import com.wavecell.sample.app.extensions.isValidDisplayName
import com.wavecell.sample.app.extensions.isValidPhoneNumber
import com.wavecell.sample.app.extensions.isValidUserId
import com.wavecell.sample.app.validation.InputValidationType


@BindingAdapter(value = ["onTextChanged", "inputValidationType"], requireAll = true)
fun TextInputLayout.setOnTextChanged(text: String?, type: InputValidationType) {
    val input = text ?: ""
    val isValid = when(type) {
        InputValidationType.DISPLAY_NAME -> input.isValidDisplayName()
        InputValidationType.USER_ID -> input.isValidUserId()
        InputValidationType.PHONE_NUMBER -> input.isValidPhoneNumber()
        InputValidationType.ACCOUNT_ID -> input.isValidAccountId()
    }
    isErrorEnabled = !isValid
    error = if (!isValid) " " else ""
}