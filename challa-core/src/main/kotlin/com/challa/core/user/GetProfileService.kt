package com.challa.core.user

class GetProfileService(private val userRepository: UserRepository) : GetProfileUseCase {

    override fun getProfile(userId: Long): User = userRepository.findById(userId) ?: throw UserNotFoundException()
}
