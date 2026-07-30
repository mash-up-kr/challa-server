package com.challa.core.port.outbound

import com.challa.core.domain.Provider
import com.challa.core.domain.RefreshToken
import com.challa.core.domain.User

interface UserRepository {
    fun findByProviderAndProviderId(provider: Provider, providerId: String): User?
    fun findById(id: Long): User?
    fun save(user: User): User

    fun updateAppleAuthorizationCode(id: Long, authorizationCode: String): User

    fun deleteById(id: Long)
}

interface RefreshTokenRepository {
    fun save(token: RefreshToken): RefreshToken
    fun findByTokenHash(tokenHash: String): RefreshToken?

    fun markRevoked(id: Long): Boolean

    fun revokeAllByUserId(userId: Long)

    fun deleteByTokenHash(tokenHash: String)
    fun deleteByUserId(userId: Long)
}
