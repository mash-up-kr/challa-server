package com.challa.core.room.port.input

import com.challa.core.room.domain.RoomCover
import com.challa.core.room.domain.RoomStatus
import java.time.LocalDateTime

data class GetRoomResult(
    val roomId: Long,
    val title: String,
    val totalPhotoCount: Long,
    val remainedPhotoCount: Long,
    val invitationCode: String,
    val roomStatus: RoomStatus,
    val cover: RoomCover,
    val photoPrintCompletedAt: LocalDateTime?,
    val createdAt: LocalDateTime,
    val expiresAt: LocalDateTime
)
