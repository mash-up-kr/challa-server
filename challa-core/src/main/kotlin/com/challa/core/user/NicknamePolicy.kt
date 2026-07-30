package com.challa.core.user

import java.text.Normalizer

object NicknamePolicy {
    const val MAX_LENGTH = 20

    fun normalize(raw: String): String {
        val normalized = Normalizer.normalize(raw, Normalizer.Form.NFC)

        val collapsed = BLANK_RUN.replace(normalized, " ").trim()
        if (collapsed.codePoints().anyMatch(::isForbidden)) {
            throw InvalidNicknameException("Nickname must not contain control characters")
        }
        if (collapsed.codePoints().noneMatch(::isVisible)) {
            throw InvalidNicknameException("Nickname must not be blank")
        }
        val length = collapsed.codePointCount(0, collapsed.length)
        if (length > MAX_LENGTH) {
            throw InvalidNicknameException("Nickname must be at most $MAX_LENGTH characters")
        }
        return collapsed
    }

    private val BLANK_RUN = Regex("[\\s\\p{Z}]+")

    private val BLANK_LOOKING = setOf(0x115F, 0x1160, 0x3164, 0xFFA0, 0x2800)

    private fun isForbidden(codePoint: Int): Boolean = when (Character.getType(codePoint)) {
        Character.CONTROL.toInt(),
        Character.FORMAT.toInt(),
        Character.PRIVATE_USE.toInt(),
        Character.SURROGATE.toInt(),
        Character.UNASSIGNED.toInt()
        -> true

        else -> false
    }

    private fun isVisible(codePoint: Int): Boolean = !Character.isWhitespace(codePoint) &&
        !Character.isSpaceChar(codePoint) &&
        codePoint !in BLANK_LOOKING
}
