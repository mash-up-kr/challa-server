package com.challa.web.room.dto

import com.challa.core.room.domain.RoomStatus
import com.challa.core.room.port.input.ListRoomsResult
import java.time.LocalDateTime

data class ListRoomResponse(
    val id: Long,
    val status: RoomStatus,
    val title: String,
    val memberCount: Long,
    val totalPhotoCount: Long,
    val remainedPhotoCount: Long,
    val thumbnailImageUrls: List<String?>,
    val photoPrintCompletedAt: LocalDateTime?
) {
    companion object {
        fun fromResult(result: ListRoomsResult.RoomProjection) = ListRoomResponse(
            id = result.roomId,
            status = result.roomStatus,
            title = result.title,
            memberCount = result.memberCount,
            totalPhotoCount = result.totalPhotoCount,
            remainedPhotoCount = result.remainedPhotoCount,
            thumbnailImageUrls = result.thumbnailImageUrls,
            photoPrintCompletedAt = result.photoPrintCompletionAt
        )
    }
}
