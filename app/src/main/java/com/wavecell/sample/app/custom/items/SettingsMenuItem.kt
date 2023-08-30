package com.wavecell.sample.app.custom.items

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import com.wavecell.sample.app.R
import com.wavecell.sample.app.databinding.ItemSettingsMenuBinding

class SettingsMenuItem@JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0,
        defStyleRes: Int = 0
): FrameLayout(context, attrs, defStyle, defStyleRes) {

    enum class Item(val title: String) {
        NONE("None"),
        NAME("Name"),
        SERVICE_URL("Service URL"),
        TOKEN_URL("Token URL"),
        USER_ID("User ID"),
        ACCOUNT_ID("Account ID"),
        ENVIRONMENT("Environment"),
        PHONE_NUMBER("Phone Number"),
        RINGTONE("Ringtone"),
        INBOUND_CALL_PATH("Inbound Call Path")
    }

    interface ClickListener {
        fun onClick(item: Item)
    }

    interface TextWatcherListener {
        fun onTextChanged(item: Item, value: String)
    }

    internal var item = Item.NONE
    internal val binding: ItemSettingsMenuBinding =
            DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_settings_menu, this, true)
}