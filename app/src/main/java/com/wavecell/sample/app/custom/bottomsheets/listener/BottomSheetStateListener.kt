package com.wavecell.sample.app.custom.bottomsheets.listener

import com.wavecell.sample.app.custom.bottomsheets.state.BottomSheetState

interface BottomSheetStateListener {
    fun onStateChanged(state: BottomSheetState)
}