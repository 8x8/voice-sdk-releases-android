package com.wavecell.sample.app.custom.bottomsheets

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.wavecell.sample.app.R
import com.wavecell.sample.app.WavecellApplication
import com.wavecell.sample.app.databinding.BottomSheetUsersBinding
import com.wavecell.sample.app.injection.components.RegisterComponent
import com.wavecell.sample.app.presentation.model.AccountsBottomSheetViewModel
import javax.inject.Inject

class AccountsBottomSheet: BottomSheetDialogFragment() {

    companion object {
        val NAME = AccountsBottomSheet::class.simpleName
    }

    @Inject
    lateinit var viewModel: AccountsBottomSheetViewModel

    /**
     * Lifecycle
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener {

            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { layout ->
                val behaviour = BottomSheetBehavior.from(layout)
                setupFullHeight(layout)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return dialog
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = DataBindingUtil.inflate<BottomSheetUsersBinding>(inflater, R.layout.bottom_sheet_users, container, false)
        binding.viewModel = viewModel
        setupObservers()
        return binding.root
    }


    /**
     * Setups
     */

    private fun setupObservers() {
        viewModel.shouldDismiss.observe(viewLifecycleOwner) { shouldDismiss ->
            if (shouldDismiss) {
                dismiss()
            }
        }

        viewModel.showErrorToast.observe(viewLifecycleOwner) {
            if(it) {
                Toast.makeText(context, R.string.missing_account_data, Toast.LENGTH_SHORT).show()
            }
        }
    }


    /**
     * Injection
     */

    private fun inject() {
        WavecellApplication.componentHolder
                .getComponent(RegisterComponent::class.java, this)
                .inject(this)
    }
}