package com.challa.core.usecase

import com.challa.core.domain.OidcIdentity
import com.challa.core.domain.Provider
import com.challa.core.domain.RefreshToken
import com.challa.core.domain.User
import com.challa.core.port.inbound.LoginCommand
import com.challa.core.port.inbound.LoginResult
import com.challa.core.port.inbound.LoginUseCase
import com.challa.core.port.outbound.AccessTokenIssuer
import com.challa.core.port.outbound.OidcTokenVerifier
import com.challa.core.port.outbound.RefreshTokenRepository
import com.challa.core.port.outbound.UserRepository
import com.challa.core.token.RefreshTokenFactory
import java.time.Clock
import java.time.Duration

class LoginService(
    private val oidcTokenVerifier: OidcTokenVerifier,
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val accessTokenIssuer: AccessTokenIssuer,
    private val refreshTokenFactory: RefreshTokenFactory,
    private val clock: Clock,
    private val refreshTokenTtl: Duration
) : LoginUseCase {
    override fun login(command: LoginCommand): LoginResult {
        val identity = oidcTokenVerifier.verify(command.provider, command.idToken)

        val existing = userRepository.findByProviderAndProviderId(command.provider, identity.subject)
        val user = if (existing == null) {
            userRepository.save(newUser(command, identity))
        } else {
            refreshAppleAuthorizationCode(existing, command)
        }

        val userId = requireNotNull(user.id) { "Persisted user must have an id" }
        val accessToken = accessTokenIssuer.issue(userId, user.provider)
        val generated = refreshTokenFactory.generate()
        refreshTokenRepository.save(
            RefreshToken(
                userId = userId,
                tokenHash = generated.hash,
                expiresAt = clock.instant().plus(refreshTokenTtl)
            )
        )

        return LoginResult(accessToken, generated.rawValue, isNew = !user.hasNickname)
    }

    private fun newUser(command: LoginCommand, identity: OidcIdentity): User = User(
        provider = command.provider,
        providerId = identity.subject,
        nickname = null,
        profileImageUrl = identity.profileImageUrl,
        appleAuthorizationCode = command.authorizationCode.takeIf { command.provider == Provider.APPLE }
    )

    private fun refreshAppleAuthorizationCode(existing: User, command: LoginCommand): User {
        val code = command.authorizationCode?.takeIf { command.provider == Provider.APPLE }
        if (code == null || code == existing.appleAuthorizationCode) return existing
        val userId = requireNotNull(existing.id) { "Persisted user must have an id" }
        return userRepository.updateAppleAuthorizationCode(userId, code)
    }
}
