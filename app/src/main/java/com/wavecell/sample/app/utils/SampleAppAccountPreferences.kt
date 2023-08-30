package com.wavecell.sample.app.utils

import android.content.SharedPreferences
import com.wavecell.sample.app.constants.AuthConstants

class SampleAppAccountPreferences constructor(
        sharedPreferences: SharedPreferences
): PreferenceDataUtil(sharedPreferences) {

    companion object {
        private const val KEY_ACCOUNT_ID = "user accountId key"
        private const val KEY_SERVICE_URL = "service url key"
        private const val KEY_TOKEN_URL = "token url key"
    }

    var accountId: String
        get() = getString(KEY_ACCOUNT_ID).orEmpty()
        set(value) { put(KEY_ACCOUNT_ID, value) }

    var serviceUrl: String
        get() = getString(KEY_SERVICE_URL, AuthConstants.SERVICE_URL).orEmpty()
        set(value) { put(KEY_SERVICE_URL, value) }

    var tokenUrl: String
        get() = getString(KEY_TOKEN_URL, AuthConstants.TOKEN_URL).orEmpty()
        set(value) { put(KEY_TOKEN_URL, value) }
}