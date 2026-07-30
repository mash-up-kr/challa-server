package com.challa.bootstrap.config

import com.challa.core.port.inbound.DeleteAccountUseCase
import com.challa.core.port.inbound.GetProfileUseCase
import com.challa.core.port.inbound.LoginUseCase
import com.challa.core.port.inbound.LogoutUseCase
import com.challa.core.port.inbound.RefreshTokenUseCase
import com.challa.core.port.inbound.SuggestNicknameUseCase
import com.challa.core.port.inbound.UpdateProfileUseCase
import com.challa.core.port.outbound.AccessTokenIssuer
import com.challa.core.port.outbound.OidcTokenVerifier
import com.challa.core.port.outbound.RandomNicknameGenerator
import com.challa.core.port.outbound.RefreshTokenRepository
import com.challa.core.port.outbound.SocialAccountRevoker
import com.challa.core.port.outbound.UserRepository
import com.challa.core.token.RefreshTokenFactory
import com.challa.core.usecase.DeleteAccountService
import com.challa.core.usecase.GetProfileService
import com.challa.core.usecase.LoginService
import com.challa.core.usecase.LogoutService
import com.challa.core.usecase.RefreshTokenService
import com.challa.core.usecase.SuggestNicknameService
import com.challa.core.usecase.UpdateProfileService
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
    fun suggestNicknameUseCase(randomNicknameGenerator: RandomNicknameGenerator): SuggestNicknameUseCase =
        SuggestNicknameService(randomNicknameGenerator)

    @Bean
    fun deleteAccountUseCase(
        userRepository: UserRepository,
        refreshTokenRepository: RefreshTokenRepository,
        socialAccountRevoker: SocialAccountRevoker
    ): DeleteAccountUseCase = DeleteAccountService(userRepository, refreshTokenRepository, socialAccountRevoker)
}
