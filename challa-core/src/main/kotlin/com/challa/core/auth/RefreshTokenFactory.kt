package com.challa.core.auth

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import java.util.HexFormat

class RefreshTokenFactory(
    private val secureRandom: SecureRandom = SecureRandom(),
    private val tokenByteLength: Int = 32
) {
    init {
        require(tokenByteLength >= 32) { "Refresh token must carry at least 256 bits of entropy" }
    }

    fun generate(): GeneratedRefreshToken {
        val bytes = ByteArray(tokenByteLength)
        secureRandom.nextBytes(bytes)
        val raw = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
        return GeneratedRefreshToken(rawValue = raw, hash = hash(raw))
    }

    fun hash(rawValue: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashed = digest.digest(rawValue.toByteArray(StandardCharsets.UTF_8))
        return HexFormat.of().formatHex(hashed)
    }
}

data class GeneratedRefreshToken(val rawValue: String, val hash: String)
