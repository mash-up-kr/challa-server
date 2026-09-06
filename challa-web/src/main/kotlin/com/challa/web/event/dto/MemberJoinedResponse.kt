package com.challa.web.event.dto

import com.challa.core.room.application.event.MemberJoinedEvent

data class MemberJoinedResponse(
    val id: Long,
    val title: String,
    val userId: Long,
    val userNickname: String,
    val userProfileImageUrl: String?
) {
    companion object {
        fun fromEvent(event: MemberJoinedEvent) = MemberJoinedResponse(
            id = event.roomId,
            title = event.roomTitle,
            userId = event.userId,
            userNickname = event.userNickname,
            userProfileImageUrl = event.userProfileImageUrl
        )
    }
}
