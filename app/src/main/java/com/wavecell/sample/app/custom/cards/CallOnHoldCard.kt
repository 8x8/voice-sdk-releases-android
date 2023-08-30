package com.wavecell.sample.app.custom.cards

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModel
import com.eght.voice.sdk.model.VoiceCall
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.wavecell.sample.app.R
import com.wavecell.sample.app.presentation.model.ActivityCallViewModel


class CallOnHoldCard@JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0,
        defStyleRes: Int = 0
): FrameLayout(context, attrs, defStyle, defStyleRes) {

    private val parent: ViewGroup? = null
    private val view = LayoutInflater.from(context).inflate(R.layout.custom_call_on_hold, parent, false)

    var call: VoiceCall? = null
        set(value) {
            field = value
            updateViews()
        }

    var viewModel: ActivityCallViewModel? = null

    private val avatarImageView = view.findViewById(R.id.on_hold_avatar_iv) as ImageView
    private val nameTextView = view.findViewById(R.id.on_hold_name_tv) as TextView
    private val endCall = view.findViewById(R.id.reject_fab) as FloatingActionButton
    private val resumeCall = view.findViewById(R.id.resume_fab) as FloatingActionButton

    init {
        addView(view)
    }



    /**
     * Setups
     */

    private fun updateViews() {
        if (call == null) {
            hide()
            return
        } else show()
        nameTextView.text = call?.displayName ?: "Unknown"
        endCall.setOnClickListener {
            viewModel?.onEndingCallOnHold()
        }

        resumeCall.setOnClickListener {
            viewModel?.onRetrieveCallOnHold()
        }
    }

    /**
     * Visibility
     */

    private fun hide() {
        visibility = View.GONE
    }

    private fun show() {
        visibility = View.VISIBLE
    }
}