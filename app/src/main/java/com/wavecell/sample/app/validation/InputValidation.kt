package com.wavecell.sample.app.validation

object InputValidation {

    val displayName = InputValidator(ValidationPatterns.regexPatternDisplayName, ValidationCharacterSets.displayName, 30)
    val userId = InputValidator(ValidationPatterns.regexPatternUserId, ValidationCharacterSets.userId, 36)
    val phoneNumber = InputValidator(ValidationPatterns.regexPatternPhoneNumber, ValidationCharacterSets.phoneNumber, 16)
    val accountId = InputValidator(ValidationPatterns.regexPatterAccountId, ValidationCharacterSets.accountId, 36)
}