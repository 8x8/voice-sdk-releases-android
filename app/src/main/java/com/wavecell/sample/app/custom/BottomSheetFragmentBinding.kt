package com.wavecell.sample.app.custom

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.wavecell.sample.app.R
import com.wavecell.sample.app.custom.bottomsheets.listener.BottomSheetStateListener
import com.wavecell.sample.app.custom.bottomsheets.state.BottomSheetState

abstract class BottomSheetFragmentBinding<DB: ViewDataBinding>(
        private val supportFragmentManager: FragmentManager,
        private val viewResId: Int,
        private val key: String
): BottomSheetDialogFragment() {

    enum class State {
        EXPANDED,
        COLLAPSED
    }

    private var state: State = State.COLLAPSED


    /**
     * Abstracts
     */

    protected abstract fun shouldExpand(): Boolean
    protected abstract fun bind(binding: DB)
    protected abstract var stateListener: StateListener?
    protected abstract fun inject()


    /**
     * Listeners
     */

    private var bottomSheetStateListener: BottomSheetStateListener? = null


    /**
     * Bottom Sheet Dialog Fragment Lifecycle
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<DB>(inflater, viewResId, container, false)
        bind(binding)
        bottomSheetStateListener?.onStateChanged(BottomSheetState.EXPANDED)
        return binding.root
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        stateListener?.onCollapsed()
        bottomSheetStateListener?.onStateChanged(BottomSheetState.COLLAPSED)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (shouldExpand()) { expand() }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheetId = com.google.android.material.R.id.design_bottom_sheet
            val bottomSheet = dialog.findViewById<View>(bottomSheetId) as FrameLayout
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

                override fun onSlide(bottomSheet: View, slideOffset: Float) { /* ignore */ }

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_EXPANDED -> { stateListener?.onExpanded() }
                        else -> { }
                    }
                }
            })
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        return dialog
    }


    /**
     * Setters
     */

    fun setOnBottomSheetStateListener(listener: BottomSheetStateListener) {
        bottomSheetStateListener = listener
    }


    /**
     * Helpers
     */

    fun expand() {
        if (state == State.COLLAPSED) {
            state = State.EXPANDED
            show(supportFragmentManager, key)
        }

    }

    override fun show(manager: FragmentManager, tag: String?) {
        val fragmentTransaction: FragmentTransaction = manager.beginTransaction()
        fragmentTransaction.add(this, tag)
        fragmentTransaction.commitAllowingStateLoss()
    }

    fun collapse() {
        if (state == State.EXPANDED) {
            state = State.COLLAPSED
            dismiss()
        }
    }


    /**
     * State Listener
     */

    interface StateListener {
        fun onExpanded()
        fun onCollapsed()
    }
}
