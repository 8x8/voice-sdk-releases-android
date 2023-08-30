package com.wavecell.sample.app.presentation.model

import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import com.eght.voice.sdk.Voice
import com.eght.voice.sdk.model.InboundCallPath
import com.eght.voice.sdk.model.VoiceCall
import com.eght.voice.sdk.registration.domain.interactor.ResultWrapper
import com.eght.voice.sdk.registration.exception.RegistrationException
import com.wavecell.sample.app.R
import com.wavecell.sample.app.TokenVerifier
import com.wavecell.sample.app.adapter.RecentRecycleAdapter
import com.wavecell.sample.app.constants.Constants.LOG_PREFIX
import com.wavecell.sample.app.constants.Constants.RINGTONE_ITEMS
import com.wavecell.sample.app.custom.bottomsheets.listener.BottomSheetStateListener
import com.wavecell.sample.app.custom.bottomsheets.state.BottomSheetState
import com.wavecell.sample.app.custom.dialog.DialogInput
import com.wavecell.sample.app.custom.items.SettingsMenuItem
import com.wavecell.sample.app.extensions.index
import com.wavecell.sample.app.extensions.title
import com.wavecell.sample.app.log.AppComponentTag
import com.wavecell.sample.app.log.AppFeatureTag
import com.wavecell.sample.app.log.AppLog
import com.wavecell.sample.app.log.AppTag
import com.wavecell.sample.app.log.FileLogWriter
import com.wavecell.sample.app.models.CallList
import com.wavecell.sample.app.presentation.event.MainEvent
import com.wavecell.sample.app.presentation.event.MainToastMessage
import com.wavecell.sample.app.presentation.model.factory.TokenRefreshHandler
import com.wavecell.sample.app.utils.DeviceUtils
import com.wavecell.sample.app.utils.LifecycleManager
import com.wavecell.sample.app.utils.SampleAppPreferences
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

