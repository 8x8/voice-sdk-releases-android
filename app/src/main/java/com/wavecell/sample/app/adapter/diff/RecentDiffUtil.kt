package com.wavecell.sample.app.adapter.diff

import androidx.recyclerview.widget.DiffUtil
import com.wavecell.sample.app.presentation.model.RowRecentCallViewModel

class RecentDiffUtil: DiffUtil.ItemCallback<RowRecentCallViewModel>() {

    override fun areItemsTheSame(oldItem: RowRecentCallViewModel, newItem: RowRecentCallViewModel): Boolean {
        return oldItem.date == newItem.date
    }

    override fun areContentsTheSame(oldItem: RowRecentCallViewModel, newItem: RowRecentCallViewModel): Boolean {
        return oldItem.date == newItem.date &&
                oldItem.avatarUrl == newItem.avatarUrl &&
                oldItem.callType == newItem.callType &&
                oldItem.nameOrNumber == newItem.nameOrNumber &&
                oldItem.userId == newItem.userId
    }
}