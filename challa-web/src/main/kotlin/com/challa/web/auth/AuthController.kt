package com.challa.web.auth

import com.challa.core.port.inbound.LoginUseCase
import com.challa.core.port.inbound.LogoutUseCase
import com.challa.core.port.inbound.RefreshTokenUseCase
import com.challa.web.common.response.ApiResponse
import com.challa.web.security.AuthUserId
import com.challa.web.security.PublicEndpoint
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val loginUseCase: LoginUseCase,
    private val refreshTokenUseCase: RefreshTokenUseCase,
    private val logoutUseCase: LogoutUseCase
) {

    @PublicEndpoint
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ApiResponse<LoginResponse> {
        val result = loginUseCase.login(request.toCommand())
        return ApiResponse.ok(
            data = LoginResponse(result.accessToken, result.refreshToken, result.isNew),
            message = "Login successful"
        )
    }

    @PublicEndpoint
    @PostMapping("/refresh")
    fun refresh(@RequestBody request: RefreshRequest): ApiResponse<TokenPairResponse> {
        val pair = refreshTokenUseCase.refresh(request.refreshToken)
        return ApiResponse.ok(
            data = TokenPairResponse(pair.accessToken, pair.refreshToken),
            message = "Token refreshed"
        )
    }

    @PostMapping("/logout")
    fun logout(@AuthUserId userId: Long, @RequestBody request: LogoutRequest): ApiResponse<Unit?> {
        logoutUseCase.logout(userId, request.refreshToken)
        return ApiResponse.empty("Logged out")
    }
}
