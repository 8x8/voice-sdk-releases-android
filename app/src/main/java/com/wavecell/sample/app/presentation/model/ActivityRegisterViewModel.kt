package com.wavecell.sample.app.presentation.model

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import com.eght.voice.sdk.Voice
import com.eght.voice.sdk.configuration.Configuration
import com.eght.voice.sdk.configuration.SessionConfiguration
import com.eght.voice.sdk.configuration.UserConfiguration
import com.eght.voice.sdk.configuration.configuration
import com.eght.voice.sdk.configuration.sessionConfiguration
import com.eght.voice.sdk.configuration.userConfiguration
import com.eght.voice.sdk.registration.exception.RegistrationException
import com.wavecell.sample.app.BuildConfig
import com.wavecell.sample.app.GetTokenUseCase
import com.wavecell.sample.app.R
import com.wavecell.sample.app.ResultWrapper
import com.wavecell.sample.app.WavecellApplication
import com.wavecell.sample.app.constants.Constants.LOG_PREFIX
import com.wavecell.sample.app.log.AppComponentTag
import com.wavecell.sample.app.log.AppFeatureTag
import com.wavecell.sample.app.log.AppLog
import com.wavecell.sample.app.log.AppTag
import com.wavecell.sample.app.log.TOKEN_SET_AND_UPDATE_TAG
import com.wavecell.sample.app.models.VoiceAccount
import com.wavecell.sample.app.presentation.callback.VoiceAccountSelectionListener
import com.wavecell.sample.app.presentation.event.RegisterEvent
import com.wavecell.sample.app.utils.SampleAppAccountPreferences
import com.wavecell.sample.app.utils.SampleAppPreferences
import kotlinx.coroutines.launch

class ActivityRegisterViewModel(
    private val voice: Voice,
    private val sampleAppPreferences: SampleAppPreferences,
    private val sampleAppAccountPreferences: SampleAppAccountPreferences,
    private val getTokenUseCase: GetTokenUseCase
): ViewModel(),
        VoiceAccountSelectionListener,
        DefaultLifecycleObserver {

    companion object {
        private val TAG = AppTag.make(AppFeatureTag.SAMPLE_APPLICATION, AppComponentTag.ACCOUNT_INFO, ActivityRegisterViewModel::class.java)
    }

    /**
     * Observables
     */

    private val account = ObservableField(VoiceAccount(sampleAppAccountPreferences.accountId, sampleAppAccountPreferences.serviceUrl, sampleAppAccountPreferences.tokenUrl))
    val userId = ObservableField<String>()
    val userName = ObservableField<String>()
    val isRegistering = ObservableBoolean()


    /**
     * Read-only
     */

    private val uniqueDeviceId: String
        get() = WavecellApplication.instanceId


    /**
     * Live Data
     */

    private var _event = MutableLiveData<RegisterEvent>()
    val event: LiveData<RegisterEvent>
        get() = _event


    /**
     * Lifecycle
     */

    override fun onCreate(owner: LifecycleOwner) {
        super.onStart(owner)
        observeVoiceState(owner)
    }

    private fun observeVoiceState(owner: LifecycleOwner) {
        viewModelScope.launch {
            owner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                voice.state.collect { state ->
                    AppLog.v(TAG, "---> [state] state changed to: $state")
                }
            }
        }
    }

    /**
     * Click Listeners
     */

    fun onRegister() {
        val username = userName.get() ?: run {
            AppLog.i(TAG, "$LOG_PREFIX Set user name")
            _event.value = RegisterEvent.UserNameNotFound
            return
        }

        val uid = userId.get() ?: run {
            AppLog.i(TAG, "$LOG_PREFIX Set user id")
            _event.value = RegisterEvent.UserIdNotFound
            return
        }

        account.get()?.let { accountData ->
            if(isAccountDataSet(accountData)) {
                val sessionConfiguration = getSessionConfiguration(accountData)
                val userConfiguration = getUserConfiguration(accountData, uid, username)
                getTokenAndActivate(accountData, userConfiguration, sessionConfiguration)
                AppLog.v(TAG, "AccountId : ${accountData.accountId} Service URL: ${accountData.serviceUrl} Token URL: ${accountData.tokenUrl}")
            } else {
               onAccountDataNotSet()
            }
        } ?: run {
            onAccountDataNotSet()
        }
    }

    private fun onAccountDataNotSet() {
        AppLog.i(TAG, "$LOG_PREFIX Select account to proceed registration")
        _event.value = RegisterEvent.AccountNotSelected
        return
    }

    private fun isAccountDataSet(voiceAccount: VoiceAccount): Boolean {
        return voiceAccount.accountId.isNotBlank() && voiceAccount.tokenUrl.isNotBlank() && voiceAccount.serviceUrl.isNotBlank()
    }

    private fun getTokenAndActivate(account: VoiceAccount, userConfiguration: UserConfiguration, sessionConfiguration: SessionConfiguration) {
        isRegistering.set(true)

        viewModelScope.launch {
            when (val result = getTokenUseCase.execute(account.tokenUrl, userConfiguration.userId, userConfiguration.accountId)) {
                is ResultWrapper.Error -> AppLog.w(TAG, "$TOKEN_SET_AND_UPDATE_TAG Failed to get token", result.error)
                is ResultWrapper.Success -> {
                    val token = result.value
                    userConfiguration.jwtToken  = token
                    try {
                        voice.activate(getConfiguration(userConfiguration, sessionConfiguration))
                        saveUserDataToPreference(userConfiguration)
                        AppLog.i(TAG, "$LOG_PREFIX Sample app Registration successful")
                        _event.value = RegisterEvent.NavigationToManActivity
                    } catch(e: RegistrationException) {
                        _event.value = RegisterEvent.RegistrationFailed(e)
                        AppLog.e(TAG, "$LOG_PREFIX Sample app Registration failed", e)
                    }
                    finally {
                        isRegistering.set(false)
                    }
                }
            }
        }
    }

    fun onAppInfo() {
        _event.value = RegisterEvent.AppInfo
    }


    /**
     * Voice Account Selection Listener Implementation
     */

    override fun onAccountSelected(account: VoiceAccount) {
        saveAccountDataToPreference(account)
        this.account.set(account)
    }

    private fun saveAccountDataToPreference(account: VoiceAccount) {
        sampleAppAccountPreferences.accountId = account.accountId
        sampleAppAccountPreferences.serviceUrl = account.serviceUrl
        sampleAppAccountPreferences.tokenUrl = account.tokenUrl
    }


    /**
     * Helpers
     */

    private fun saveUserDataToPreference(userConfiguration: UserConfiguration) {
        sampleAppPreferences.userId = userConfiguration.userId
        sampleAppPreferences.userName = userConfiguration.displayName
    }

    private fun getUserConfiguration(account: VoiceAccount,
                                     uid: String,
                                     name: String): UserConfiguration =
            userConfiguration {
                accountId = account.accountId
                userId = uid
                msisdn = ""
                jwtToken = ""
                displayName = name
                deviceId = uniqueDeviceId
            }

    private fun getSessionConfiguration(account: VoiceAccount): SessionConfiguration =
            sessionConfiguration {
                applicationId = BuildConfig.APPLICATION_ID
                pushToken = WavecellApplication.pnToken
                baseUrl = account.serviceUrl
                ringtoneResourceId = R.raw.cheesecake_loop_ringtone
            }

    private fun getConfiguration(userConfig: UserConfiguration,
                                 sessionConfig: SessionConfiguration): Configuration =
            configuration {
                userConfiguration = userConfig
                sessionConfiguration = sessionConfig
            }

}