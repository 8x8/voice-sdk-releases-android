package com.wavecell.sample.app.custom.bottomsheets

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.wavecell.sample.app.R
import com.wavecell.sample.app.WavecellApplication
import com.wavecell.sample.app.custom.BottomSheetFragmentBinding
import com.wavecell.sample.app.databinding.BottomSheetPlaceCallBinding
import com.wavecell.sample.app.extensions.showKeyboard
import com.wavecell.sample.app.injection.components.MainComponent
import com.wavecell.sample.app.presentation.event.PlaceCallEvent
import com.wavecell.sample.app.presentation.model.PlaceCallBottomSheetViewModel
import com.wavecell.sample.app.presentation.model.factory.PlaceCallBottomSheetViewModelFactory
import com.wavecell.sample.app.presentation.navigator.Navigator
import com.wavecell.sample.app.utils.SampleAppAccountPreferences
import com.wavecell.sample.app.utils.SampleAppPreferences
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class PlaceCallBottomSheet(supportFragmentManager: FragmentManager) :
        BottomSheetFragmentBinding<BottomSheetPlaceCallBinding>(supportFragmentManager,
                R.layout.bottom_sheet_place_call, PlaceCallBottomSheet::class.java.simpleName),
        BottomSheetFragmentBinding.StateListener {

    companion object {
        val NAME: String = PlaceCallBottomSheet::class.java.simpleName
        private const val EXTRA_USER_ID = "extra_user_id"

        fun newInstance(supportFragmentManager: FragmentManager,
                        userId: String): PlaceCallBottomSheet {
            val bundle = Bundle()
            bundle.putSerializable(EXTRA_USER_ID, userId)
            val bottomSheet = PlaceCallBottomSheet(supportFragmentManager)
            bottomSheet.arguments = bundle
            return bottomSheet
        }
    }

    @Inject
    lateinit var viewModelFactory: PlaceCallBottomSheetViewModelFactory

    @Inject
    lateinit var preferences: SampleAppPreferences

    @Inject
    lateinit var sampleAppAccountPreferences: SampleAppAccountPreferences

    @Inject
    lateinit var navigator: Navigator

    private val viewModel by viewModels<PlaceCallBottomSheetViewModel> { viewModelFactory }

    /**
     * Bottom Sheet Fragment Implementation
     */

    override fun shouldExpand(): Boolean = false

    @Suppress("UNUSED_PARAMETER")
    override var stateListener: StateListener?
        get() = this
        set(value) { /* ignore */ }

    override fun inject() {
        WavecellApplication.componentHolder
                .getComponent(MainComponent::class.java, this)
                .inject(this)
    }

    override fun bind(binding: BottomSheetPlaceCallBinding) {
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        setupObservers()
    }


    /**
     * Setups
     */

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.shouldCollapse.collectLatest { shouldCollapse ->
                    if (shouldCollapse) {
                        collapse()
                    }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.placeCallEvent.events.collectLatest { event ->
                    when(event) {
                        is PlaceCallEvent.Failure -> Toast.makeText(requireContext(), event.resId, Toast.LENGTH_LONG).show()
                        is PlaceCallEvent.Success -> {
                            navigator.presentCallUIAndNotification(event.callId, event.callee)
                        }
                    }
                }
            }
        }
    }


    /**
     * Bottom Sheet Fragment State Listener Implementation
     */

    override fun onExpanded() {
        var userId: String? = null
        var accountId: String?= null
        val peerPhoneNumber = arguments?.getString(EXTRA_USER_ID)
        val split = peerPhoneNumber?.split("!")
        if(split?.size == 2) {
            userId = split.get(0)
            accountId = split.get(1)
        }
        viewModel.calleeIdText.value = userId.orEmpty()
        viewModel.accountIdText. value = accountId ?: preferences.recentCallAccountId.ifBlank { sampleAppAccountPreferences.accountId }
        if (userId.isNullOrBlank()) {
            viewModel.onRequestFocus.value = true
            context?.showKeyboard()
        }
    }

    private fun getAccountId() = preferences.recentCallAccountId.ifBlank { sampleAppAccountPreferences.accountId }

    override fun onCollapsed() {
        viewModel.onRequestFocus.value = false
        viewModel.shouldCollapse.value = false
    }
}