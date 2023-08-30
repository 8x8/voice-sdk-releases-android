package com.wavecell.sample.app.presentation.view.activity

import android.app.KeyguardManager
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.wavecell.sample.app.R
import com.wavecell.sample.app.WavecellApplication
import com.wavecell.sample.app.databinding.ActivityIncomingCallBinding
import com.wavecell.sample.app.injection.components.IncomingCallComponent
import com.wavecell.sample.app.presentation.model.ActivityIncomingCallViewModel
import com.wavecell.sample.app.presentation.model.IncomingCallBottomSheetViewModel
import com.wavecell.sample.app.presentation.model.factory.ActivityIncomingCallViewModelFactory
import com.wavecell.sample.app.presentation.navigator.IncomingCallNavigator
import com.wavecell.sample.app.utils.AppApiLevelUtils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class ActivityIncomingCall : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ActivityIncomingCallViewModelFactory

    @Inject
    lateinit var incomingCallBottomSheetViewModel: IncomingCallBottomSheetViewModel

    @Inject
    lateinit var navigator: IncomingCallNavigator

    private val viewModel by viewModels<ActivityIncomingCallViewModel> { viewModelFactory }

    /**
     * Lifecycle
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(viewModel)
        val binding: ActivityIncomingCallBinding = DataBindingUtil.setContentView(this, R.layout.activity_incoming_call)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.incomingCallBottomSheetVieWModel = incomingCallBottomSheetViewModel
        setupObservers()
        setupActivityProperties()
    }

    override fun onDestroy() {
        super.onDestroy()
        WavecellApplication.componentHolder.clearComponent(IncomingCallComponent::class.java)
    }


    /**
     * Injection
     */

    private fun inject() {
        WavecellApplication.componentHolder.getComponent(IncomingCallComponent::class.java, this).inject(this)
    }


    /**
     * Setups
     */

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.incomingCall.collectLatest { call ->
                    incomingCallBottomSheetViewModel.call.set(call)
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.finishEvent.events.collectLatest { navigator.finish() }
            }
        }
    }

    private fun setupActivityProperties() {
        if (AppApiLevelUtils.hasAtLeastOreoMr1()) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            dismissKeyguard()
        }

        window.addFlags(getFlags())
    }

    /**
     * Dismiss keyguard with [KeyguardManager]
     */
    private fun dismissKeyguard() {
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        keyguardManager.requestDismissKeyguard(this, null)
    }

    /**
     * Provides flag combination suitable for the phone's Android version.
     */
    @Suppress("DEPRECATION")
    private fun getFlags(): Int {
        val baseFlag = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON

        return if (AppApiLevelUtils.hasAtLeastOreoMr1()) {
            baseFlag
        } else {
            baseFlag or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
        }
    }
}
