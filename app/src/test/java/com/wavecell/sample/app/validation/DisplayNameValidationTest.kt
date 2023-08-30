package com.wavecell.sample.app.validation

import junitparams.JUnitParamsRunner
import junitparams.Parameters
import junitparams.naming.TestCaseName
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(JUnitParamsRunner::class)
class DisplayNameValidationTest {

    private val pattern = InputValidation.displayName.pattern

    @Parameters("usr",
            "username",
            "USERname",
            "user.name",
            "user-name",
            "user_name",
            "user.name.123",
            "user-name-123",
            "user_name_123",
            "username123",
            "user123456",
            "123456",
            "01234567890123456789",
            "012345678901234567890123456789")
    @TestCaseName("input {0} should be valid")
    @Test
    fun test_validNames(input: String) {
        assertTrue(input.matches(pattern))
    }

    @Parameters("ab",                              // invalid length 2, length must between 3-30
            "012345678901234567890123456789a", // invalid length 31, length must between 3-30
            "_username_",                      // invalid start and last character
            ".username.",                      // invalid start and last character
            "-username-",                      // invalid start and last character
            "username#$%@123",                 // invalid symbols, support dot, hyphen and underscore
            ".user..name",                     // dot cant appear consecutively
            "user--name",                      // hyphen can't appear consecutively
            "user__name",                      // underscore can't appear consecutively
            "user._name",                      // dot and hyphen can't appear consecutively
            "user.-name",                      // dot and hyphen can't appear consecutively
            " ",                               // empty
            "")
    @TestCaseName("input {0} should be invalid")
    @Test
    fun test_invalidNames(input: String) {
        assertFalse(input.matches(pattern))
    }
}