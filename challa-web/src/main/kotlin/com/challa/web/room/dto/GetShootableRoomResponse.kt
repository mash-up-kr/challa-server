package com.challa.web.room.dto

import com.challa.core.room.port.input.GetShootableRoomsResult

data class GetShootableRoomResponse(
    val id: Long,
    val title: String,
    val remainedPhotoCount: Long,
    val totalPhotoCount: Long
) {
    companion object {
        fun fromResult(result: GetShootableRoomsResult.ShootableRoom) = GetShootableRoomResponse(
            id = result.id,
            title = result.title,
            remainedPhotoCount = result.remainedPhotoCount,
            totalPhotoCount = result.totalPhotoCount
        )
    }
}
