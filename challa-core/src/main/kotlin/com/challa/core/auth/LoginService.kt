package com.challa.core.auth

import com.challa.core.user.User
import com.challa.core.user.UserRepository
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
