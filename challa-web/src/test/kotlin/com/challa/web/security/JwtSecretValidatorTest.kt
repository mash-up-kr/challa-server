package com.challa.web.security

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class JwtSecretValidatorTest {

    private val strongSecret = "a-strong-production-secret-that-is-long-enough-123456"

    @Test
    fun `placeholder secret is rejected outside dev and test profiles`() {
        assertThrows(IllegalStateException::class.java) {
            JwtSecretValidator.validate(JwtSecretValidator.INSECURE_DEV_SECRET, emptyList())
        }
        assertThrows(IllegalStateException::class.java) {
            JwtSecretValidator.validate(JwtSecretValidator.INSECURE_DEV_SECRET, listOf("prod"))
        }
    }

    @Test
    fun `blank secret is rejected outside dev and test profiles`() {
        assertThrows(IllegalStateException::class.java) {
            JwtSecretValidator.validate("", emptyList())
        }
    }

    @Test
    fun `short secret is rejected outside dev and test profiles`() {
        assertThrows(IllegalStateException::class.java) {
            JwtSecretValidator.validate("too-short", listOf("prod"))
        }
    }

    @Test
    fun `strong secret is accepted in any profile`() {
        assertDoesNotThrow { JwtSecretValidator.validate(strongSecret, emptyList()) }
        assertDoesNotThrow { JwtSecretValidator.validate(strongSecret, listOf("prod")) }
    }

    @Test
    fun `dev and test profiles are exempt so local runs and tests need no real secret`() {
        assertDoesNotThrow {
            JwtSecretValidator.validate(JwtSecretValidator.INSECURE_DEV_SECRET, listOf("dev"))
        }
        assertDoesNotThrow { JwtSecretValidator.validate("", listOf("test")) }
    }
}
