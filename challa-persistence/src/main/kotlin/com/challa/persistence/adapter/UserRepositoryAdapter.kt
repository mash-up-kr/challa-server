package com.challa.persistence.adapter

import com.challa.core.domain.Provider
import com.challa.core.domain.User
import com.challa.core.port.outbound.UserRepository
import com.challa.persistence.entity.UserEntity
import com.challa.persistence.repository.UserJpaRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class UserRepositoryAdapter(private val jpa: UserJpaRepository) : UserRepository {
    @Transactional(readOnly = true)
    override fun findByProviderAndProviderId(provider: Provider, providerId: String): User? =
        jpa.findByProviderAndProviderId(provider, providerId)?.toDomain()

    @Transactional(readOnly = true)
    override fun findById(id: Long): User? = jpa.findById(id).orElse(null)?.toDomain()

    @Transactional
    override fun save(user: User): User {
        val userId = user.id
        val entity = if (userId != null) {
            jpa.findById(userId).orElseThrow {
                IllegalStateException("User $userId no longer exists")
            }.apply {
                nickname = user.nickname
                profileImageUrl = user.profileImageUrl
                appleAuthorizationCode = user.appleAuthorizationCode
            }
        } else {
            UserEntity(
                provider = user.provider,
                providerId = user.providerId,
                nickname = user.nickname,
                profileImageUrl = user.profileImageUrl,
                appleAuthorizationCode = user.appleAuthorizationCode
            )
        }
        return jpa.save(entity).toDomain()
    }

    @Transactional
    override fun updateAppleAuthorizationCode(id: Long, authorizationCode: String): User {
        val entity = jpa.findById(id).orElseThrow {
            IllegalStateException("User $id no longer exists")
        }
        entity.appleAuthorizationCode = authorizationCode
        return jpa.save(entity).toDomain()
    }

    @Transactional
    override fun deleteById(id: Long) {
        jpa.deleteById(id)
    }
}
