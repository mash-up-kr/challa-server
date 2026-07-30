package com.challa.web.user

import com.challa.core.domain.User
import com.challa.core.port.inbound.UpdateProfileCommand
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

data class UserProfileResponse(val id: Long, val nickname: String?, val profileImageUrl: String?) {
    companion object {
        fun from(user: User): UserProfileResponse = UserProfileResponse(
            id = requireNotNull(user.id),
            nickname = user.nickname,
            profileImageUrl = user.profileImageUrl
        )
    }
}

data class UpdateProfileRequest(
    @param:JsonProperty(required = true)
    @field:Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "1-20 characters")
    val nickname: String,
    @param:JsonProperty(required = true)
    @field:Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    val profileImageUrl: String?
) {
    fun toCommand(): UpdateProfileCommand = UpdateProfileCommand(nickname = nickname, profileImageUrl = profileImageUrl)
}

data class RandomNicknameResponse(val nickname: String)
