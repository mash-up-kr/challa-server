package com.challa.externalout.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "oidc")
data class OidcProperties(val kakao: ProviderOidc, val apple: ProviderOidc)

data class ProviderOidc(

    val issuer: String,

    val jwksUri: String,

    val clientId: String,

    val nativeClientId: String? = null
)
