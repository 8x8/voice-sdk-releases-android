package com.wavecell.sample.app.validation

object ValidationPatterns {

    val regexPatternDisplayName = Regex("^[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9]){1,28}[a-zA-Z0-9]$")

    val regexPatternUserId = Regex("(^[a-fA-F0-9]([-](?![-])|[a-fA-F0-9]){6,34}[a-fA-F0-9]$)|(^[a-zA-Z0-9&&[^pP]]{1,36}$)")

    val regexPatternPhoneNumber = Regex("^\\+(?:[0-9]?){6,14}[0-9]$")

    val regexPatterAccountId = Regex("^[a-zA-Z0-9&&[^pP]]([_](?![_])|[a-zA-Z0-9&&[^pP]]){3,18}[a-zA-Z0-9&&[^pP]]$")

}