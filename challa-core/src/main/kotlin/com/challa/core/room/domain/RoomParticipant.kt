package com.challa.core.room.domain

import java.time.LocalDateTime

typealias RoomParticipantId = Long

data class RoomParticipant(
    val roomParticipantId: RoomParticipantId?,
    val roomId: Long,
    val userId: Long,
    val role: UserRole,
    val createdAt: LocalDateTime
) {
    companion object {
        fun createOwner(roomId: Long, userId: Long) = RoomParticipant(
            roomParticipantId = null,
            roomId = roomId,
            userId = userId,
            role = UserRole.OWNER,
            createdAt = LocalDateTime.now()
        )

        fun createMember(roomId: Long, userId: Long) = RoomParticipant(
            roomParticipantId = null,
            roomId = roomId,
            userId = userId,
            role = UserRole.MEMBER,
            createdAt = LocalDateTime.now()
        )
    }
}
