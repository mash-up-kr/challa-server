package com.challa.core.auth

import com.challa.core.FakeAccessTokenIssuer
import com.challa.core.FakeRefreshTokenRepository
import com.challa.core.FakeUserRepository
import com.challa.core.user.UpdateProfileCommand
import com.challa.core.user.UpdateProfileService
import com.challa.core.user.User
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset

class LoginServiceTest {
    private val now = Instant.parse("2026-07-12T00:00:00Z")
    private val clock = Clock.fixed(now, ZoneOffset.UTC)
    private val factory = RefreshTokenFactory()

    private lateinit var users: FakeUserRepository
    private lateinit var tokens: FakeRefreshTokenRepository
    private lateinit var verifier: OidcTokenVerifier
    private lateinit var service: LoginService

    @BeforeEach
    fun setUp() {
        users = FakeUserRepository()
        tokens = FakeRefreshTokenRepository()
        verifier = mockk()
        service = LoginService(
            oidcTokenVerifier = verifier,
            userRepository = users,
            refreshTokenRepository = tokens,
            accessTokenIssuer = FakeAccessTokenIssuer(),
            refreshTokenFactory = factory,
            clock = clock,
            refreshTokenTtl = Duration.ofDays(30)
        )
    }

    @Test
    fun `creates a new user with an empty nickname and stores a refresh token`() {
        every { verifier.verify(Provider.KAKAO, "id-token") } returns
            OidcIdentity(
                Provider.KAKAO,
                subject = "kakao-sub",
                profileImageUrl = "https://img.example/1.png"
            )

        val result = service.login(LoginCommand(Provider.KAKAO, "id-token"))

        assertTrue(result.isNew)
        assertTrue(result.accessToken.isNotBlank())
        val user = users.findByProviderAndProviderId(Provider.KAKAO, "kakao-sub")!!

        assertNull(user.nickname)
        assertEquals("https://img.example/1.png", user.profileImageUrl)

        assertEquals(1, tokens.store.values.count { it.userId == user.id })
        assertEquals(factory.hash(result.refreshToken), tokens.store.values.first().tokenHash)
    }

    @Test
    fun `returning user without a nickname is still flagged as new on every login`() {
        users.save(existingUser(nickname = null))
        every { verifier.verify(Provider.APPLE, "id-token") } returns
            OidcIdentity(Provider.APPLE, subject = "apple-sub")

        val first = service.login(LoginCommand(Provider.APPLE, "id-token"))
        val second = service.login(LoginCommand(Provider.APPLE, "id-token"))

        assertTrue(first.isNew)
        assertTrue(second.isNew)
        assertNull(users.findById(1)!!.nickname)
    }

    @Test
    fun `a blank nickname counts as no nickname`() {
        users.save(existingUser(nickname = "   "))
        every { verifier.verify(Provider.APPLE, "id-token") } returns
            OidcIdentity(Provider.APPLE, subject = "apple-sub")

        assertTrue(service.login(LoginCommand(Provider.APPLE, "id-token")).isNew)
    }

    @Test
    fun `user with a nickname is not flagged as new and Apple auth code is refreshed`() {
        users.save(existingUser(nickname = "골라진 닉네임"))
        every { verifier.verify(Provider.APPLE, "id-token") } returns
            OidcIdentity(Provider.APPLE, subject = "apple-sub")

        val result = service.login(
            LoginCommand(
                provider = Provider.APPLE,
                idToken = "id-token",
                authorizationCode = "auth-code-123"
            )
        )

        assertFalse(result.isNew)
        val user = users.findById(1)!!
        assertEquals("골라진 닉네임", user.nickname)
        assertEquals("auth-code-123", user.appleAuthorizationCode)
    }

    @Test
    fun `isNew turns false only after the profile update sets a nickname`() {
        every { verifier.verify(Provider.KAKAO, "id-token") } returns
            OidcIdentity(Provider.KAKAO, subject = "kakao-sub")

        assertTrue(service.login(LoginCommand(Provider.KAKAO, "id-token")).isNew)
        val userId = users.findByProviderAndProviderId(Provider.KAKAO, "kakao-sub")!!.id!!

        UpdateProfileService(users).update(userId, UpdateProfileCommand("호랑이", null))

        assertFalse(service.login(LoginCommand(Provider.KAKAO, "id-token")).isNew)
    }

    @Test
    fun `login never writes the profile, so a chosen nickname cannot be lost`() {
        every { verifier.verify(Provider.KAKAO, "id-token") } returns
            OidcIdentity(Provider.KAKAO, subject = "kakao-sub", profileImageUrl = "https://img.example/1.png")
        users.save(
            User(
                id = 2,
                provider = Provider.KAKAO,
                providerId = "kakao-sub",
                nickname = "내가 고른 닉네임",

                profileImageUrl = null
            )
        )
        val writesBefore = users.saveCount

        service.login(LoginCommand(Provider.KAKAO, "id-token"))

        assertEquals(writesBefore, users.saveCount)
        val user = users.findById(2)!!
        assertEquals("내가 고른 닉네임", user.nickname)
        assertNull(user.profileImageUrl)
    }

    @Test
    fun `propagates verification failure`() {
        every { verifier.verify(any(), any()) } throws InvalidTokenException()
        assertThrows(InvalidTokenException::class.java) {
            service.login(LoginCommand(Provider.KAKAO, "bad-token"))
        }
    }

    private fun existingUser(nickname: String?) = User(
        id = 1,
        provider = Provider.APPLE,
        providerId = "apple-sub",
        nickname = nickname,
        profileImageUrl = null
    )
}
