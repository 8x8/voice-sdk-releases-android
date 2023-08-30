package com.wavecell.sample.app.validation

import junitparams.JUnitParamsRunner
import junitparams.Parameters
import junitparams.naming.TestCaseName
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(JUnitParamsRunner::class)
class PhoneNumberValidationTest {

    private val pattern = InputValidation.phoneNumber.pattern

    @Parameters("+226071234567",
            "+14155552671",
            "+44207183875044",
            "+447953966150",
            "+55115525632555",
            "+123456789012345")
    @TestCaseName("input {0} should be valid")
    @Test
    fun test_validPhoneNumber(input: String) {
        assertTrue(input.matches(pattern))
    }

    @Parameters("226071234567",
            "+1234567890123456")
    @TestCaseName("input {0} should be invalid")
    @Test
    fun test_invalidPhoneNumbers(input: String) {
        assertFalse(input.matches(pattern))
    }
}