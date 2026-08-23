package com.challa.core.room.port.input

import com.challa.core.room.domain.RoomCover
import com.challa.core.room.domain.RoomStatus
import java.time.LocalDateTime

data class ListRoomsResult(val roomProjections: List<RoomProjection>) {
    data class RoomProjection(
        val roomId: Long,
        val roomStatus: RoomStatus,
        val title: String,
        val memberCount: Long,
        val totalPhotoCount: Long,
        val remainedPhotoCount: Long,
        val thumbnailImageUrls: List<String?>,
        val cover: RoomCover,
        val photoPrintCompletedAt: LocalDateTime?,
        val createdAt: LocalDateTime,
        val expiresAt: LocalDateTime,
        val photoPrintCompletionCheckedAt: LocalDateTime? = null
    )
}
