package com.wavecell.sample.app.custom.bottomsheets

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.eght.voice.sdk.model.VoiceCallAudioOption
import com.wavecell.sample.app.R
import com.wavecell.sample.app.WavecellApplication
import com.wavecell.sample.app.custom.BottomSheetFragmentBinding
import com.wavecell.sample.app.databinding.BottomSheetAudioOptionsBinding
import com.wavecell.sample.app.extensions.addOnPropertyChanged
import com.wavecell.sample.app.injection.components.CallComponent
import com.wavecell.sample.app.presentation.model.AudioOptionsBottomSheetViewModel
import javax.inject.Inject

class AudioOptionsBottomSheet(supportFragmentManager: FragmentManager):
        BottomSheetFragmentBinding<BottomSheetAudioOptionsBinding>(supportFragmentManager,
                R.layout.bottom_sheet_audio_options, NAME) {

    companion object Factory {
        val NAME = AudioOptionsBottomSheet::class.java.simpleName
        private const val EXTRA_AUDIO_OPTION = "extra_audio_option"

        fun newInstance(supportFragmentManager: FragmentManager,
                        audioOption: VoiceCallAudioOption): AudioOptionsBottomSheet {
            val bundle = Bundle()
            bundle.putSerializable(EXTRA_AUDIO_OPTION, audioOption)
            val bottomSheet = AudioOptionsBottomSheet(supportFragmentManager)
            bottomSheet.arguments = bundle
            return bottomSheet
        }
    }

    @Inject
    lateinit var viewModel: AudioOptionsBottomSheetViewModel


    /**
     * Bottom Sheet Fragment Implementation
     */

    override fun shouldExpand(): Boolean = false

    @Suppress("UNUSED_PARAMETER")
    override var stateListener: StateListener?
        get() = null
        set(value) { /* ignore */ }

    override fun inject() {
        WavecellApplication.componentHolder
                .getComponent(CallComponent::class.java, this)
                .inject(this)
    }

    override fun bind(binding: BottomSheetAudioOptionsBinding) {
        val option = arguments?.getSerializable(EXTRA_AUDIO_OPTION) as? VoiceCallAudioOption
        binding.viewModel = viewModel
        viewModel.onBluetoothAvailable.set(true)
        viewModel.onAudioOptionSelected.set(option)
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.shouldCollapse.addOnPropertyChanged { shouldCollapse ->
            if (shouldCollapse.get())
                collapse()
        }
    }
}