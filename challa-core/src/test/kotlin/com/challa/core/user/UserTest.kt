package com.challa.core.user

import com.challa.core.auth.Provider
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UserTest {
    @Test
    fun `uses the nickname as the display nickname when it exists`() {
        assertEquals("라이언", User.displayNicknameOf(user(nickname = "라이언")))
    }

    @Test
    fun `uses the fallback display nickname when the nickname is missing`() {
        assertEquals(FALLBACK_DISPLAY_NICKNAME, User.displayNicknameOf(user(nickname = null)))
    }

    @Test
    fun `uses the fallback display nickname when the nickname is blank`() {
        assertEquals(FALLBACK_DISPLAY_NICKNAME, User.displayNicknameOf(user(nickname = "   ")))
    }

    @Test
    fun `uses the fallback display nickname when the user does not exist`() {
        val user: User? = null

        assertEquals(FALLBACK_DISPLAY_NICKNAME, User.displayNicknameOf(user))
    }

    private fun user(nickname: String?) = User(
        id = 1L,
        provider = Provider.KAKAO,
        providerId = "provider-id",
        nickname = nickname
    )

    private companion object {
        const val FALLBACK_DISPLAY_NICKNAME = "탈퇴한 사용자"
    }
}
