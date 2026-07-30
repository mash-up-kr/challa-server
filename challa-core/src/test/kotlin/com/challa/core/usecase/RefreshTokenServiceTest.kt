package com.challa.core.usecase

import com.challa.core.domain.Provider
import com.challa.core.domain.RefreshToken
import com.challa.core.domain.User
import com.challa.core.exception.InvalidTokenException
import com.challa.core.exception.TokenReuseDetectedException
import com.challa.core.token.RefreshTokenFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset

class RefreshTokenServiceTest {
    private val now = Instant.parse("2026-07-12T00:00:00Z")
    private val clock = Clock.fixed(now, ZoneOffset.UTC)
    private val ttl = Duration.ofDays(30)
    private val factory = RefreshTokenFactory()

    private lateinit var users: FakeUserRepository
    private lateinit var tokens: FakeRefreshTokenRepository
    private lateinit var service: RefreshTokenService

    @BeforeEach
    fun setUp() {
        users = FakeUserRepository()
        tokens = FakeRefreshTokenRepository()
        users.save(User(id = 1, provider = Provider.KAKAO, providerId = "sub-1"))
        service = RefreshTokenService(
            refreshTokenRepository = tokens,
            userRepository = users,
            accessTokenIssuer = FakeAccessTokenIssuer(),
            refreshTokenFactory = factory,
            clock = clock,
            refreshTokenTtl = ttl
        )
    }

    private fun issueToken(): String {
        val generated = factory.generate()
        tokens.save(
            RefreshToken(userId = 1, tokenHash = generated.hash, expiresAt = now.plus(ttl))
        )
        return generated.rawValue
    }

    @Test
    fun `rotation issues a new pair and revokes the presented token`() {
        val oldRaw = issueToken()

        val pair = service.refresh(oldRaw)

        assertNotEquals(oldRaw, pair.refreshToken)

        assertTrue(tokens.byHash(factory.hash(oldRaw))!!.revoked)

        val newStored = tokens.byHash(factory.hash(pair.refreshToken))
        assertNotNull(newStored)
        assertFalse(newStored!!.revoked)
        assertEquals(1, newStored.userId)
    }

    @Test
    fun `presenting an already-rotated token revokes all user tokens and fails`() {
        val oldRaw = issueToken()
        val firstPair = service.refresh(oldRaw)

        val ex = assertThrows(TokenReuseDetectedException::class.java) {
            service.refresh(oldRaw)
        }
        assertNotNull(ex)

        assertTrue(tokens.store.values.filter { it.userId == 1L }.all { it.revoked })
        assertTrue(tokens.byHash(factory.hash(firstPair.refreshToken))!!.revoked)
    }

    @Test
    fun `unknown token is rejected`() {
        assertThrows(InvalidTokenException::class.java) {
            service.refresh("totally-unknown-token")
        }
    }

    @Test
    fun `expired token is rejected`() {
        val generated = factory.generate()
        tokens.save(
            RefreshToken(userId = 1, tokenHash = generated.hash, expiresAt = now.minusSeconds(1))
        )
        assertThrows(InvalidTokenException::class.java) {
            service.refresh(generated.rawValue)
        }
    }

    @Test
    fun `concurrent rotation losing the compare-and-set is treated as reuse`() {
        val racyTokens = object : FakeRefreshTokenRepository() {
            override fun findByTokenHash(tokenHash: String): RefreshToken? =
                super.findByTokenHash(tokenHash)?.copy(revoked = false)
        }
        val racyService = RefreshTokenService(
            refreshTokenRepository = racyTokens,
            userRepository = users,
            accessTokenIssuer = FakeAccessTokenIssuer(),
            refreshTokenFactory = factory,
            clock = clock,
            refreshTokenTtl = ttl
        )
        val generated = factory.generate()
        val stored = racyTokens.save(
            RefreshToken(userId = 1, tokenHash = generated.hash, expiresAt = now.plus(ttl))
        )

        assertTrue(racyTokens.markRevoked(stored.id!!))

        assertThrows(TokenReuseDetectedException::class.java) {
            racyService.refresh(generated.rawValue)
        }

        assertTrue(racyTokens.store.values.filter { it.userId == 1L }.all { it.revoked })
        assertEquals(1, racyTokens.store.values.count { it.userId == 1L })
    }

    private fun assertNotNull(value: Any?) = assertFalse(value == null)
}
