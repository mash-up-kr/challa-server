package com.challa.web.security

class UnauthenticatedException(message: String = "Authentication required") : RuntimeException(message)
