package com.challa.core.auth

import com.challa.core.user.User

interface OidcTokenVerifier {
    fun verify(provider: Provider, idToken: String): OidcIdentity
}

interface SocialAccountRevoker {
    fun revoke(user: User)
}

interface AccessTokenIssuer {
    fun issue(userId: Long, provider: Provider): String
}

interface RefreshTokenRepository {
    fun save(token: RefreshToken): RefreshToken

    fun findByTokenHash(tokenHash: String): RefreshToken?

    fun markRevoked(id: Long): Boolean

    fun revokeAllByUserId(userId: Long)

    fun deleteByTokenHash(tokenHash: String)

    fun deleteByUserId(userId: Long)
}
