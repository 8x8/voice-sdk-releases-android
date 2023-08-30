package com.wavecell.sample.app.adapter.binding

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.databinding.BindingAdapter
import com.wavecell.sample.app.custom.dialog.DialogInput

@BindingAdapter("onTextChanged")
fun EditText.setOnTextChanged(listener: DialogInput.TextChangeListener?) {
    addTextChangedListener(object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { /* ignore */ }
        override fun afterTextChanged(s: Editable?) { /* ignore*/ }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            listener?.onTextChanged(s?.toString().orEmpty())
        }
    })
}