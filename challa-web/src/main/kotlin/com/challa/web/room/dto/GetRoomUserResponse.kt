package com.challa.web.room.dto

import com.challa.core.room.port.input.GetRoomUsersResult

data class GetRoomUserResponse(val id: Long, val nickname: String, val profileImageUrl: String?) {
    companion object {
        fun fromResult(result: GetRoomUsersResult.UserProjection) = GetRoomUserResponse(
            id = result.id,
            nickname = result.nickname,
            profileImageUrl = result.profileImageUrl
        )
    }
}
