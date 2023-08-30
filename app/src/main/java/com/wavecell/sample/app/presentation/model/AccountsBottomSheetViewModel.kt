package com.wavecell.sample.app.presentation.model

import androidx.lifecycle.MutableLiveData
import com.wavecell.sample.app.models.VoiceAccount
import com.wavecell.sample.app.presentation.callback.VoiceAccountSelectionListener
import com.wavecell.sample.app.utils.SampleAppAccountPreferences
import com.wavecell.sample.app.utils.manyLet

class AccountsBottomSheetViewModel(private val accountSelectionListener: VoiceAccountSelectionListener, sampleAppAccountPreferences: SampleAppAccountPreferences) {

    val shouldDismiss = MutableLiveData<Boolean>()
    val showErrorToast = MutableLiveData<Boolean>()
    val accountId = MutableLiveData(sampleAppAccountPreferences.accountId)
    val serviceUrl = MutableLiveData(sampleAppAccountPreferences.serviceUrl)
    val tokenUrl = MutableLiveData(sampleAppAccountPreferences.tokenUrl)
    fun onAddAccountInfo() {
        val accountId= accountId.value
        val serviceURL = serviceUrl.value
        val tokenURL = tokenUrl.value
        manyLet(accountId, serviceURL, tokenURL) { account, service, token ->
            accountSelectionListener.onAccountSelected(VoiceAccount(account, service, token))
            shouldDismiss.value = true
        } ?: run {
            showErrorToast.value = true
        }
    }
}