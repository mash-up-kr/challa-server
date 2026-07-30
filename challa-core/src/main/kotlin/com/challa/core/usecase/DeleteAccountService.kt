package com.challa.core.usecase

import com.challa.core.exception.UserNotFoundException
import com.challa.core.port.inbound.DeleteAccountUseCase
import com.challa.core.port.outbound.RefreshTokenRepository
import com.challa.core.port.outbound.SocialAccountRevoker
import com.challa.core.port.outbound.UserRepository

class DeleteAccountService(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val socialAccountRevoker: SocialAccountRevoker
) : DeleteAccountUseCase {
    override fun delete(userId: Long) {
        val user = userRepository.findById(userId) ?: throw UserNotFoundException()

        socialAccountRevoker.revoke(user)

        refreshTokenRepository.deleteByUserId(userId)
        userRepository.deleteById(userId)
    }
}
