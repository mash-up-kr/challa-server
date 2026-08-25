package com.challa.core.room.domain

import java.time.LocalDateTime

private const val EXPIRATION_DAYS = 30L

typealias RoomId = Long

data class Room(
    val id: RoomId?,
    val title: String,
    val totalPhotoCount: Long,
    val remainedPhotoCount: Long,
    val invitationCode: String,
    val roomStatus: RoomStatus,
    val coverImageUrl: String?,
    val coverStickerId: Long?,
    val coverStickerColorId: Long?,
    val photoPrintCompletedAt: LocalDateTime?,
    val createdAt: LocalDateTime,
    val expiresAt: LocalDateTime,
    val deletedAt: LocalDateTime? = null
) {
    companion object {
        fun create(
            title: String,
            totalPhotoCount: Long,
            invitationCode: String,
            coverImageUrl: String?,
            coverStickerId: Long?,
            coverStickerColorId: Long?
        ) = Room(
            id = null,
            title = title,
            totalPhotoCount = totalPhotoCount,
            remainedPhotoCount = totalPhotoCount,
            invitationCode = invitationCode,
            roomStatus = RoomStatus.SHOOTING,
            coverImageUrl = coverImageUrl,
            coverStickerId = coverStickerId,
            coverStickerColorId = coverStickerColorId,
            photoPrintCompletedAt = null, // remainedPhotoCount 가 0 이 되는 시점 + N 시간으로 설정될 예정
            createdAt = LocalDateTime.now(),
            expiresAt = LocalDateTime.now().plusDays(EXPIRATION_DAYS)
        )
    }
}
