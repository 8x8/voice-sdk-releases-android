package com.wavecell.sample.app.presentation.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.eght.voice.sdk.registration.exception.RegistrationException
import com.wavecell.sample.app.R
import com.wavecell.sample.app.WavecellApplication
import com.wavecell.sample.app.databinding.ActivityRegisterBinding
import com.wavecell.sample.app.extensions.copyToClipboard
import com.wavecell.sample.app.extensions.toast
import com.wavecell.sample.app.injection.components.RegisterComponent
import com.wavecell.sample.app.presentation.event.RegisterEvent
import com.wavecell.sample.app.presentation.model.ActivityRegisterViewModel
import com.wavecell.sample.app.presentation.navigator.RegisterNavigator
import com.wavecell.sample.app.utils.DeviceUtils
import javax.inject.Inject

class ActivityRegister : AppCompatActivity() {

    @Inject
    lateinit var viewModel: ActivityRegisterViewModel

    @Inject
    lateinit var navigator: RegisterNavigator

    private lateinit var binding: ActivityRegisterBinding


    /**
     * Lifecycle
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)
        binding.viewModel = viewModel
        binding.navigator = navigator
        lifecycle.addObserver(viewModel)

        setupObservers()

        PermissionProcessor.ensurePermissions(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(viewModel)
        WavecellApplication.componentHolder.clearComponent(RegisterComponent::class.java)
    }


    /**
     * Injection
     */

    private fun inject() {
        WavecellApplication.componentHolder
                .getComponent(RegisterComponent::class.java, this)
                .inject(this)
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

    private fun observeEvent(event: RegisterEvent) {
        when(event) {
            is RegisterEvent.AccountNotSelected -> toast(getString(R.string.no_account_selected))
            is RegisterEvent.RegistrationFailed -> {
                val id = when(event.e) {
                    is RegistrationException.InvalidEndpointCreated -> {
                        R.string.endpoint_created_incorrectly
                    }
                    else -> R.string.registration_failed
                }
                toast(getString(id))
            }
            is RegisterEvent.UserIdNotFound -> toast(getString(R.string.set_user_id_to_proceed))
            is RegisterEvent.UserNameNotFound -> toast(getString(R.string.set_user_name_to_proceed))
            is RegisterEvent.NavigationToManActivity -> navigator.navigateToActivityMain()
            is RegisterEvent.AppInfo -> {
                copyToClipboard(DeviceUtils.APP_VERSION_INFO)
                toast(DeviceUtils.APP_VERSION_INFO)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionProcessor.processPermissionResult(this, binding.container, requestCode, permissions, grantResults)
    }
}
