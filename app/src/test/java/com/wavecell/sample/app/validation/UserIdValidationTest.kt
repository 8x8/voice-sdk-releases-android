package com.wavecell.sample.app.validation

import junitparams.JUnitParamsRunner
import junitparams.Parameters
import junitparams.naming.TestCaseName
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(JUnitParamsRunner::class)
class UserIdValidationTest {

    private val pattern = InputValidation.userId.pattern

    @Parameters("1",
            "usr1",
            "user01",
            "user01234567890123456789012345678901",
            "A0D2A01B-0086-4074-9962-7E6C355488C8")
    @TestCaseName("input {0} should be valid")
    @Test
    fun test_validUserId(input: String) {
        assertTrue(input.matches(pattern))
    }

    @Parameters("user012345678901234567890123456789012",
            "A0D2A01B-0086-4074-9962-7E6C355488C89",
            "p",
            "P",
            " ",
            "")
    @TestCaseName("input {0} should be invalid")
    @Test
    fun test_invalidUserId(input: String) {
        assertFalse(input.matches(pattern))
    }
}