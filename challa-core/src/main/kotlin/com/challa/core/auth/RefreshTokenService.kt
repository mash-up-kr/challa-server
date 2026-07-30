package com.challa.core.auth

import com.challa.core.user.UserNotFoundException
import com.challa.core.user.UserRepository
import java.time.Clock
import java.time.Duration

class RefreshTokenService(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val userRepository: UserRepository,
    private val accessTokenIssuer: AccessTokenIssuer,
    private val refreshTokenFactory: RefreshTokenFactory,
    private val clock: Clock,
    private val refreshTokenTtl: Duration
) : RefreshTokenUseCase {
    override fun refresh(refreshToken: String): TokenPair {
        val hash = refreshTokenFactory.hash(refreshToken)
        val stored = refreshTokenRepository.findByTokenHash(hash)
            ?: throw InvalidTokenException("Unknown refresh token")

        if (stored.revoked) {
            refreshTokenRepository.revokeAllByUserId(stored.userId)
            throw TokenReuseDetectedException()
        }

        val now = clock.instant()
        if (!stored.expiresAt.isAfter(now)) {
            throw InvalidTokenException("Expired refresh token")
        }

        val user = userRepository.findById(stored.userId)
            ?: throw UserNotFoundException()
        val userId = requireNotNull(user.id)

        val claimed = refreshTokenRepository.markRevoked(
            requireNotNull(stored.id) { "Stored token must have an id" }
        )
        if (!claimed) {
            refreshTokenRepository.revokeAllByUserId(stored.userId)
            throw TokenReuseDetectedException()
        }
        val generated = refreshTokenFactory.generate()
        refreshTokenRepository.save(
            RefreshToken(
                userId = userId,
                tokenHash = generated.hash,
                expiresAt = now.plus(refreshTokenTtl)
            )
        )

        val accessToken = accessTokenIssuer.issue(userId, user.provider)
        return TokenPair(accessToken, generated.rawValue)
    }
}
