package com.wavecell.sample.app.custom.bottomsheets

import android.view.View
import com.wavecell.sample.app.R
import com.wavecell.sample.app.custom.BottomSheet

class ProfileBottomSheet(view: View): BottomSheet(view, R.id.profile_bs) {


    /**
     * Bottom Sheet Implementation
     */

    override fun peekHeight(): Int = 0

    override fun onExpanded() { /* ignore */ }

    override fun onCollapsed() { /* ignore */ }
}
