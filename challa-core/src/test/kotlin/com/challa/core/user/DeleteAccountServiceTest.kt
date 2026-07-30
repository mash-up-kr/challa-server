package com.challa.core.user

import com.challa.core.FakeRefreshTokenRepository
import com.challa.core.FakeUserRepository
import com.challa.core.auth.Provider
import com.challa.core.auth.RefreshToken
import com.challa.core.auth.SocialAccountRevoker
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant

class DeleteAccountServiceTest {
    private lateinit var users: FakeUserRepository
    private lateinit var tokens: FakeRefreshTokenRepository
    private lateinit var revoker: SocialAccountRevoker
    private lateinit var service: DeleteAccountService

    @BeforeEach
    fun setUp() {
        users = FakeUserRepository()
        tokens = FakeRefreshTokenRepository()
        revoker = mockk(relaxed = true)
        service = DeleteAccountService(users, tokens, revoker)
    }

    private fun seedUser() {
        users.save(User(id = 1, provider = Provider.KAKAO, providerId = "sub-1"))
        tokens.save(RefreshToken(userId = 1, tokenHash = "h1", expiresAt = Instant.MAX))
        tokens.save(RefreshToken(userId = 1, tokenHash = "h2", expiresAt = Instant.MAX))
    }

    @Test
    fun `revokes provider link and deletes user with all tokens`() {
        seedUser()

        service.delete(1)

        verify(exactly = 1) { revoker.revoke(any()) }
        assertNull(users.findById(1))
        assertTrue(tokens.store.values.none { it.userId == 1L })
    }

    @Test
    fun `throws when the user does not exist`() {
        assertThrows(UserNotFoundException::class.java) { service.delete(999) }
    }
}
