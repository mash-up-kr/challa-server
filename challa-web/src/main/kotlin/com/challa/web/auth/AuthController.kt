package com.challa.web.auth

import com.challa.core.auth.LoginUseCase
import com.challa.core.auth.LogoutUseCase
import com.challa.core.auth.RefreshTokenUseCase
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
    fun login(@RequestBody request: LoginRequest): ApiResponse<LoginResponse> =
        ApiResponse.ok(LoginResponse.from(loginUseCase.login(request.toCommand())))

    @PublicEndpoint
    @PostMapping("/refresh")
    fun refresh(@RequestBody request: RefreshRequest): ApiResponse<TokenPairResponse> =
        ApiResponse.ok(TokenPairResponse.from(refreshTokenUseCase.refresh(request.refreshToken)))

    @PostMapping("/logout")
    fun logout(@AuthUserId userId: Long, @RequestBody request: LogoutRequest): ApiResponse<Unit?> {
        logoutUseCase.logout(userId, request.refreshToken)
        return ApiResponse.empty()
    }
}
