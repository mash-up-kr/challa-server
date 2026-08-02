package com.challa.core.room.port.input

import com.challa.core.room.domain.RoomStatus

data class ListRoomsResult(val roomProjections: List<RoomProjection>) {
    data class RoomProjection(
        val roomId: Long,
        val roomStatus: RoomStatus,
        val title: String,
        val memberCount: Long,
        val remainedPhotoCount: Long
    )
}
