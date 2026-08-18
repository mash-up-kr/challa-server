package com.challa.web.room.dto

import com.challa.core.room.domain.RoomCover
import com.challa.core.room.domain.RoomStatus
import com.challa.core.room.port.input.GetRoomResult
import java.time.LocalDateTime

data class GetRoomResponse(
    val id: Long,
    val title: String,
    val totalPhotoCount: Long,
    val remainedPhotoCount: Long,
    val invitationCode: String,
    val status: RoomStatus,
    val cover: RoomCover,
    val photoPrintCompletedAt: LocalDateTime?,
    val createdAt: LocalDateTime,
    val expiresAt: LocalDateTime
) {
    companion object {
        fun fromResult(result: GetRoomResult) = GetRoomResponse(
            id = result.roomId,
            title = result.title,
            totalPhotoCount = result.totalPhotoCount,
            remainedPhotoCount = result.remainedPhotoCount,
            invitationCode = result.invitationCode,
            status = result.roomStatus,
            cover = result.cover,
            photoPrintCompletedAt = result.photoPrintCompletedAt,
            createdAt = result.createdAt,
            expiresAt = result.expiresAt
        )
    }
}
