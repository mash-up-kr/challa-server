package com.challa.web.room.dto

import com.challa.core.room.domain.RoomStatus
import com.challa.core.room.port.input.ListRoomsResult

data class ListRoomsResponse(
    val id: Long,
    val status: RoomStatus,
    val title: String,
    val memberCount: Long,
    val remainedPhotoCount: Long
) {
    companion object {
        fun fromResult(result: ListRoomsResult.RoomProjection) = ListRoomsResponse(
            id = result.roomId,
            status = result.roomStatus,
            title = result.title,
            memberCount = result.memberCount,
            remainedPhotoCount = result.remainedPhotoCount
        )
    }
}
