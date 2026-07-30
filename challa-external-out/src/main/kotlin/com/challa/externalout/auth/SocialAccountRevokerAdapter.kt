package com.challa.externalout.auth

import com.challa.core.auth.Provider
import com.challa.core.auth.SocialAccountRevoker
import com.challa.core.user.User
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class SocialAccountRevokerAdapter(
    private val kakaoUnlinkClient: KakaoUnlinkClient,
    private val appleRevokeClient: AppleRevokeClient
) : SocialAccountRevoker {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun revoke(user: User) {
        try {
            when (user.provider) {
                Provider.KAKAO -> kakaoUnlinkClient.unlink(user.providerId)
                Provider.APPLE -> appleRevokeClient.revoke(user.appleAuthorizationCode)
            }
        } catch (ex: Exception) {
            log.warn(
                "Provider account revocation failed for user={} provider={}; proceeding with deletion",
                user.id,
                user.provider,
                ex
            )
        }
    }
}
