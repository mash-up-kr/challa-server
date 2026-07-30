package com.challa.core.user

import com.challa.core.auth.RefreshTokenRepository
import com.challa.core.auth.SocialAccountRevoker

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
