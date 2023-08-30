package com.wavecell.sample.app.validation

import com.wavecell.sample.app.extensions.isValidAccountId
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import junitparams.naming.TestCaseName
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(JUnitParamsRunner::class)
class AccountIdValidationTest {

    private val pattern = InputValidation.accountId.pattern

    @Parameters("test1",
            "8x8_test",
            "8x8_test1",
            "8x8_test2",
            "01234567890123456789",
            "A0D2_0086_4074_A01B7")
    @TestCaseName("input {0} should be valid")
    @Test
    fun test_validAccountId(input: String) {
        assertTrue(input.isValidAccountId())
    }

    @Parameters("test012345678901234567890123456789012",
            "my-account",
            "p",
            "P",
            " ",
            "")
    @TestCaseName("input {0} should be invalid")
    @Test
    fun test_invalidAccountId(input: String) {
        assertFalse(input.isValidAccountId())
    }
}