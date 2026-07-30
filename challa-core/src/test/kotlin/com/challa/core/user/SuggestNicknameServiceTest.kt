package com.challa.core.user

import com.challa.core.FakeRandomNicknameGenerator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class SuggestNicknameServiceTest {
    private val generator = FakeRandomNicknameGenerator()
    private val service = SuggestNicknameService(generator)

    @Test
    fun `returns a generated nickname`() {
        assertEquals("용감한 호랑이", service.suggest())
    }

    @Test
    fun `fails when the nickname source is empty`() {
        generator.next = null

        assertThrows(NicknameSuggestionUnavailableException::class.java) { service.suggest() }
    }

    @Test
    fun `fails instead of suggesting a nickname the profile update would reject`() {
        generator.next = "가".repeat(NicknamePolicy.MAX_LENGTH + 1)

        assertThrows(NicknameSuggestionUnavailableException::class.java) { service.suggest() }
    }

    @Test
    fun `normalizes what the generator returns`() {
        generator.next = "  용감한   호랑이 "

        assertEquals("용감한 호랑이", service.suggest())
    }
}
