package com.challa.web.room.dto

import com.challa.core.room.port.input.GetShootableRoomsResult

data class GetShootableRoomsResponse(val rooms: List<ShootableRoom>) {
    data class ShootableRoom(val id: Long, val title: String, val remainedPhotoCount: Long, val totalPhotoCount: Long)

    companion object {
        fun fromResult(getShootableRoomsResult: GetShootableRoomsResult): GetShootableRoomsResponse {
            val shootableRooms = getShootableRoomsResult.rooms.map { room ->
                ShootableRoom(
                    id = room.id,
                    title = room.title,
                    remainedPhotoCount = room.remainedPhotoCount,
                    totalPhotoCount = room.totalPhotoCount
                )
            }

            return GetShootableRoomsResponse(
                rooms = shootableRooms
            )
        }
    }
}
