package com.challa.core.auth

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
