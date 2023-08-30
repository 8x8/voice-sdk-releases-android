package com.wavecell.sample.app.presentation.model

import com.wavecell.sample.app.adapter.RecentRecycleAdapter

enum class CallType {
    INBOUND,
    OUTBOUND
}

class RowRecentCallViewModel(var nameOrNumber: String,
                             var userId: String,
                             var avatarUrl: String,
                             var duration: Long,
                             var date: Long,
                             var callType: CallType) {

    var listener: RecentRecycleAdapter.ClickListener? = null

    fun onClick() {
        listener?.onClick(userId)
    }
}