class ActivityMainViewModel @Inject constructor(
    private val voice: Voice,
    private val logWriter: FileLogWriter,
    private val lifecycleManager: LifecycleManager,
    private val tokenNeedsRefresh: TokenVerifier,
    private val tokenRefreshHandler: TokenRefreshHandler,
    private val sampleAppPreferences: SampleAppPreferences
): ViewModel(),
        DefaultLifecycleObserver,
        BottomSheetStateListener,
        RecentRecycleAdapter.ClickListener,
        SettingsMenuItem.ClickListener,
        DialogInput.ClickListener {

    companion object {
        private val TAG = AppTag.make(AppFeatureTag.SAMPLE_APPLICATION, AppComponentTag.UI, ActivityMainViewModel::class.java)
    }

    private val callStateJobs = mutableMapOf<UUID, Job>()

    /**
     * Properties
     */

    var isProfileBottomSheetVisible = ObservableBoolean(false)
    var isPlaceCallFabVisible = ObservableBoolean(true)
    val userId = ObservableField<String>()
    val ringtone = ObservableField<String>()
    var ongoingCall = ObservableField<VoiceCall>()
    val inboundCallPath = ObservableField<String>()
    val recentCallViewModelList = ObservableField<List<RowRecentCallViewModel>>(ArrayList())
    val recentCallPeekHeight = ObservableInt()
    val appInfo = ObservableField<String>()
    val sdkVersion = voice.sdkVersion
    val displayName = ObservableField<String>()
    val phoneNumber = ObservableField<String>()
    val accountId = ObservableField<String>()
    val serviceUrl = ObservableField<String>()
    val tokenUrl = ObservableField<String>()

    /**
     * Live Data
     */

    private var _event = MutableLiveData<MainEvent>()
    val event: LiveData<MainEvent> get() = _event


    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        lifecycleManager.onLogin()
        AppLog.d(TAG, "---> registration: ${voice.registrationLog}")

        observeVoiceState(owner)
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        checkIfUserIsLoggedIn()
        listenToProfileVisibility()
        loadPhoneNumber()
        loadRingtoneOption()
        loadInboundCallPath()
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        setCallList()
        val voiceCall = voice.voiceCall
        AppLog.v(TAG, "---> [call] (resume) is call incoming ${voiceCall?.isIncoming}")
        initVoiceCall(voiceCall)
        if(voice.isActivated() && tokenNeedsRefresh.isTokenExpired()) {
            tokenRefreshHandler.onTokenRequested()
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        callStateJobs.values.forEach { it.cancel() }
        callStateJobs.clear()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        lifecycleManager.onLogout()
        voice.onDestroy()
    }

    /**
     * Init
     */

    init {
        loadAppInfo()
    }

    private fun initVoiceCall(voiceCall: VoiceCall?) {
        voiceCall?.let {
            if (it.isOngoing || it.isPeerOnHold) {
                ongoingCall.set(it)
            }
            callStateJobs[it.uuid]?.cancel()
            callStateJobs[it.uuid] = viewModelScope.launch {
                voice
                    .voiceCallStateForId(it.uuid, viewModelScope)
                    .collect { voiceCallState ->
                        onCallUpdated(voiceCallState.call)
                    }
            }
        } ?: run {
            ongoingCall.set(null)
        }
    }


    private fun checkIfUserIsLoggedIn() {
        if (!voice.isActivated()) {
            _event.value = MainEvent.StartRegistrationActivity
        }
    }

    private fun listenToProfileVisibility() {
        isProfileBottomSheetVisible.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                val isVisible = isProfileBottomSheetVisible.get()
                if (isVisible) {
                    displayName.notifyChange()
                }
                _event.value = MainEvent.ShowProfileBottomSheet(isVisible)
            }
        })
    }

    private fun loadAppInfo() {
        appInfo.set(DeviceUtils.APP_VERSION_INFO)
    }

    private fun loadPhoneNumber() {
        phoneNumber.set(voice.phoneNumber)
    }

    private fun loadRingtoneOption() {
        val ringtoneName = if(sampleAppPreferences.isRingtoneSet) {
            when (voice.ringtoneResourceId) {
                R.raw.upbeat_loop_ringtone -> RINGTONE_ITEMS[1]
                R.raw.cheesecake_loop_ringtone -> RINGTONE_ITEMS[2]
                else -> RINGTONE_ITEMS[0]
            }
        } else {
            RINGTONE_ITEMS[0]
        }
        ringtone.set(ringtoneName)
    }

    private fun loadInboundCallPath() {
        inboundCallPath.set(voice.inboundCallPath.title)
    }

    private fun observeVoiceState(owner: LifecycleOwner) {
        viewModelScope.launch {
            owner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                voice.state.collect { state ->
                    AppLog.d(TAG, "---> [state] state changed to: $state")
                }
            }
        }
    }


    /**
     * Click Listeners
     */

    private fun onHidPlaceCallBottomSheet() {
        isPlaceCallFabVisible.set(true)
        _event.value = MainEvent.HideKeyboard
    }

    fun onCopyToClipboard(text: String) {
        _event.value = MainEvent.CopyToClipboard(text)
    }

    fun showOpenSourceLicenses() {
        _event.value = MainEvent.ShowOpenSourceLicenses
    }

    /**
     * Voice
     */

    private fun setCallList() {
        recentCallViewModelList.set(CallList.list)
    }

    fun deactivateWavecell() {
        AppLog.d(TAG, "$LOG_PREFIX [deactivate] deactivating voice")
        CallList.clearCallLogs()
        viewModelScope.launch {
            try {
                voice.deactivate()
                AppLog.d(TAG, "$LOG_PREFIX  [deactivate] success")
            } catch (e: RegistrationException) {
                AppLog.e(TAG, "$LOG_PREFIX  [deactivate] error", e)
            } finally {
                lifecycleManager.onLogout()
            }
        }

        _event.value = MainEvent.StartRegistrationActivity
    }

    private fun updateUserName(name: String) {
        if (name.isBlank()) {
            _event.value = MainEvent.ShowToast(MainToastMessage.PROVIDE_USER_NAME)
            return
        }

        AppLog.d(TAG, "---> changing name to: $name")
        when (val result = voice.updateDisplayName(name)) {
            is ResultWrapper.Error -> {
                AppLog.e(TAG, "name not updated", result.error)
                _event.value = MainEvent.ShowToast(MainToastMessage.USER_NAME_FAILED)
            }
            is ResultWrapper.Success -> {
                AppLog.i(TAG, "name updated")
                _event.value = MainEvent.ShowToast(MainToastMessage.USER_NAME_SAVED)
            }
        }
        displayName.set(name)
        _event.value = MainEvent.SaveUserName(name)
    }

    private fun updatePhoneNumber(number: String) {
        AppLog.d(TAG, "---> changing phone number to: $number")
        viewModelScope.launch {
            when (val result = voice.updatePhoneNumber(number)) {
                is ResultWrapper.Error -> {
                    AppLog.e(TAG, "---> phone number not updated", result.error)
                    _event.value = MainEvent.ShowToast(MainToastMessage.PHONE_NUMBER_FAILED)

                }
                is ResultWrapper.Success -> {
                    AppLog.i(TAG, "---> phone number updated")
                    phoneNumber.set(number)
                    _event.value = MainEvent.ShowToast(MainToastMessage.PHONE_NUMBER_SAVED)
                }
            }

        }
    }

    fun updateInboundCallPath(index: Int, item: String) {
        inboundCallPath.set(item)
        val path = when (index) {
            0 -> InboundCallPath.VOIP
            1 -> InboundCallPath.PSTN
            else -> InboundCallPath.VOIP_PSTN_FALLBACK
        }

        viewModelScope.launch {
            when(val result = voice.updateInboundCallPath(path)) {
                is ResultWrapper.Error -> {
                    AppLog.e(TAG, "---> phone number not updated", result.error)
                    _event.value = MainEvent.ShowToast(MainToastMessage.INBOUND_CALL_PATH_FAILED)

                }
                is ResultWrapper.Success -> {
                    AppLog.i(TAG, "---> phone number updated")
                    _event.value = MainEvent.ShowToast(MainToastMessage.INBOUND_CALL_PATH_SAVED)
                }
            }
        }
        _event.value = MainEvent.ShowToast(MainToastMessage.INBOUND_CALL_PATH_SAVED)
    }

    fun updateRingtone(index: Int, item: String) {
        val ringtoneResourceId = when (index) {
            1 -> R.raw.upbeat_loop_ringtone
            2 -> R.raw.cheesecake_loop_ringtone
            else -> -1
        }
        sampleAppPreferences.isRingtoneSet = (ringtoneResourceId != -1)
        voice.ringtoneResourceId = ringtoneResourceId
        ringtone.set(item)
        _event.value = MainEvent.ShowToast(MainToastMessage.RINGTONE_PATH_SAVED)
    }

    fun onShareLogs() {
        viewModelScope.launch {
            logWriter.zipFile()?.file?.let { file ->
                _event.value = MainEvent.StartShareFileAction(file.absoluteFile)
            } ?: run {
                AppLog.e(TAG, "---> (onLogs) failed to create the zip file")
            }
        }
    }


    private fun onCallUpdated(call: VoiceCall) {
        AppLog.v(TAG, "---> [call] (onCallUpdated) call: ${call.displayName}")
        if (call.isDisconnected || call.isFailed) {
            if (call.isFailed) {
                _event.value = MainEvent.ShowToast(MainToastMessage.CALL_FAILED)
            }
            ongoingCall.set(null)
            setCallList()
        } else if (call.isOngoing) {
            ongoingCall.set(call)
        } else {
            ongoingCall.notifyChange()
        }
    }



    /**
     * Settings Menu Items
     */

    private fun onShowUserNameOption() {
        val name = displayName.get().orEmpty()
        _event.value = MainEvent.ShowNameDialog(name)
    }

    private fun onShowPhoneNumberOption() {
        val number = phoneNumber.get().orEmpty()
        _event.value = MainEvent.ShowPhoneNumberDialog(number)
    }

    private fun onShowRingtoneOptions() {
        val options = RINGTONE_ITEMS
        val index = if(sampleAppPreferences.isRingtoneSet) {
            when(voice.ringtoneResourceId) {
                R.raw.upbeat_loop_ringtone -> 1
                R.raw.cheesecake_loop_ringtone -> 2
                else -> 0
            }
        } else {
            0
        }
        _event.value = MainEvent.ShowRingtoneDialog(options, index)
    }

    private fun onShowInboundCallPathOptions() {
        val options = InboundCallPath.values().map { it.title }.toTypedArray()
        _event.value = MainEvent.ShowInboundCallPathDialog(options, voice.inboundCallPath.index)
    }


    /**
     * Bottom Sheet State Listener Implementation
     */

    override fun onStateChanged(state: BottomSheetState) {
        if (state == BottomSheetState.COLLAPSED) {
            onHidPlaceCallBottomSheet()
        }
    }


    /**
     * Recent Recycler Adapter On Click Listener Implementation
     */

    override fun onClick(userId: String) {
        isPlaceCallFabVisible.set(false)
        _event.value = MainEvent.ShowPlaceCallBottomSheet(userId)
    }


    /**
     * Settings Menu Item Click Listener Implementation
     */

    override fun onClick(item: SettingsMenuItem.Item) {
        when(item) {
            SettingsMenuItem.Item.NAME -> onShowUserNameOption()
            SettingsMenuItem.Item.PHONE_NUMBER -> onShowPhoneNumberOption()
            SettingsMenuItem.Item.RINGTONE -> onShowRingtoneOptions()
            SettingsMenuItem.Item.INBOUND_CALL_PATH -> onShowInboundCallPathOptions()
            else -> { }
        }
    }


    /**
     * Dialog Input Click Listener Implementation
     */

    override fun onPositive(option: DialogInput.Option, input: String) {
        when(option) {
            DialogInput.Option.NAME -> updateUserName(input)
            DialogInput.Option.PHONE_NUMBER -> updatePhoneNumber(input)
        }
    }
}
