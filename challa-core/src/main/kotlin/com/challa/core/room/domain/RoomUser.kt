package com.challa.core.room.domain

import java.time.LocalDateTime

typealias RoomUserId = Long

data class RoomUser(
    val id: RoomUserId?,
    val roomId: Long,
    val userId: Long,
    val createdAt: LocalDateTime,
    val photoPrintCompletionCheckedAt: LocalDateTime? = null
) {
    companion object {
        fun createOwner(roomId: Long, userId: Long) = RoomUser(
            id = null,
            roomId = roomId,
            userId = userId,
            createdAt = LocalDateTime.now()
        )

        fun createMember(roomId: Long, userId: Long) = RoomUser(
            id = null,
            roomId = roomId,
            userId = userId,
            createdAt = LocalDateTime.now()
        )
    }
}
