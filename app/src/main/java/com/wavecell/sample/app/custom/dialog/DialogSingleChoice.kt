package com.wavecell.sample.app.custom.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.wavecell.sample.app.R

class DialogSingleChoice constructor(
        private val title: String,
        private val items: Array<String>,
        private val index: Int,
        private var listener: ClickListener?
): DialogFragment() {

    companion object {
        val TAG: String = DialogSingleChoice::class.java.simpleName
    }

    fun interface ClickListener {
        fun onItemSelected(index: Int)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext(), R.style.DialogTheme).apply {
            setTitle(title)
            setSingleChoiceItems(items, index) { dialog, i ->
                listener?.onItemSelected(i)
                dialog.dismiss()
            }
            setNegativeButton(context.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
        }
        return builder.create()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener = null
    }
}