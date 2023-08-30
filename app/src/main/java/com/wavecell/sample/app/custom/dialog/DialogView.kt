package com.wavecell.sample.app.custom.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import com.wavecell.sample.app.R

abstract class DialogView<T : ViewDataBinding>(
        @LayoutRes private val resourceId: Int,
        private val title: String
): DialogFragment() {

    /**
     * Views
     */

    private var builder: AlertDialog.Builder? = null
    private var v: View? = null

    protected var positiveButton: Button? = null


    /**
     * Lifecycle
     */

    abstract fun onBind(bind: T)
    abstract fun onCreateDialog()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        v = LayoutInflater.from(requireContext()).inflate(resourceId, null, false)
        builder = AlertDialog.Builder(requireContext(), R.style.DialogTheme).apply {
            setTitle(title)
            setView(v)
        }
        onCreateDialog()
        return builder?.create() ?: super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        v?.let {
            DataBindingUtil.bind<T>(it)?.let { binding ->
                onBind(binding)
                return binding.root
            }
        }
        return v
    }

    override fun onStart() {
        super.onStart()
        val d = dialog as? AlertDialog
        positiveButton = d?.getButton(AlertDialog.BUTTON_POSITIVE)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        builder = null
        v = null
    }


    /**
     * Setters
     */

    fun setOnPositiveClickListener(@StringRes resId: Int, listener: () -> Unit) {
        builder?.setPositiveButton(resId) { d, _ ->
            listener()
            d.dismiss()
        }
    }

    fun setOnNegativeClickListener(@StringRes resId: Int, listener: () -> Unit = { }) {
        builder?.setNegativeButton(resId) { d, _ ->
            listener()
            d.dismiss()
        }
    }
}