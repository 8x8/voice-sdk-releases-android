package com.wavecell.sample.app.proximity

import com.eght.voice.sdk.Voice
import com.eght.voice.sdk.model.VoiceCall
import com.eght.voice.sdk.model.VoiceCallAudioOption
import com.wavecell.sample.app.log.AppComponentTag
import com.wavecell.sample.app.log.AppFeatureTag
import com.wavecell.sample.app.log.AppLog
import com.wavecell.sample.app.log.AppTag

class CallProximitySensor(private val proximityLock: ProximityLock, private val voice: Voice)
{
    companion object {
        private val TAG = AppTag.make(AppFeatureTag.SAMPLE_APPLICATION, AppComponentTag.UI, CallProximitySensor::class.java)
    }

    private var lastAudioRoute: VoiceCallAudioOption? = null


    /**
     * Read-only properties
     */

    private val activeCalls: List<VoiceCall>
        get() = voice.calls


    fun onCallUpdated() {
        AppLog.d(TAG, "---> [sensor] on call updated")
        determineProximitySensor()
    }

    fun onAudioChanged(audioOption: VoiceCallAudioOption) {
        updateForAudioRoute(audioOption)
    }

    /**
     * Helper functions
     */

    private fun updateForAudioRoute(callAudioOutputOption: VoiceCallAudioOption) {
        AppLog.d(TAG, "---> received audio option: $callAudioOutputOption")
        lastAudioRoute = callAudioOutputOption
        when (callAudioOutputOption) {
            VoiceCallAudioOption.BLUETOOTH,
            VoiceCallAudioOption.SPEAKER -> disableProximitySensor()
            else -> enableProximitySensor()
        }
    }

    private fun shouldIgnoreEnableCommand(): Boolean {
        val hasNoProximityAudioRoutes = lastAudioRoute == VoiceCallAudioOption.BLUETOOTH
                || lastAudioRoute == VoiceCallAudioOption.SPEAKER
        val hasOnlyIncomingCalls = activeCalls.isNotEmpty() && activeCalls.all { it.isIncoming }

        return hasNoProximityAudioRoutes || hasOnlyIncomingCalls
    }

    private fun determineProximitySensor() {
        if (voice.hasActiveCalls && voice.currentAudioOption == VoiceCallAudioOption.EARPIECE) {
            enableProximitySensor()
        } else {
            disableProximitySensor()
        }
    }

    private fun enableProximitySensor() {
        AppLog.d(TAG, "---> [sensor] enabling proximity sensor")
        if (!shouldIgnoreEnableCommand()) {
            proximityLock.enable()
        }
    }

    private fun disableProximitySensor() {
        AppLog.d(TAG, "---> [sensor] disabling proximity sensor")
        proximityLock.disable()
    }
}
