package com.wavecell.sample.app

import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.eght.voice.sdk.Voice
import com.eght.voice.sdk.registration.domain.interactor.ResultWrapper
import com.wavecell.sample.app.constants.Constants
import com.wavecell.sample.app.log.AppComponentTag
import com.wavecell.sample.app.log.AppFeatureTag
import com.wavecell.sample.app.log.AppLog
import com.wavecell.sample.app.log.AppTag
import com.wavecell.sample.app.presentation.navigator.ToastNavigator
import javax.inject.Inject

class PushTokenRefreshHandler @Inject constructor (private val voice: Voice,
                                                   private val workManager: WorkManager,
                                                   private val toastNavigator: ToastNavigator) {
    private var shouldNotifyResult = false

    companion object {
        private val TAG = AppTag.make(AppFeatureTag.SAMPLE_APPLICATION, AppComponentTag.TOKEN_SET_AND_UPDATE, PushTokenRefreshHandler::class.java)
    }

    fun onPushTokenChanged(token: String) {
        if(voice.isActivated()) {
            AppLog.v(TAG, "On new token requested by SDK")
            when (val result = voice.updatePushToken(token)) {
                is ResultWrapper.Error -> {
                    toastNavigator.showPushTokenUpdateFailed()
                    AppLog.e(TAG, "${Constants.LOG_PREFIX}  updating push token failed", result.error)
                }
                is ResultWrapper.Success -> {
                    AppLog.i(TAG, "${Constants.LOG_PREFIX}  successfully updated push token, worker name to observe: ${result.value}")
                    shouldNotifyResult = true
                    observerWorker(result.value)
                }
            }
        }
    }

    private fun observerWorker(workerName: String) {
        workManager
            .getWorkInfosForUniqueWorkLiveData(workerName)
            .observeForever { info -> processWorkInfo(info) }
    }

    private fun processWorkInfo(info: List<WorkInfo>) {
        val result = info.firstOrNull()
        AppLog.i(TAG, "Checking work info $result, should notify: $shouldNotifyResult")
        if (shouldNotifyResult) {
            if (result?.state == WorkInfo.State.SUCCEEDED) {
                toastNavigator.showPushTokenUpdatedOnSDKSuccessfully()
                shouldNotifyResult = false
            } else if (result?.state == WorkInfo.State.FAILED) {
                toastNavigator.showPushTokenUpdateFailed()
                shouldNotifyResult = false

            }
        }
    }
}