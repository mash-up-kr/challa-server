package com.challa.core.domain

import com.challa.core.exception.InvalidNicknameException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class NicknamePolicyTest {
    @Test
    fun `trims and collapses whitespace`() {
        assertEquals("용감한 호랑이", NicknamePolicy.normalize("  용감한   호랑이 "))
        assertEquals("a b", NicknamePolicy.normalize("a\t\nb"))
    }

    @Test
    fun `collapses unicode separators the ASCII whitespace class misses`() {
        assertEquals("가 나", NicknamePolicy.normalize("가${cp(IDEOGRAPHIC_SPACE, IDEOGRAPHIC_SPACE)}나"))
        assertEquals("가 나", NicknamePolicy.normalize("${cp(NBSP)}가${cp(NBSP)}나${cp(NBSP)}"))
    }

    @Test
    fun `normalizes to NFC so equal-looking nicknames are stored identically`() {
        val decomposed = cp(HANGUL_G, HANGUL_A)

        assertEquals("가", NicknamePolicy.normalize(decomposed))
    }

    @Test
    fun `accepts the maximum length`() {
        val max = "가".repeat(NicknamePolicy.MAX_LENGTH)

        assertEquals(max, NicknamePolicy.normalize(max))
    }

    @Test
    fun `counts length in code points so one emoji costs one character`() {
        val emoji = cp(TIGER_FACE).repeat(NicknamePolicy.MAX_LENGTH)

        assertEquals(emoji, NicknamePolicy.normalize(emoji))
        assertThrows(InvalidNicknameException::class.java) {
            NicknamePolicy.normalize(cp(TIGER_FACE).repeat(NicknamePolicy.MAX_LENGTH + 1))
        }
    }

    @Test
    fun `rejects blank values`() {
        listOf("", " ", "\t\n", cp(IDEOGRAPHIC_SPACE), cp(NBSP)).forEach { raw ->
            assertThrows(InvalidNicknameException::class.java) { NicknamePolicy.normalize(raw) }
        }
    }

    @Test
    fun `rejects invisible values`() {
        listOf(
            cp(ZERO_WIDTH_SPACE),
            cp(ZERO_WIDTH_NO_BREAK_SPACE),
            cp(ZERO_WIDTH_JOINER),
            cp(HANGUL_FILLER),
            cp(HANGUL_CHOSEONG_FILLER),
            cp(BRAILLE_BLANK),
            cp(ZERO_WIDTH_SPACE, HANGUL_FILLER)
        ).forEach { raw ->
            assertThrows(InvalidNicknameException::class.java) { NicknamePolicy.normalize(raw) }
        }
    }

    @Test
    fun `rejects control and bidi characters mixed into a visible value`() {
        listOf(
            "a${cp(BELL)}b",

            "a${cp(NUL)}b",
            "${cp(RIGHT_TO_LEFT_OVERRIDE)}abc"
        ).forEach { raw ->
            assertThrows(InvalidNicknameException::class.java) { NicknamePolicy.normalize(raw) }
        }
    }

    @Test
    fun `rejects values over the maximum length`() {
        assertThrows(InvalidNicknameException::class.java) {
            NicknamePolicy.normalize("가".repeat(NicknamePolicy.MAX_LENGTH + 1))
        }
    }

    @Test
    fun `length is measured after normalization`() {
        val padded = " " + "가".repeat(NicknamePolicy.MAX_LENGTH) + " "

        assertEquals(NicknamePolicy.MAX_LENGTH, NicknamePolicy.normalize(padded).length)
    }

    private fun cp(vararg codePoints: Int): String = codePoints.joinToString("") { String(Character.toChars(it)) }

    private companion object {
        const val NUL = 0x0000
        const val BELL = 0x0007
        const val NBSP = 0x00A0
        const val HANGUL_G = 0x1100
        const val HANGUL_A = 0x1161
        const val HANGUL_CHOSEONG_FILLER = 0x115F
        const val ZERO_WIDTH_SPACE = 0x200B
        const val ZERO_WIDTH_JOINER = 0x200D
        const val RIGHT_TO_LEFT_OVERRIDE = 0x202E
        const val BRAILLE_BLANK = 0x2800
        const val IDEOGRAPHIC_SPACE = 0x3000
        const val HANGUL_FILLER = 0x3164
        const val ZERO_WIDTH_NO_BREAK_SPACE = 0xFEFF
        const val TIGER_FACE = 0x1F42F
    }
}
