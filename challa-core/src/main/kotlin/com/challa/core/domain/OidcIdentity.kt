package com.challa.core.domain

data class OidcIdentity(val provider: Provider, val subject: String, val profileImageUrl: String? = null)
