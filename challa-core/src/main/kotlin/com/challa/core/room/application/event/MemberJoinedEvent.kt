package com.challa.core.room.application.event

import com.challa.core.room.domain.RoomId

data class MemberJoinedEvent(
    val roomId: RoomId,
    val roomTitle: String,
    val userId: Long,
    val userNickname: String,
    val userProfileImageUrl: String?,
    val targetUserIds: List<Long>
)
