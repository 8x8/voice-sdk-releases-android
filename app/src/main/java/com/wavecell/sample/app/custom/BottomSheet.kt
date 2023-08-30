package com.wavecell.sample.app.custom

import android.view.View
import androidx.core.widget.NestedScrollView
import com.google.android.material.bottomsheet.BottomSheetBehavior

abstract class BottomSheet(private val view: View,
                           private val bottomSheetResId: Int = -1,
                           private val backgroundResId: Int = -1) {

    protected abstract fun peekHeight(): Int
    protected abstract fun onExpanded()
    protected abstract fun onCollapsed()


    /**
     * Vars
     */

    private lateinit var behavior: BottomSheetBehavior<View>



    /**
     * Views
     */

    private var bottomSheet: NestedScrollView? = null // = view.findViewById(R.id.recent_calls_bs)
    private var backgroundView: View? = null



    /**
     * Init
     */

    init {
        setupViews()
        setupBehavior()
    }


    /**
     * Setups
     */

    private fun setupViews() {
        if (backgroundResId != -1) backgroundView = view.findViewById(backgroundResId)
        if (bottomSheetResId != -1) bottomSheet = view.findViewById(bottomSheetResId)
    }


    private fun setupBehavior() {
        bottomSheet?.let {
            behavior = BottomSheetBehavior.from(it)
            behavior.peekHeight = peekHeight()
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
            behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    backgroundView?.alpha = slideOffset
                    if (slideOffset > 0.0) {
                        backgroundView?.visibility = View.VISIBLE
                    }
                }

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                        backgroundView?.visibility = View.GONE
                        onCollapsed()
                    } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                        backgroundView?.visibility = View.VISIBLE
                        onExpanded()
                    }
                }
            })
        }
    }



    /**
     * Helpers
     */

    fun expand() {
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    fun collapse() {
        behavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

}