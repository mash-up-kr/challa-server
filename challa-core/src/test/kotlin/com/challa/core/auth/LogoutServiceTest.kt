package com.challa.core.auth

import com.challa.core.FakeRefreshTokenRepository
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant

class LogoutServiceTest {
    private val factory = RefreshTokenFactory()
    private lateinit var tokens: FakeRefreshTokenRepository
    private lateinit var service: LogoutService

    @BeforeEach
    fun setUp() {
        tokens = FakeRefreshTokenRepository()
        service = LogoutService(tokens, factory)
    }

    @Test
    fun `deletes the token when it belongs to the caller`() {
        val generated = factory.generate()
        tokens.save(RefreshToken(userId = 1, tokenHash = generated.hash, expiresAt = Instant.MAX))

        service.logout(userId = 1, refreshToken = generated.rawValue)

        assertNull(tokens.byHash(generated.hash))
    }

    @Test
    fun `does nothing when the token belongs to another user`() {
        val generated = factory.generate()
        tokens.save(RefreshToken(userId = 2, tokenHash = generated.hash, expiresAt = Instant.MAX))

        service.logout(userId = 1, refreshToken = generated.rawValue)

        assertNotNull(tokens.byHash(generated.hash))
    }

    @Test
    fun `does nothing for an unknown token`() {
        service.logout(userId = 1, refreshToken = "unknown")
    }
}
