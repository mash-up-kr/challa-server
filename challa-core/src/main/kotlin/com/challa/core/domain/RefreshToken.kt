package com.challa.core.domain

import java.time.Instant

data class RefreshToken(
    val id: Long? = null,
    val userId: Long,
    val tokenHash: String,
    val expiresAt: Instant,
    val revoked: Boolean = false,
    val createdAt: Instant? = null,
    val rotatedAt: Instant? = null
)
