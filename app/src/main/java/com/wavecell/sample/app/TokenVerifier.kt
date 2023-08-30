package com.wavecell.sample.app

import com.auth0.android.jwt.JWT
import com.wavecell.sample.app.log.AppComponentTag
import com.wavecell.sample.app.log.AppFeatureTag
import com.wavecell.sample.app.log.AppLog
import com.wavecell.sample.app.log.AppTag
import com.wavecell.sample.app.utils.SampleAppPreferences
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TokenVerifier(private val sampleAppPreferences: SampleAppPreferences) {

    companion object {
        private val TAG = AppTag.make(AppFeatureTag.SAMPLE_APPLICATION, AppComponentTag.TOKEN_SET_AND_UPDATE, TokenVerifier::class.java)
    }
    fun isTokenExpired() : Boolean {
        val jwt = JWT(sampleAppPreferences.jwtToken)
        val tokenIsConsideredExpired = jwt.isExpired(0)
        AppLog.v(TAG, "Token expiration time ${getDate(jwt.expiresAt?.time)}, current time ${getDate(System.currentTimeMillis())}, token is considered expired $tokenIsConsideredExpired")
        return tokenIsConsideredExpired
    }

    private fun getDate(timeInMilliseconds: Long?): String {
        return timeInMilliseconds?.let {
            val formatter = SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS", Locale.US)
            val calendar: Calendar = Calendar.getInstance()
            calendar.timeInMillis = timeInMilliseconds
            formatter.format(calendar.time)
        } ?: ""
    }
}