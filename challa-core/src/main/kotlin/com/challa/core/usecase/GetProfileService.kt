package com.challa.core.usecase

import com.challa.core.domain.User
import com.challa.core.exception.UserNotFoundException
import com.challa.core.port.inbound.GetProfileUseCase
import com.challa.core.port.outbound.UserRepository

class GetProfileService(private val userRepository: UserRepository) : GetProfileUseCase {

    override fun getProfile(userId: Long): User = userRepository.findById(userId) ?: throw UserNotFoundException()
}
