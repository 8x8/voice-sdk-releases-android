package com.wavecell.sample.app.presentation.view.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.wavecell.sample.app.R
import com.wavecell.sample.app.WavecellApplication
import com.wavecell.sample.app.databinding.ActivityCallBinding
import com.wavecell.sample.app.extensions.toast
import com.wavecell.sample.app.injection.components.CallComponent
import com.wavecell.sample.app.presentation.event.ActiveCallEvent
import com.wavecell.sample.app.presentation.model.ActivityCallViewModel
import com.wavecell.sample.app.presentation.model.factory.ActivityCallViewModelFactory
import com.wavecell.sample.app.presentation.navigator.Navigator
import javax.inject.Inject

class ActivityCall : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ActivityCallViewModelFactory

    @Inject
    lateinit var navigator: Navigator

    private val viewModel: ActivityCallViewModel by viewModels { viewModelFactory }


    /**
     * Lifecycle
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityCallBinding>(this, R.layout.activity_call)
        binding.viewModel = viewModel
        lifecycle.addObserver(viewModel)

        setupObservers()
    }

    override fun onDestroy() {
        super.onDestroy()
        clearCallComponent()
    }


    /**
     * Injection
     */

    private fun inject() {
        WavecellApplication.componentHolder.getComponent(CallComponent::class.java, this).inject(this)
    }

    private fun clearCallComponent() {
        WavecellApplication.componentHolder.clearComponent(CallComponent::class.java)
    }


    /**
     * Setups
     */

    private fun setupObservers() {
        viewModel.event.observe(this) { observeEvent(it) }
    }


    /**
     * Live Data Observers
     */

    private fun observeEvent(event: ActiveCallEvent) {
        when(event) {
            is ActiveCallEvent.ShowToast -> toast(event.message)
            is ActiveCallEvent.FinishActivity -> {
                finishAndRemoveTask()
                clearCallComponent()
            }
            is ActiveCallEvent.ShowAudioOptions -> {
                navigator.showAudioOptionBottomSheet(event.audioOption)
            }
        }
    }
}
