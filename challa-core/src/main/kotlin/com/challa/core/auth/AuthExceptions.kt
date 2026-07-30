package com.challa.core.auth

sealed class AuthException(message: String) : RuntimeException(message)

class InvalidTokenException(message: String = "Invalid token") : AuthException(message)

class InvalidIdTokenException(message: String = "Invalid id_token") : AuthException(message)

class TokenReuseDetectedException(message: String = "Refresh token reuse detected") : AuthException(message)
