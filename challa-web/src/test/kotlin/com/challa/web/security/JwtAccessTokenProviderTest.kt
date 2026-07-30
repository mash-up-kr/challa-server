package com.challa.web.security

import com.challa.core.domain.Provider
import com.challa.core.exception.InvalidTokenException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset

class JwtAccessTokenProviderTest {
    private val secret = "unit-test-secret-must-be-at-least-32-bytes-long-0123456789"
    private val now = Instant.parse("2026-07-12T00:00:00Z")

    private fun provider(clock: Clock) = JwtAccessTokenProvider(
        properties = JwtProperties(
            secret = secret,
            issuer = "challa",
            accessTokenTtl = Duration.ofMinutes(30)
        ),
        clock = clock
    )

    @Test
    fun `issues a token that parses back to the same principal`() {
        val provider = provider(Clock.fixed(now, ZoneOffset.UTC))

        val token = provider.issue(userId = 42, provider = Provider.KAKAO)
        val principal = provider.parse(token)

        assertEquals(42L, principal.userId)
        assertEquals("KAKAO", principal.provider)
    }

    @Test
    fun `rejects an expired token`() {
        val issued = provider(Clock.fixed(now, ZoneOffset.UTC)).issue(1, Provider.APPLE)

        val later = provider(Clock.fixed(now.plus(Duration.ofMinutes(31)), ZoneOffset.UTC))

        assertThrows(InvalidTokenException::class.java) { later.parse(issued) }
    }

    @Test
    fun `rejects a tampered token`() {
        val provider = provider(Clock.fixed(now, ZoneOffset.UTC))
        val token = provider.issue(1, Provider.KAKAO)
        val tampered = token.dropLast(3) + "abc"

        assertThrows(InvalidTokenException::class.java) { provider.parse(tampered) }
    }

    @Test
    fun `rejects a token signed with a different secret`() {
        val issued = provider(Clock.fixed(now, ZoneOffset.UTC)).issue(1, Provider.KAKAO)
        val other = JwtAccessTokenProvider(
            properties = JwtProperties(secret = "another-completely-different-secret-key-abcdefghij"),
            clock = Clock.fixed(now, ZoneOffset.UTC)
        )

        assertThrows(InvalidTokenException::class.java) { other.parse(issued) }
    }
}
