package com.wavecell.sample.app.validation

object ValidationCharacterSets {

    private val decimalDigits = ('0'..'9')
    private val alphanumerics = ('a'..'z') + ('A'..'Z') + decimalDigits

    val displayName: Set<Char>
        get() = alphanumerics.toMutableSet().union(listOf('.', '_', '-'))

    val userId: Set<Char>
        get() = alphanumerics.toMutableSet().union(listOf('-')).subtract(listOf('p', 'P'))

    val phoneNumber: Set<Char>
        get() = decimalDigits.toMutableSet().union(listOf('+'))

    val accountId: Set<Char>
        get() = alphanumerics.toMutableSet().union(listOf('_')).subtract(listOf('p', 'P'))

}