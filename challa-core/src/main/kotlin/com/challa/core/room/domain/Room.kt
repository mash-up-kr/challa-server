package com.challa.core.room.domain

import java.time.LocalDateTime

typealias RoomId = Long

data class Room(
    val id: RoomId?,
    val title: String,
    val totalPhotoCount: Long,
    val remainedPhotoCount: Long,
    val invitationCode: String,
    val roomStatus: RoomStatus,
    val photoPrintCompletionAt: LocalDateTime?,
    val createdAt: LocalDateTime
) {
    companion object {
        fun create(title: String, totalPhotoCount: Long, invitationCode: String) = Room(
            id = null,
            title = title,
            totalPhotoCount = totalPhotoCount,
            remainedPhotoCount = totalPhotoCount,
            invitationCode = invitationCode,
            roomStatus = RoomStatus.SHOOTING,
            photoPrintCompletionAt = null, // remainedPhotoCount 가 0 이 되는 시점 + N 시간으로 설정될 예정
            createdAt = LocalDateTime.now()
        )
    }
}
