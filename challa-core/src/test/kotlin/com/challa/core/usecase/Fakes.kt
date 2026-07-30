package com.challa.core.usecase

import com.challa.core.domain.Provider
import com.challa.core.domain.RefreshToken
import com.challa.core.domain.User
import com.challa.core.port.outbound.AccessTokenIssuer
import com.challa.core.port.outbound.RandomNicknameGenerator
import com.challa.core.port.outbound.RefreshTokenRepository
import com.challa.core.port.outbound.UserRepository
import java.time.Instant

class FakeUserRepository : UserRepository {
    val store = linkedMapOf<Long, User>()

    var saveCount = 0
        private set

    private var sequence = 0L

    override fun findByProviderAndProviderId(provider: Provider, providerId: String): User? = store.values.firstOrNull {
        it.provider ==
            provider &&
            it.providerId == providerId
    }

    override fun findById(id: Long): User? = store[id]

    override fun save(user: User): User {
        saveCount++
        val id = user.id ?: ++sequence
        val saved = user.copy(id = id, createdAt = user.createdAt ?: Instant.EPOCH)
        store[id] = saved
        return saved
    }

    override fun updateAppleAuthorizationCode(id: Long, authorizationCode: String): User {
        val current = checkNotNull(store[id]) { "User $id no longer exists" }
        val updated = current.copy(appleAuthorizationCode = authorizationCode)
        store[id] = updated
        return updated
    }

    override fun deleteById(id: Long) {
        store.remove(id)
    }
}

open class FakeRefreshTokenRepository : RefreshTokenRepository {
    val store = linkedMapOf<Long, RefreshToken>()
    private var sequence = 0L

    override fun save(token: RefreshToken): RefreshToken {
        val id = token.id ?: ++sequence
        val saved = token.copy(id = id)
        store[id] = saved
        return saved
    }

    override fun findByTokenHash(tokenHash: String): RefreshToken? = store.values.firstOrNull {
        it.tokenHash ==
            tokenHash
    }

    override fun markRevoked(id: Long): Boolean {
        val current = store[id] ?: return false
        if (current.revoked) return false
        store[id] = current.copy(revoked = true, rotatedAt = Instant.EPOCH)
        return true
    }

    override fun revokeAllByUserId(userId: Long) {
        store.filterValues { it.userId == userId && !it.revoked }
            .forEach { (id, token) -> store[id] = token.copy(revoked = true, rotatedAt = Instant.EPOCH) }
    }

    override fun deleteByTokenHash(tokenHash: String) {
        store.values.filter { it.tokenHash == tokenHash }.mapNotNull { it.id }.forEach { store.remove(it) }
    }

    override fun deleteByUserId(userId: Long) {
        store.values.filter { it.userId == userId }.mapNotNull { it.id }.forEach { store.remove(it) }
    }

    fun byHash(hash: String): RefreshToken? = findByTokenHash(hash)
}

class FakeAccessTokenIssuer : AccessTokenIssuer {
    override fun issue(userId: Long, provider: Provider): String = "access-token-for-$userId-$provider"
}

class FakeRandomNicknameGenerator(var next: String? = "용감한 호랑이") : RandomNicknameGenerator {
    override fun generate(): String? = next
}
