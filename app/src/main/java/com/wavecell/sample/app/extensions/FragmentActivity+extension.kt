package com.wavecell.sample.app.extensions

import android.widget.Toast
import androidx.fragment.app.FragmentActivity

fun FragmentActivity.toast(message: String?, length: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, message, length).show()
}