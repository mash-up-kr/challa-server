package com.challa.bootstrap.config

import com.challa.core.auth.AccessTokenIssuer
import com.challa.core.auth.LoginService
import com.challa.core.auth.LoginUseCase
import com.challa.core.auth.LogoutService
import com.challa.core.auth.LogoutUseCase
import com.challa.core.auth.OidcTokenVerifier
import com.challa.core.auth.RefreshTokenFactory
import com.challa.core.auth.RefreshTokenRepository
import com.challa.core.auth.RefreshTokenService
import com.challa.core.auth.RefreshTokenUseCase
import com.challa.core.auth.SocialAccountRevoker
import com.challa.core.user.DeleteAccountService
import com.challa.core.user.DeleteAccountUseCase
import com.challa.core.user.GetProfileService
import com.challa.core.user.GetProfileUseCase
import com.challa.core.user.UpdateProfileService
import com.challa.core.user.UpdateProfileUseCase
import com.challa.core.user.UserRepository
import com.challa.web.security.JwtProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock

@Configuration
class CoreBeansConfig {
    @Bean
    fun clock(): Clock = Clock.systemUTC()

    @Bean
    fun refreshTokenFactory(): RefreshTokenFactory = RefreshTokenFactory()

    @Bean
    fun loginUseCase(
        oidcTokenVerifier: OidcTokenVerifier,
        userRepository: UserRepository,
        refreshTokenRepository: RefreshTokenRepository,
        accessTokenIssuer: AccessTokenIssuer,
        refreshTokenFactory: RefreshTokenFactory,
        clock: Clock,
        jwtProperties: JwtProperties
    ): LoginUseCase = LoginService(
        oidcTokenVerifier = oidcTokenVerifier,
        userRepository = userRepository,
        refreshTokenRepository = refreshTokenRepository,
        accessTokenIssuer = accessTokenIssuer,
        refreshTokenFactory = refreshTokenFactory,
        clock = clock,
        refreshTokenTtl = jwtProperties.refreshTokenTtl
    )

    @Bean
    fun refreshTokenUseCase(
        refreshTokenRepository: RefreshTokenRepository,
        userRepository: UserRepository,
        accessTokenIssuer: AccessTokenIssuer,
        refreshTokenFactory: RefreshTokenFactory,
        clock: Clock,
        jwtProperties: JwtProperties
    ): RefreshTokenUseCase = RefreshTokenService(
        refreshTokenRepository = refreshTokenRepository,
        userRepository = userRepository,
        accessTokenIssuer = accessTokenIssuer,
        refreshTokenFactory = refreshTokenFactory,
        clock = clock,
        refreshTokenTtl = jwtProperties.refreshTokenTtl
    )

    @Bean
    fun logoutUseCase(
        refreshTokenRepository: RefreshTokenRepository,
        refreshTokenFactory: RefreshTokenFactory
    ): LogoutUseCase = LogoutService(refreshTokenRepository, refreshTokenFactory)

    @Bean
    fun getProfileUseCase(userRepository: UserRepository): GetProfileUseCase = GetProfileService(userRepository)

    @Bean
    fun updateProfileUseCase(userRepository: UserRepository): UpdateProfileUseCase =
        UpdateProfileService(userRepository)

    @Bean
    fun deleteAccountUseCase(
        userRepository: UserRepository,
        refreshTokenRepository: RefreshTokenRepository,
        socialAccountRevoker: SocialAccountRevoker
    ): DeleteAccountUseCase = DeleteAccountService(userRepository, refreshTokenRepository, socialAccountRevoker)
}
