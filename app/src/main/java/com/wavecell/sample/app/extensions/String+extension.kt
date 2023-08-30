package com.wavecell.sample.app.extensions

import com.eght.voice.sdk.logging.LogLevel
import com.wavecell.sample.app.models.SampleAppLogLevel
import com.wavecell.sample.app.validation.InputValidation
import com.wavecell.sample.app.validation.InputValidator

fun String.toSampleAppLogLevel() = when (this) {
    LogLevel.ERROR.name -> SampleAppLogLevel.ERROR
    LogLevel.WARNING.name -> SampleAppLogLevel.WARNING
    LogLevel.INFO.name -> SampleAppLogLevel.INFO
    LogLevel.DEBUG.name -> SampleAppLogLevel.DEBUG
    LogLevel.VERBOSE.name -> SampleAppLogLevel.VERBOSE
    else -> SampleAppLogLevel.DEBUG
}

fun String.isValidDisplayName(): Boolean {
    return isValid(this.trim(), InputValidation.displayName)
}

fun String.isValidUserId(): Boolean {
    return isValid(this, InputValidation.userId)
}

fun String.isValidPhoneNumber(): Boolean {
    return isValid(this, InputValidation.phoneNumber)
}

fun String.isValidAccountId(): Boolean {
    return isValid(this, InputValidation.accountId)
}

private fun isValid(text: String, validator: InputValidator): Boolean {
    return text.length <= validator.maxLength && text.matches(validator.pattern)
}