package com.challa.core.token

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.Base64

class RefreshTokenFactoryTest {

    private val factory = RefreshTokenFactory()

    @Test
    fun `generates unique tokens carrying at least 256 bits of entropy`() {
        val a = factory.generate()
        val b = factory.generate()

        assertNotEquals(a.rawValue, b.rawValue)
        assertNotEquals(a.hash, b.hash)
        val decoded = Base64.getUrlDecoder().decode(a.rawValue)
        assertTrue(decoded.size >= 32, "expected >= 256 bits of entropy")
    }

    @Test
    fun `hash is deterministic and matches the generated hash`() {
        val generated = factory.generate()
        assertEquals(generated.hash, factory.hash(generated.rawValue))
        assertEquals(factory.hash("same-input"), factory.hash("same-input"))
    }

    @Test
    fun `hash never equals the raw value`() {
        val generated = factory.generate()
        assertNotEquals(generated.rawValue, generated.hash)
    }

    @Test
    fun `rejects insufficient entropy`() {
        assertThrows(IllegalArgumentException::class.java) {
            RefreshTokenFactory(tokenByteLength = 16)
        }
    }
}
