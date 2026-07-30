package com.challa.core.auth

data class OidcIdentity(val provider: Provider, val subject: String, val profileImageUrl: String? = null)
