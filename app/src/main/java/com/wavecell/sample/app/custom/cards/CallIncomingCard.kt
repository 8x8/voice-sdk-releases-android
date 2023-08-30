package com.wavecell.sample.app.custom.cards

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.eght.voice.sdk.model.VoiceCall
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.wavecell.sample.app.R
import com.wavecell.sample.app.constants.Constants
import com.wavecell.sample.app.log.AppComponentTag
import com.wavecell.sample.app.log.AppFeatureTag
import com.wavecell.sample.app.log.AppLog
import com.wavecell.sample.app.log.AppTag


class CallIncomingCard @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0,
        defStyleRes: Int = 0
): FrameLayout(context, attrs, defStyle, defStyleRes) {

    companion object {
        private val TAG = AppTag.make(AppFeatureTag.SAMPLE_APPLICATION, AppComponentTag.INCOMING_CALL, CallIncomingCard::class.java)
    }

    private val shakeAnimation = AnimationUtils.loadAnimation(context, R.anim.shake)

    private val parent: ViewGroup? = null
    private val view = LayoutInflater.from(context).inflate(R.layout.custom_call_incoming, parent, false)

    var call: VoiceCall? = null
        set(value) {
            field = value
            updateViews()
        }

    private val avatarImageView = view.findViewById(R.id.incoming_avatar_iv) as ImageView
    private val nameTextView = view.findViewById(R.id.incoming_name_tv) as TextView
    private val rejectFab = view.findViewById(R.id.incoming_reject_fab) as FloatingActionButton
    private val acceptFab = view.findViewById(R.id.incoming_accept_fab) as FloatingActionButton

    init {
        addView(view)
        setupViews()
    }

    /**
     * Setups
     */

    private fun setupViews() {
        rejectFab.setOnClickListener {
            call?.reject()
            call = null
        }

        acceptFab.setOnClickListener {
            AppLog.d(TAG, "${Constants.LOG_PREFIX} [call] (setupViews) accepting call ${call?.uuid}")
            call?.accept()
            //call?.endAndAccept()
            call = null
        }
    }

    private fun updateViews() {
        if (call == null) {
            hide()
            return
        } else show()
        nameTextView.text = call?.displayName ?: "Unknown"
    }



    /**
     * Visibility
     */

    private fun hide() {
        clearAnimation()
        visibility = View.GONE
    }

    private fun show() {
        visibility = View.VISIBLE
        startAnimation(shakeAnimation)
    }
}