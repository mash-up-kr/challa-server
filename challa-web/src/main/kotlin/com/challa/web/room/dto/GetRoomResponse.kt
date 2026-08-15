package com.challa.web.room.dto

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
    val coverImageUrl: String?,
    val coverStickerUrl: String,
    val photoPrintCompletedAt: LocalDateTime?,
    val createdAt: LocalDateTime,
    val expiresAt: LocalDateTime
) {
    companion object {
        fun fromResult(result: GetRoomResult) = GetRoomResponse(
            id = result.room.id!!,
            title = result.room.title,
            totalPhotoCount = result.room.totalPhotoCount,
            remainedPhotoCount = result.room.remainedPhotoCount,
            invitationCode = result.room.invitationCode,
            status = result.room.roomStatus,
            coverImageUrl = result.room.coverImageUrl,
            coverStickerUrl = result.room.coverStickerUrl,
            photoPrintCompletedAt = result.room.photoPrintCompletedAt,
            createdAt = result.room.createdAt,
            expiresAt = result.room.expiresAt
        )
    }
}
