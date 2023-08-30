package com.wavecell.sample.app.extensions

import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.eght.voice.sdk.model.VoiceCallAudioOption
import com.wavecell.sample.app.R

fun TextView.selected(isChecked: Boolean) {
    val textColor = if (isChecked) R.color.colorAccent else R.color.md_black_1000
    val backgroundId = if (isChecked) R.drawable.ripple_light_mode_selected else R.drawable.ripple_light_mode_default
    val color = ContextCompat.getColor(context, textColor)
    val colorStateList = ColorStateList.valueOf(color)
    setTextColor(colorStateList)
    setBackgroundResource(backgroundId)

    compoundDrawables.firstOrNull()?.let {
        it.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
        setCompoundDrawables(it, null, null, null)
    }
}

@BindingAdapter("onAudioOutput")
fun TextView.onAudioOutput(option: VoiceCallAudioOption) {
    when(tag) {
        context.getString(R.string.bluetooth) -> selected(option == VoiceCallAudioOption.BLUETOOTH)
        context.getString(R.string.earpiece) -> selected(option == VoiceCallAudioOption.EARPIECE)
        context.getString(R.string.speaker) -> selected(option == VoiceCallAudioOption.SPEAKER)
    }
}
