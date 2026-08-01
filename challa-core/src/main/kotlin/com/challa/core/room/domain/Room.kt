package com.challa.core.room.domain

import java.time.LocalDateTime

typealias RoomId = Long

data class Room(
    val roomId: RoomId?,
    val title: String,
    val filmLimit: Long,
    val remainingFilmCount: Long,
    val inviteCode: String,
    val roomStatus: RoomStatus,
    val printCompletionAt: LocalDateTime?,
    val isActive: Boolean,
    val createdAt: LocalDateTime
) {
    companion object {
        fun create(title: String, filmLimit: Long, inviteCode: String) = Room(
            roomId = null,
            title = title,
            filmLimit = filmLimit,
            remainingFilmCount = filmLimit,
            inviteCode = inviteCode,
            roomStatus = RoomStatus.SHOOTING,
            printCompletionAt = null, // remainingFilmCount 가 0 이 되는 시점 + N 시간으로 설정될 예정
            isActive = true,
            createdAt = LocalDateTime.now()
        )
    }
}
