package com.wavecell.sample.app.presentation.event

sealed class RegisterEvent {
    object AccountNotSelected: RegisterEvent()
    data class RegistrationFailed(val e: Exception): RegisterEvent()
    object UserIdNotFound: RegisterEvent()
    object UserNameNotFound: RegisterEvent()
    object AppInfo: RegisterEvent()
    object NavigationToManActivity: RegisterEvent()
}