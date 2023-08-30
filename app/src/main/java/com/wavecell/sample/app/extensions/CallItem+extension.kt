package com.wavecell.sample.app.extensions

import com.wavecell.sample.app.models.CallItem
import com.wavecell.sample.app.presentation.model.RowRecentCallViewModel

fun CallItem.toRowViewModel(): RowRecentCallViewModel {
    val diff = if (date <= 0L) 0 else (endTime - date)
    return RowRecentCallViewModel(nameOrNumber, userId, avatarUrl, diff, date, callType)
}