package com.challa.web.auth

import com.challa.core.auth.LoginCommand
import com.challa.core.auth.LoginResult
import com.challa.core.auth.Provider
import com.challa.core.auth.TokenPair
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
) {
    companion object {
        fun from(result: LoginResult): LoginResponse = LoginResponse(
            accessToken = result.accessToken,
            refreshToken = result.refreshToken,
            isNew = result.isNew
        )
    }
}

data class RefreshRequest(val refreshToken: String)

data class TokenPairResponse(val accessToken: String, val refreshToken: String) {
    companion object {
        fun from(pair: TokenPair): TokenPairResponse =
            TokenPairResponse(accessToken = pair.accessToken, refreshToken = pair.refreshToken)
    }
}

data class LogoutRequest(val refreshToken: String)
