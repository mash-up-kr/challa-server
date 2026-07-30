package com.challa.core.domain

import java.time.Instant

data class User(
    val id: Long? = null,
    val provider: Provider,
    val providerId: String,
    val nickname: String? = null,
    val profileImageUrl: String? = null,
    val appleAuthorizationCode: String? = null,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
) {
    val hasNickname: Boolean get() = !nickname.isNullOrBlank()
}
