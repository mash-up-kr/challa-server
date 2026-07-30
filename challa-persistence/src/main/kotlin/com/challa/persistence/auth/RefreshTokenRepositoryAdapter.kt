package com.challa.persistence.auth

import com.challa.core.auth.RefreshToken
import com.challa.core.auth.RefreshTokenRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Clock

@Component
class RefreshTokenRepositoryAdapter(private val jpa: RefreshTokenJpaRepository, private val clock: Clock) :
    RefreshTokenRepository {

    @Transactional
    override fun save(token: RefreshToken): RefreshToken {
        val tokenId = token.id
        val entity = if (tokenId != null) {
            jpa.findById(tokenId).orElseThrow {
                IllegalStateException("Refresh token $tokenId no longer exists")
            }.apply {
                expiresAt = token.expiresAt
                revoked = token.revoked
                rotatedAt = token.rotatedAt
            }
        } else {
            RefreshTokenEntity(
                userId = token.userId,
                tokenHash = token.tokenHash,
                expiresAt = token.expiresAt,
                revoked = token.revoked,
                rotatedAt = token.rotatedAt
            )
        }
        return jpa.save(entity).toDomain()
    }

    @Transactional(readOnly = true)
    override fun findByTokenHash(tokenHash: String): RefreshToken? = jpa.findByTokenHash(tokenHash)?.toDomain()

    @Transactional
    override fun markRevoked(id: Long): Boolean = jpa.markRevoked(id, clock.instant()) == 1

    @Transactional
    override fun revokeAllByUserId(userId: Long) {
        jpa.revokeAllByUserId(userId, clock.instant())
    }

    @Transactional
    override fun deleteByTokenHash(tokenHash: String) {
        jpa.deleteByTokenHash(tokenHash)
    }

    @Transactional
    override fun deleteByUserId(userId: Long) {
        jpa.deleteByUserId(userId)
    }
}
