package com.wavecell.sample.app

import com.wavecell.sample.app.log.AppComponentTag
import com.wavecell.sample.app.log.AppFeatureTag
import com.wavecell.sample.app.log.AppLog
import com.wavecell.sample.app.log.AppTag
import com.wavecell.sample.app.models.AuthRequestBody
import com.wavecell.sample.app.utils.SampleAppPreferences
import retrofit2.HttpException

class TokenRemoteDataSource(
    private val tokenApi: ApiInterface,
    private val sampleAppPreferences: SampleAppPreferences
) {
    suspend fun getToken(tokenUrl: String, userId: String, accountId: String): String {
        return try {
            val authRequestBody = AuthRequestBody(userId, accountId)
            val token = tokenApi.getAuthToken(tokenUrl, authRequestBody).token
            sampleAppPreferences.jwtToken = token
            token
        } catch (e: HttpException) {
            AppLog.e(TAG, "Failed to fetch auth token", e)
            ""
        }
    }

    companion object {
        private const val DEFAULT_TOKEN_EXPIRY = "4h"
        private val TAG = AppTag.make(AppFeatureTag.SAMPLE_APPLICATION, AppComponentTag.TOKEN_SET_AND_UPDATE, TokenRemoteDataSource::class.java)
    }
}