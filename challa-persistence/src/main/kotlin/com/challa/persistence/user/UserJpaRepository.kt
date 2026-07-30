package com.challa.persistence.user

import com.challa.core.auth.Provider
import org.springframework.data.jpa.repository.JpaRepository

interface UserJpaRepository : JpaRepository<UserEntity, Long> {
    fun findByProviderAndProviderId(provider: Provider, providerId: String): UserEntity?
}
