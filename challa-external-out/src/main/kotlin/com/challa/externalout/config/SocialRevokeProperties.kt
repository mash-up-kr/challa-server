package com.challa.externalout.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "social-revoke")
data class SocialRevokeProperties(val kakao: KakaoRevoke, val apple: AppleRevoke)

data class KakaoRevoke(val adminKey: String, val unlinkUri: String = "https://kapi.kakao.com/v1/user/unlink")

data class AppleRevoke(

    val clientId: String,
    val teamId: String,
    val keyId: String,

    val privateKey: String,
    val tokenUri: String = "https://appleid.apple.com/auth/token",
    val revokeUri: String = "https://appleid.apple.com/auth/revoke"
)
