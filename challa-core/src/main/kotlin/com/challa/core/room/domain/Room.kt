package com.challa.core.room.domain

import java.time.LocalDateTime

typealias RoomId = Long

data class Room(
    val id: RoomId?,
    val title: String,
    val filmLimit: Long,
    val remainingFilmCount: Long,
    val invitationCode: String,
    val roomStatus: RoomStatus,
    val printCompletionAt: LocalDateTime?,
    val createdAt: LocalDateTime
) {
    companion object {
        fun create(title: String, filmLimit: Long, invitationCode: String) = Room(
            id = null,
            title = title,
            filmLimit = filmLimit,
            remainingFilmCount = filmLimit,
            invitationCode = invitationCode,
            roomStatus = RoomStatus.SHOOTING,
            printCompletionAt = null, // remainingFilmCount 가 0 이 되는 시점 + N 시간으로 설정될 예정
            createdAt = LocalDateTime.now()
        )
    }
}
