package com.wavecell.sample.app.presentation.event

import java.io.File

enum class MainToastMessage {
    CALL_FAILED,
    PROVIDE_USER_NAME,
    USER_NAME_SAVED,
    USER_NAME_FAILED,
    PHONE_NUMBER_SAVED,
    PHONE_NUMBER_FAILED,
    RINGTONE_PATH_SAVED,
    INBOUND_CALL_PATH_SAVED,
    INBOUND_CALL_PATH_FAILED,
}

sealed class MainEvent {
    object StartRegistrationActivity: MainEvent()
    class StartShareFileAction(val file: File): MainEvent()
    object FinishActivity: MainEvent()
    object HideKeyboard: MainEvent()
    data class CopyToClipboard(val text: String): MainEvent()
    class SaveUserName(val name: String): MainEvent()
    class ShowPlaceCallBottomSheet(val userId: String = ""): MainEvent()
    class ShowProfileBottomSheet(val isVisible: Boolean): MainEvent()
    class ShowToast(val toastMessage: MainToastMessage): MainEvent()
    class ShowNameDialog(val name: String): MainEvent()
    class ShowPhoneNumberDialog(val phoneNumber: String): MainEvent()
    class ShowInboundCallPathDialog(val items: Array<String>, val index: Int): MainEvent()
    class ShowRingtoneDialog(val items: Array<String>, val index: Int): MainEvent()
    object ShowOpenSourceLicenses: MainEvent()
}
