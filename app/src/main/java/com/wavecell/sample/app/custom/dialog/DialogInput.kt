package com.wavecell.sample.app.custom.dialog

import android.content.DialogInterface
import android.text.InputType
import com.wavecell.sample.app.R
import com.wavecell.sample.app.custom.dialog.DialogInput.Option.NAME
import com.wavecell.sample.app.custom.dialog.DialogInput.Option.PHONE_NUMBER
import com.wavecell.sample.app.databinding.DialogInputBinding
import com.wavecell.sample.app.extensions.isValidDisplayName
import com.wavecell.sample.app.extensions.isValidPhoneNumber
import com.wavecell.sample.app.extensions.showKeyboard

class DialogInput constructor(
        private val option: Option,
        title: String,
        private val hint: String,
        private val value: String,
        private val listener: ClickListener
): DialogView<DialogInputBinding>(R.layout.dialog_input, title) {

    companion object {
        val TAG = DialogInput::class.java.simpleName
    }

    enum class Option {
        NAME,
        PHONE_NUMBER
    }

    interface TextChangeListener {
        fun onTextChanged(text: String)
    }

    interface ClickListener {
        fun onPositive(option: Option, input: String)
    }

    private var query: String = value
    private var textChangedListener: TextChangeListener? = object: TextChangeListener {
        override fun onTextChanged(text: String) {
            query = text
            updatePositiveButton(text)
        }
    }


    /**
     * Lifecycle
     */

    override fun onCreateDialog() {
        setupPositiveButton()
        setupNegativeButton()
    }

    override fun onBind(bind: DialogInputBinding) {
        bind.lifecycleOwner = viewLifecycleOwner
        bind.listener = textChangedListener
        bind.hint = hint
        bind.value = value
        bind.inputType = when(option) {
            NAME -> InputType.TYPE_TEXT_FLAG_CAP_WORDS
            PHONE_NUMBER -> InputType.TYPE_CLASS_PHONE
        }
        bind.edit.context.showKeyboard()
    }

    override fun onStart() {
        super.onStart()
        updatePositiveButton(value)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        textChangedListener = null
    }


    /**
     * Setups
     */

    private fun setupPositiveButton() {
        setOnPositiveClickListener(R.string.save) {
            listener.onPositive(option, query)
        }
    }

    private fun setupNegativeButton() {
        setOnNegativeClickListener(R.string.cancel)
    }


    /**
     * Helpers
     */

    private fun updatePositiveButton(text: String) {
        val isValid = when(option) {
            NAME -> text.isValidDisplayName()
            PHONE_NUMBER -> text.isEmpty() || text.isValidPhoneNumber()
        }
        positiveButton?.isEnabled = isValid
        positiveButton?.alpha = if (isValid) 1.0f else 0.25f
    }
}

