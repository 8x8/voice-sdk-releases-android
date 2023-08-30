package com.wavecell.sample.app.custom.views

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.wavecell.sample.app.R

class CallButtonView@JvmOverloads constructor(context: Context, private val attrs: AttributeSet? = null,
                                       defStyle: Int = 0, defStyleRes: Int = 0):
        FrameLayout(context, attrs, defStyle, defStyleRes) {

    /**
     * Views
     */

    private val parent: ViewGroup? = null
    private val view = LayoutInflater.from(context).inflate(R.layout.call_button_view, parent, false)

    private val fab = view.findViewById(R.id.call_button_fab) as FloatingActionButton
    private val titleTextView = view.findViewById(R.id.call_button_title_tv) as TextView


    init {
        addView(view)
        setupAttrs()
    }


    /**
     * Setters
     */

    private var key: String = ""
        set(value) {
            field = value
            fab.tag = key
        }

    var title: String = ""
        set(value) {
            field = value
            titleTextView.text = value
        }

    var iconResId: Int = 0
        set(value) {
            field = value
            fab.setImageDrawable(resources.getDrawable(value, null))
        }

    private var icon: Drawable? = null
        set(value) {
            field = value
            fab.setImageDrawable(value)
        }

    var backColor: Int = 0
        set(value) {
            field = value
            fab.backgroundTintList = ColorStateList.valueOf(value)
        }

    private var iconColor: Int = 0
        set(value) {
            field = value
            fab.imageTintList = ColorStateList.valueOf(value)
        }

    var textColor: Int = 0
        set(value) {
            field = value
            titleTextView.setTextColor(ColorStateList.valueOf(value))
        }

    fun setOnFabClickListener(clickListener: OnClickListener) {
        fab.setOnClickListener(clickListener)
    }



    /**
     * Setups
     */

    private fun setupAttrs() {
        val set = context.obtainStyledAttributes(attrs, R.styleable.CallButtonView)
        title = set.getString(R.styleable.CallButtonView_button_title) ?: ""
        icon = set.getDrawable(R.styleable.CallButtonView_button_icon)
        backColor = set.getColor(R.styleable.CallButtonView_button_background_color,
                resources.getColor(R.color.colorAccentLight, null))
        this.iconColor = set.getColor(R.styleable.CallButtonView_button_icon_color,
                resources.getColor(R.color.md_black_1000, null))
        textColor = set.getColor(R.styleable.CallButtonView_button_text_color,
                resources.getColor(R.color.md_black_1000, null))
        key = set.getString(R.styleable.CallButtonView_button_key) ?: ""
        set.recycle()
    }
 }