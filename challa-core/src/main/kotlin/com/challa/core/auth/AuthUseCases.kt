package com.challa.core.auth

interface LoginUseCase {
    fun login(command: LoginCommand): LoginResult
}

data class LoginCommand(val provider: Provider, val idToken: String, val authorizationCode: String? = null)

data class LoginResult(val accessToken: String, val refreshToken: String, val isNew: Boolean)

interface RefreshTokenUseCase {
    fun refresh(refreshToken: String): TokenPair
}

data class TokenPair(val accessToken: String, val refreshToken: String)

interface LogoutUseCase {
    fun logout(userId: Long, refreshToken: String)
}
