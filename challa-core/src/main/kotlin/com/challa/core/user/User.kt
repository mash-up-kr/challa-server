package com.challa.core.user

import com.challa.core.auth.Provider
import java.time.Instant

private const val FALLBACK_NICKNAME = "알 수 없는 사용자"

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

    companion object {
        fun displayNicknameOf(user: User?): String = user?.nickname?.takeIf { it.isNotBlank() } ?: FALLBACK_NICKNAME
    }
}
