package com.challa.persistence.auth

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.Instant

interface RefreshTokenJpaRepository : JpaRepository<RefreshTokenEntity, Long> {
    fun findByTokenHash(tokenHash: String): RefreshTokenEntity?

    fun deleteByTokenHash(tokenHash: String)

    fun deleteByUserId(userId: Long)

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
        "update RefreshTokenEntity t " +
            "set t.revoked = true, t.rotatedAt = :now " +
            "where t.userId = :userId and t.revoked = false"
    )
    fun revokeAllByUserId(@Param("userId") userId: Long, @Param("now") now: Instant)

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
        "update RefreshTokenEntity t " +
            "set t.revoked = true, t.rotatedAt = :now " +
            "where t.id = :id and t.revoked = false"
    )
    fun markRevoked(@Param("id") id: Long, @Param("now") now: Instant): Int
}
