package com.wavecell.sample.app.presentation.callback

enum class ActiveCallClickOption {
    HANG_UP,
    HOLD,
    MUTE,
    SPEAKER
}

interface ActiveCallClickListener {
    fun onOptionSelected(option: ActiveCallClickOption)
}