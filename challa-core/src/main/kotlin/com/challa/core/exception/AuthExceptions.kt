package com.challa.core.exception

sealed class AuthException(message: String) : RuntimeException(message)

class InvalidTokenException(message: String = "Invalid token") : AuthException(message)

class InvalidIdTokenException(message: String = "Invalid id_token") : AuthException(message)

class TokenReuseDetectedException(message: String = "Refresh token reuse detected") : AuthException(message)

class UserNotFoundException(message: String = "User not found") : AuthException(message)
