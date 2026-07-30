package com.challa.core.usecase

import com.challa.core.domain.NicknamePolicy
import com.challa.core.domain.ProfileImageUrlPolicy
import com.challa.core.domain.User
import com.challa.core.exception.UserNotFoundException
import com.challa.core.port.inbound.UpdateProfileCommand
import com.challa.core.port.inbound.UpdateProfileUseCase
import com.challa.core.port.outbound.UserRepository

class UpdateProfileService(private val userRepository: UserRepository) : UpdateProfileUseCase {
    override fun update(userId: Long, command: UpdateProfileCommand): User {
        val nickname = NicknamePolicy.normalize(command.nickname)
        val profileImageUrl = ProfileImageUrlPolicy.normalizeOrNull(command.profileImageUrl)

        val user = userRepository.findById(userId) ?: throw UserNotFoundException()

        val updated = user.copy(nickname = nickname, profileImageUrl = profileImageUrl)
        return if (updated == user) user else userRepository.save(updated)
    }
}
