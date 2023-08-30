package com.wavecell.sample.app.presentation.model.factory

import androidx.work.ExistingWorkPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.eght.voice.sdk.Voice
import com.wavecell.sample.app.log.AppComponentTag
import com.wavecell.sample.app.log.AppFeatureTag
import com.wavecell.sample.app.log.AppLog
import com.wavecell.sample.app.log.AppTag
import com.wavecell.sample.app.network.GetJWTTokenWorker
import com.wavecell.sample.app.network.TokenStatus
import com.wavecell.sample.app.presentation.navigator.ToastNavigator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class TokenRefreshHandler(private val workManager: WorkManager, private val toastNavigator: ToastNavigator, coroutineDispatcher: CoroutineDispatcher) {
    private val scope = CoroutineScope(coroutineDispatcher)
    private var tokenRequestJob: Job? = null
    private var shouldNotifyResult = false

    companion object {
        private val TAG = AppTag.make(AppFeatureTag.SAMPLE_APPLICATION, AppComponentTag.TOKEN_SET_AND_UPDATE, TokenRefreshHandler::class.java)
    }
    init {
        workManager
            .getWorkInfosForUniqueWorkLiveData(GetJWTTokenWorker.workUniqueId())
            .observeForever { info -> processWorkInfo(info) }
    }
    fun startTokenRefreshEventCollection(voice: Voice) {
        tokenRequestJob?.let {
            AppLog.i(TAG, "Collection of token expiration events has already started")
        } ?: run {
            AppLog.i(TAG, "Starting collection of token expiration events")
            tokenRequestJob = scope.launch {
                voice.tokenExpirationEvents.collect { onTokenRequested() }
            }.also { job ->
                job.invokeOnCompletion { error ->
                    error?.let {
                        AppLog.e(TAG, "Token expiration job completed exceptionally", it)
                    } ?: run {
                        AppLog.i(TAG, "Token expiration job completed normally")
                    }
                }
            }
        }
    }

    fun onTokenRequested() {
        AppLog.v(TAG, "On new token requested by SDK")
        shouldNotifyResult = true
        toastNavigator.showInvalidTokenError()
        workManager.enqueueUniqueWork(
            GetJWTTokenWorker.workUniqueId(),
            ExistingWorkPolicy.KEEP,
            GetJWTTokenWorker.createRequest()
        )
    }

    private fun processWorkInfo(info: List<WorkInfo>) {
        val result = info.firstOrNull()
        val data = result?.outputData
        val tokenUpdated = data?.getString(GetJWTTokenWorker.TOKEN_STATUS)
        AppLog.i(TAG, "Checking work info $result, should notify: $shouldNotifyResult")
        if (shouldNotifyResult) {
            if (result?.state == WorkInfo.State.SUCCEEDED && tokenUpdated == TokenStatus.UPDATED.name) {
                toastNavigator.showTokenUpdatedOnSDKSuccessfully()
                shouldNotifyResult = false
            } else if (result?.state == WorkInfo.State.FAILED) {
                if (tokenUpdated == TokenStatus.FETCH_FAILED.name) {
                    shouldNotifyResult = false
                    toastNavigator.showTokenRefreshFailed()
                } else {
                    toastNavigator.showTokenUpdateFailed()
                    shouldNotifyResult = false
                }
            }
        }
    }
}