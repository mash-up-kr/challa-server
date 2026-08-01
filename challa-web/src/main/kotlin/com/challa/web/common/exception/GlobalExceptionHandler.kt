package com.challa.web.common.exception

import com.challa.core.auth.InvalidTokenException
import com.challa.core.auth.TokenReuseDetectedException
import com.challa.core.user.InvalidProfileException
import com.challa.core.user.UserNotFoundException
import com.challa.web.common.response.ApiResponse
import com.challa.web.security.UnauthenticatedException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.resource.NoResourceFoundException

@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(InvalidTokenException::class, TokenReuseDetectedException::class)
    fun handleInvalidToken(ex: RuntimeException): ResponseEntity<ApiResponse<Unit?>> {
        log.warn("Token verification failed: {}", ex.message)
        return respond(HttpStatus.UNAUTHORIZED, "Invalid or expired token")
    }

    @ExceptionHandler(UnauthenticatedException::class)
    fun handleUnauthenticated(ex: UnauthenticatedException): ResponseEntity<ApiResponse<Unit?>> =
        respond(HttpStatus.UNAUTHORIZED, "Authentication required")

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFound(ex: UserNotFoundException): ResponseEntity<ApiResponse<Unit?>> =
        respond(HttpStatus.NOT_FOUND, "User not found")

    @ExceptionHandler(InvalidProfileException::class)
    fun handleInvalidProfile(ex: InvalidProfileException): ResponseEntity<ApiResponse<Unit?>> =
        respond(HttpStatus.BAD_REQUEST, ex.message ?: "Invalid profile")

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleUnreadable(ex: HttpMessageNotReadableException): ResponseEntity<ApiResponse<Unit?>> =
        respond(HttpStatus.BAD_REQUEST, "Malformed or invalid request body")

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoResource(ex: NoResourceFoundException): ResponseEntity<ApiResponse<Unit?>> =
        respond(HttpStatus.NOT_FOUND, "Resource not found")

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleMethodNotSupported(ex: HttpRequestMethodNotSupportedException): ResponseEntity<ApiResponse<Unit?>> =
        respond(HttpStatus.METHOD_NOT_ALLOWED, "Method not allowed")

    @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
    fun handleMediaTypeNotSupported(ex: HttpMediaTypeNotSupportedException): ResponseEntity<ApiResponse<Unit?>> =
        respond(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported media type")

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(ex: Exception): ResponseEntity<ApiResponse<Unit?>> {
        log.error("Unhandled exception", ex)
        return respond(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error")
    }

    private fun respond(status: HttpStatus, message: String): ResponseEntity<ApiResponse<Unit?>> =
        ResponseEntity.status(status).body(ApiResponse.error(message))
}
