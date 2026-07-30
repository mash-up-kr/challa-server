package com.challa.core.usecase

import com.challa.core.port.inbound.LogoutUseCase
import com.challa.core.port.outbound.RefreshTokenRepository
import com.challa.core.token.RefreshTokenFactory

class LogoutService(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val refreshTokenFactory: RefreshTokenFactory
) : LogoutUseCase {
    override fun logout(userId: Long, refreshToken: String) {
        val hash = refreshTokenFactory.hash(refreshToken)
        val stored = refreshTokenRepository.findByTokenHash(hash) ?: return

        if (stored.userId != userId) return
        refreshTokenRepository.deleteByTokenHash(hash)
    }
}
