package com.challa.core.room.port.input

data class GetRoomUsersResult(val userProjections: List<UserProjection>) {
    data class UserProjection(val id: Long, val nickname: String, val profileImageUrl: String?)
}
