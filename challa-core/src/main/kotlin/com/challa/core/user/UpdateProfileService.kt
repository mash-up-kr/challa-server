package com.challa.core.user

class UpdateProfileService(private val userRepository: UserRepository) : UpdateProfileUseCase {
    override fun update(userId: Long, command: UpdateProfileCommand): User {
        val nickname = NicknamePolicy.normalize(command.nickname)
        val profileImageUrl = ProfileImageUrlPolicy.normalizeOrNull(command.profileImageUrl)

        val user = userRepository.findById(userId) ?: throw UserNotFoundException()

        val updated = user.copy(nickname = nickname, profileImageUrl = profileImageUrl)
        return if (updated == user) user else userRepository.save(updated)
    }
}
