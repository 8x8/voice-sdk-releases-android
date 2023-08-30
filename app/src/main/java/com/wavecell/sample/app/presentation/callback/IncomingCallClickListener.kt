package com.wavecell.sample.app.presentation.callback

enum class IncomingCallClickOption {
    ACCEPT,
    REJECT
}
interface IncomingCallClickListener {
    fun onAccept()
    fun onReject()
}