package com.challa.core.user

import com.challa.core.auth.Provider

interface UserRepository {
    fun findByProviderAndProviderId(provider: Provider, providerId: String): User?

    fun findById(id: Long): User?

    fun save(user: User): User

    fun updateAppleAuthorizationCode(id: Long, authorizationCode: String): User

    fun deleteById(id: Long)

    fun findAllByIds(userIds: List<Long>): List<User>
}
