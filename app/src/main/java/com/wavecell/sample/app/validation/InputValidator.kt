package com.wavecell.sample.app.validation

data class InputValidator(
        val pattern: Regex,
        val characterSet: Set<Char>,
        val maxLength: Int
)
