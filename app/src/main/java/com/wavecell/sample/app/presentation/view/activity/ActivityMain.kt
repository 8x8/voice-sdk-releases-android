package com.wavecell.sample.app.presentation.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.ViewTreeObserver
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.eght.voice.sdk.Voice
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.wavecell.sample.app.R
import com.wavecell.sample.app.WavecellApplication
import com.wavecell.sample.app.custom.bottomsheets.ProfileBottomSheet
import com.wavecell.sample.app.databinding.ActivityMainBinding
import com.wavecell.sample.app.extensions.copyToClipboard
import com.wavecell.sample.app.extensions.hideKeyboard
import com.wavecell.sample.app.extensions.screenHeight
import com.wavecell.sample.app.extensions.toast
import com.wavecell.sample.app.extensions.yPosition
import com.wavecell.sample.app.injection.components.MainComponent
import com.wavecell.sample.app.presentation.event.MainEvent
import com.wavecell.sample.app.presentation.event.MainToastMessage
import com.wavecell.sample.app.presentation.model.ActivityMainViewModel
import com.wavecell.sample.app.presentation.model.factory.ActivityMainViewModelFactory
import com.wavecell.sample.app.presentation.model.factory.TokenRefreshHandler
import com.wavecell.sample.app.presentation.navigator.Navigator
import com.wavecell.sample.app.utils.SampleAppAccountPreferences
import com.wavecell.sample.app.utils.SampleAppPreferences
import javax.inject.Inject

class ActivityMain : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ActivityMainViewModelFactory

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var sampleAppPreferences: SampleAppPreferences

    @Inject
    lateinit var sampleAppAccountPreferences: SampleAppAccountPreferences

    @Inject
    lateinit var voice: Voice

    @Inject
    lateinit var tokenRefreshHandler: TokenRefreshHandler

    private val viewModel: ActivityMainViewModel by viewModels { viewModelFactory }

    private lateinit var binding: ActivityMainBinding
    private lateinit var profileBottomSheet: ProfileBottomSheet
    private var listener:  ViewTreeObserver.OnGlobalLayoutListener? = null


    /**
     * Lifecycle
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(viewModel)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel
        binding.bottomAppBar = binding.bottomBar
        binding.navigator = navigator

        setupProfileBottomSheet()
        setupRecentCallsPeekHeight()
        observeEvent()

        PermissionProcessor.ensurePermissions(this)
    }

    override fun onStart() {
        super.onStart()
        setupUserInfo()
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(viewModel)
        clearComponent()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionProcessor.processPermissionResult(this, binding.container, requestCode, permissions, grantResults)
    }

    /**
     * Injection
     */

    private fun inject() {
        WavecellApplication.componentHolder.getComponent(MainComponent::class.java, this).inject(this)
    }

    private fun clearComponent() {
        WavecellApplication.componentHolder.clearComponent(MainComponent::class.java)
    }


    /**
     * Setups
     */

    private fun setupUserInfo() {
        viewModel.userId.set(sampleAppPreferences.userId)
        viewModel.displayName.set(sampleAppPreferences.userName)
        viewModel.accountId.set(sampleAppAccountPreferences.accountId)
        viewModel.serviceUrl.set(sampleAppAccountPreferences.serviceUrl)
        viewModel.tokenUrl.set(sampleAppAccountPreferences.tokenUrl)
    }

    private fun setupProfileBottomSheet() {
        profileBottomSheet = ProfileBottomSheet(binding.bottomSheetProfile.root)
    }

    private fun setupRecentCallsPeekHeight() {
        listener = ViewTreeObserver.OnGlobalLayoutListener {
            val padding = 48
            val viewHeight = binding.userIdTv.height
            val viewYPosition = binding.userIdTv.yPosition
            val peek = screenHeight-viewYPosition-viewHeight-padding
            viewModel.recentCallPeekHeight.set(peek)
            binding.root.viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }

        binding.root.viewTreeObserver.addOnGlobalLayoutListener(listener)
    }

    /**
     * Live Data Observers
     */

    private fun observeEvent() {
        viewModel.event.observe(this) { event ->
            when (event) {
                is MainEvent.StartRegistrationActivity -> {
                    sampleAppPreferences.clear()
                    navigator.startRegistrationActivity()
                }
                is MainEvent.StartShareFileAction -> navigator.startShareFileAction(event.file)
                is MainEvent.FinishActivity -> finish()
                is MainEvent.HideKeyboard -> hideKeyboard(currentFocus)
                is MainEvent.CopyToClipboard -> copyToClipboard(event.text)
                is MainEvent.SaveUserName -> saveUserName(event.name)

                is MainEvent.ShowProfileBottomSheet -> showProfileBottomSheet(event.isVisible)
                is MainEvent.ShowPlaceCallBottomSheet -> navigator.showPlaceCallBottomSheet(viewModel, event.userId)
                is MainEvent.ShowToast -> showToast(event.toastMessage)
                is MainEvent.ShowNameDialog -> showUserNameDialog(event.name)
                is MainEvent.ShowPhoneNumberDialog -> showPhoneNumberDialog(event.phoneNumber)
                is MainEvent.ShowRingtoneDialog -> showRingtoneDialog(event.items, event.index)
                is MainEvent.ShowInboundCallPathDialog -> showInboundCallPathDialog(event.items, event.index)
                is MainEvent.ShowOpenSourceLicenses -> startActivity(Intent(this, OssLicensesMenuActivity::class.java))
            }
        }
    }


    /**
     * Helpers
     */

    private fun saveUserName(name: String) {
        sampleAppPreferences.userName = name.trim()
    }

    private fun showToast(toastMessage: MainToastMessage) {
        val message = when(toastMessage) {
            MainToastMessage.CALL_FAILED -> getString(R.string.call_failed)
            MainToastMessage.PROVIDE_USER_NAME -> getString(R.string.user_name_cannot_be_empty)
            MainToastMessage.USER_NAME_SAVED -> getString(R.string.user_name_saved)
            MainToastMessage.USER_NAME_FAILED -> getString(R.string.user_name_failed)
            MainToastMessage.PHONE_NUMBER_SAVED -> getString(R.string.phone_number_saved)
            MainToastMessage.PHONE_NUMBER_FAILED -> getString(R.string.phone_number_failed)
            MainToastMessage.RINGTONE_PATH_SAVED -> getString(R.string.ringtone_saved)
            MainToastMessage.INBOUND_CALL_PATH_SAVED -> getString(R.string.inbound_call_path_saved)
            MainToastMessage.INBOUND_CALL_PATH_FAILED -> getString(R.string.inbound_call_path_failed)
        }
        toast(message)
    }

    private fun showProfileBottomSheet(visible: Boolean) {
        if (visible) {
            profileBottomSheet.expand()
        } else {
            profileBottomSheet.collapse()
        }
    }

    private fun showUserNameDialog(name: String) {
        navigator.showUserNameDialog(name, viewModel)
    }

    private fun showPhoneNumberDialog(phoneNumber: String) {
        navigator.showPhoneNumberDialog(phoneNumber, viewModel)
    }

    private fun showInboundCallPathDialog(items: Array<String>, index: Int) {
        navigator.showInboundCallPathDialog(items, index) { i ->
            val item = items[i]
            viewModel.updateInboundCallPath(i, item)
        }
    }

    private fun showRingtoneDialog(items: Array<String>, index: Int) {
        navigator.showRingtoneDialog(items, index) { i ->
            val item = items[i]
            viewModel.updateRingtone(i, item)
        }
    }
}
