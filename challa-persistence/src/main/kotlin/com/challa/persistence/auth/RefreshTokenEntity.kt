package com.challa.persistence.auth

import com.challa.core.auth.RefreshToken
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.Instant

@Entity
@Table(
    name = "refresh_tokens",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_refresh_tokens_token_hash", columnNames = ["token_hash"])
    ],
    indexes = [
        Index(name = "idx_refresh_tokens_user_id", columnList = "user_id")
    ]
)
class RefreshTokenEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "user_id", nullable = false)
    var userId: Long,

    @Column(name = "token_hash", nullable = false, length = 128)
    var tokenHash: String,

    @Column(name = "expires_at", nullable = false)
    var expiresAt: Instant,

    @Column(name = "revoked", nullable = false)
    var revoked: Boolean = false,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "rotated_at")
    var rotatedAt: Instant? = null
) {
    fun toDomain(): RefreshToken = RefreshToken(
        id = id,
        userId = userId,
        tokenHash = tokenHash,
        expiresAt = expiresAt,
        revoked = revoked,
        createdAt = createdAt,
        rotatedAt = rotatedAt
    )
}
