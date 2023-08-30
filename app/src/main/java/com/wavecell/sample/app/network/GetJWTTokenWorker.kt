package com.wavecell.sample.app.network

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import com.eght.voice.sdk.Voice
import com.eght.voice.sdk.registration.domain.interactor.ResultWrapper
import com.wavecell.sample.app.TokenRemoteDataSource
import com.wavecell.sample.app.WavecellApplication
import com.wavecell.sample.app.injection.components.SampleAppComponent
import com.wavecell.sample.app.log.AppComponentTag
import com.wavecell.sample.app.log.AppFeatureTag
import com.wavecell.sample.app.log.AppLog
import com.wavecell.sample.app.log.AppTag
import com.wavecell.sample.app.utils.SampleAppAccountPreferences
import com.wavecell.sample.app.utils.SampleAppPreferences
import javax.inject.Inject

class GetJWTTokenWorker(
    val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    @Inject
    lateinit var voice: Voice

    @Inject
    lateinit var tokenRemoteDataSource: TokenRemoteDataSource

    @Inject
    lateinit var sampleAppAccountPreferences: SampleAppAccountPreferences

    @Inject
    lateinit var sampleAppPreferences: SampleAppPreferences

    override suspend fun doWork(): Result {
        inject()

        val result = tokenRemoteDataSource.getToken(sampleAppAccountPreferences.tokenUrl, sampleAppPreferences.userId, sampleAppAccountPreferences.accountId)
        val data = Data.Builder()
        return if (result.isNotBlank()) {
            AppLog.i(TAG, "Token fetched successfully from Api")
            val tokenUpdatedOnSDK = updateToken(result)
            if(tokenUpdatedOnSDK) {
                AppLog.i(TAG, "Token update succeeded")
                data.putString(TOKEN_STATUS, TokenStatus.UPDATED.name)
                Result.success(data.build())
            } else {
                AppLog.w(TAG, "Token update failed")
                data.putString(TOKEN_STATUS, TokenStatus.UPDATE_FAILED.name)
                Result.failure(data.build())
            }
        } else {
            AppLog.w(TAG, "Token fetch failed")
            data.putString(TOKEN_STATUS, TokenStatus.FETCH_FAILED.name)
            Result.failure(data.build())
        }
    }

    private fun updateToken(token: String): Boolean {
        return when(voice.updateAuthenticationToken(token)) {
            is ResultWrapper.Error -> {
                AppLog.w(TAG, "Unable to update token in the SDK")
                false
            }
            is ResultWrapper.Success -> {
                AppLog.i(TAG, "Successfully update token in the SDK")
                true
            }
        }
    }

    private fun inject() {
        WavecellApplication.componentHolder.getComponent(SampleAppComponent::class.java, applicationContext)
            ?.inject(this)
    }

    companion object {
        private val TAG = AppTag.make(AppFeatureTag.SAMPLE_APPLICATION, AppComponentTag.TOKEN_SET_AND_UPDATE, GetJWTTokenWorker::class.java)
        const val TOKEN_STATUS = "tokenUpdated"
        fun workUniqueId(): String = GetJWTTokenWorker::class.java.simpleName
        fun createRequest(): OneTimeWorkRequest {
            val networkConstraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            return OneTimeWorkRequestBuilder<GetJWTTokenWorker>()
                .setConstraints(networkConstraints)
                .addTag(workUniqueId())
                .build()
        }
    }
}

enum class TokenStatus {
    UPDATED,
    FETCH_FAILED,
    UPDATE_FAILED
}
