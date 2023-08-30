package com.wavecell.sample.app.presentation.model

import androidx.databinding.ObservableField
import com.eght.voice.sdk.model.VoiceCall
import com.wavecell.sample.app.constants.Constants
import com.wavecell.sample.app.log.AppComponentTag
import com.wavecell.sample.app.log.AppFeatureTag
import com.wavecell.sample.app.log.AppLog
import com.wavecell.sample.app.log.AppTag
import com.wavecell.sample.app.presentation.callback.IncomingCallClickListener
import com.wavecell.sample.app.presentation.navigator.IncomingCallNavigator
import javax.inject.Inject

class IncomingCallBottomSheetViewModel @Inject constructor(
        private val navigator: IncomingCallNavigator
): IncomingCallClickListener {

    companion object {
        private val TAG = AppTag.make(AppFeatureTag.SAMPLE_APPLICATION, AppComponentTag.INCOMING_CALL, IncomingCallBottomSheetViewModel::class.java)
    }

    var call = ObservableField<VoiceCall?>()


    /**
     * Incoming Call Click Listener Implementation
     */

    override fun onAccept() {
        AppLog.d(TAG, "---> [call] ACCEPT incoming call")
        call.get()?.accept() ?: AppLog.e(TAG, "${Constants.LOG_PREFIX} No calls to accept")
        call.set(null)
        navigator.navigateToActivityCall()
    }

    override fun onReject() {
        AppLog.d(TAG, "---> [call] REJECT incoming call")
        call.get()?.reject() ?: AppLog.e(TAG, "${Constants.LOG_PREFIX} No calls to reject")
        call.set(null)
    }
}