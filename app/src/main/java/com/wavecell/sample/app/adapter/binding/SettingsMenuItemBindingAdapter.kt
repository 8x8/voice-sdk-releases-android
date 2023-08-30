package com.wavecell.sample.app.adapter.binding

import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.wavecell.sample.app.R
import com.wavecell.sample.app.constants.Constants.PHONE_NUMBER_HINT
import com.wavecell.sample.app.custom.items.SettingsMenuItem

@BindingAdapter(value = ["item", "clickListener", "settingsValue"], requireAll = true)
fun SettingsMenuItem.setItem(item: SettingsMenuItem.Item,
                             clickListener: SettingsMenuItem.ClickListener,
                             value: String?) {
    binding.item = item
    binding.clickListener = clickListener

    binding.title.text = item.title

    when (item) {
        SettingsMenuItem.Item.USER_ID,
        SettingsMenuItem.Item.ACCOUNT_ID,
        SettingsMenuItem.Item.TOKEN_URL,
        SettingsMenuItem.Item.SERVICE_URL,
        SettingsMenuItem.Item.ENVIRONMENT ->
            binding.valueLayout.setBackgroundResource(R.drawable.card_default)
        else -> { }
    }

    binding.value.alpha = when(item) {
        SettingsMenuItem.Item.USER_ID,
        SettingsMenuItem.Item.ACCOUNT_ID,
        SettingsMenuItem.Item.TOKEN_URL,
        SettingsMenuItem.Item.SERVICE_URL,
        SettingsMenuItem.Item.ENVIRONMENT -> 0.24f
        else -> 1.00f
    }

    val at = if(item == SettingsMenuItem.Item.USER_ID) "@" else ""
    binding.value.text = StringBuilder().append(at).append(value.orEmpty()).toString()
    if (item == SettingsMenuItem.Item.PHONE_NUMBER) {
        val hasValue = !value.isNullOrBlank()
        val textColor = if (hasValue) R.color.md_black_1000 else R.color.md_black_1000_50
        binding.value.text = if(hasValue) value else PHONE_NUMBER_HINT
        binding.value.setTextColor(ContextCompat.getColor(context, textColor))
    }

    binding.icon.isVisible = when(item) {
        SettingsMenuItem.Item.USER_ID,
        SettingsMenuItem.Item.ACCOUNT_ID,
        SettingsMenuItem.Item.TOKEN_URL,
        SettingsMenuItem.Item.SERVICE_URL,
        SettingsMenuItem.Item.ENVIRONMENT -> false
        else -> true
    }
}