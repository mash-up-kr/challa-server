package com.challa.web.auth

import com.challa.core.domain.Provider
import com.challa.core.port.inbound.LoginCommand
import com.fasterxml.jackson.annotation.JsonProperty

data class LoginRequest(val provider: Provider, val idToken: String, val authorizationCode: String? = null) {
    fun toCommand(): LoginCommand = LoginCommand(
        provider = provider,
        idToken = idToken,
        authorizationCode = authorizationCode
    )
}

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    @get:JsonProperty("isNew") val isNew: Boolean
)

data class RefreshRequest(val refreshToken: String)

data class TokenPairResponse(val accessToken: String, val refreshToken: String)

data class LogoutRequest(val refreshToken: String)
