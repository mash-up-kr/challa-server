package com.challa.core.port.inbound

import com.challa.core.domain.User

interface GetProfileUseCase {
    fun getProfile(userId: Long): User
}

interface UpdateProfileUseCase {
    fun update(userId: Long, command: UpdateProfileCommand): User
}

data class UpdateProfileCommand(val nickname: String, val profileImageUrl: String?)

interface SuggestNicknameUseCase {
    fun suggest(): String
}

interface DeleteAccountUseCase {
    fun delete(userId: Long)
}
