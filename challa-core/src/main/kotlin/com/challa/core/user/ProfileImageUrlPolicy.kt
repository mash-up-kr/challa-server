package com.challa.core.user

object ProfileImageUrlPolicy {
    const val MAX_LENGTH = 2048

    fun normalizeOrNull(raw: String?): String? {
        val trimmed = raw?.trim()?.takeIf { it.isNotEmpty() } ?: return null
        if (trimmed.length > MAX_LENGTH) {
            throw InvalidProfileImageUrlException(
                "Profile image url must be at most $MAX_LENGTH characters"
            )
        }
        if (trimmed.codePoints().anyMatch { Character.isWhitespace(it) || Character.isISOControl(it) }) {
            throw InvalidProfileImageUrlException("Profile image url must not contain whitespace")
        }
        if (SCHEME.matches(trimmed).not()) {
            throw InvalidProfileImageUrlException("Profile image url must start with http:// or https://")
        }
        return trimmed
    }

    private val SCHEME = Regex("^https?://\\S+$", RegexOption.IGNORE_CASE)
}
