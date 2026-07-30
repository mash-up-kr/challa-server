package com.challa.core.port.outbound

import com.challa.core.domain.OidcIdentity
import com.challa.core.domain.Provider
import com.challa.core.domain.User

interface OidcTokenVerifier {
    fun verify(provider: Provider, idToken: String): OidcIdentity
}

interface SocialAccountRevoker {
    fun revoke(user: User)
}

interface AccessTokenIssuer {
    fun issue(userId: Long, provider: Provider): String
}

interface RandomNicknameGenerator {
    fun generate(): String?
}
