package com.challa.persistence.user

import com.challa.core.auth.Provider
import com.challa.core.user.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.PreUpdate
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.Instant

@Entity
@Table(
    name = "users",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_users_provider_provider_id",
            columnNames = ["provider", "provider_id"]
        )
    ]
)
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false, length = 20)
    var provider: Provider,

    @Column(name = "provider_id", nullable = false)
    var providerId: String,

    @Column(name = "nickname")
    var nickname: String? = null,

    @Column(name = "profile_image_url", columnDefinition = "text")
    var profileImageUrl: String? = null,

    @Column(name = "apple_authorization_code", columnDefinition = "text")
    var appleAuthorizationCode: String? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
) {
    @PreUpdate
    fun onUpdate() {
        updatedAt = Instant.now()
    }

    fun toDomain(): User = User(
        id = id,
        provider = provider,
        providerId = providerId,
        nickname = nickname,
        profileImageUrl = profileImageUrl,
        appleAuthorizationCode = appleAuthorizationCode,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
