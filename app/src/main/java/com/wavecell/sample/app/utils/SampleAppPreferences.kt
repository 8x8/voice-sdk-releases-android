package com.wavecell.sample.app.utils

import android.content.SharedPreferences

class SampleAppPreferences constructor(
        sharedPreferences: SharedPreferences
): PreferenceDataUtil(sharedPreferences) {

    companion object {
        private const val KEY_USER_ID = "user id key"
        private const val KEY_USER_NAME = "user name key"
        private const val KEY_RECENT_CALL_ACCOUNT_ID = "recent_call_user_accountId_key"
        private const val JWT_TOKEN_KEY = "jwt_token_key"
        private const val IS_DEFAULT_RINGTONE_SELECTED = "is_default_ringtone_selected"
    }

    var userId: String
        get() = getString(KEY_USER_ID).orEmpty()
        set(value) { put(KEY_USER_ID, value) }

    var userName: String
        get() = getString(KEY_USER_NAME).orEmpty()
        set(value) { put(KEY_USER_NAME, value) }

    var recentCallAccountId: String
        get() = getString(KEY_RECENT_CALL_ACCOUNT_ID).orEmpty()
        set(value) { put(KEY_RECENT_CALL_ACCOUNT_ID, value) }

    var jwtToken: String
        get() = getString(JWT_TOKEN_KEY).orEmpty()
        set(value) { put(JWT_TOKEN_KEY, value) }

    var isRingtoneSet: Boolean
        get() = getBoolean(IS_DEFAULT_RINGTONE_SELECTED, false)
        set(value) { put(IS_DEFAULT_RINGTONE_SELECTED, value) }

}