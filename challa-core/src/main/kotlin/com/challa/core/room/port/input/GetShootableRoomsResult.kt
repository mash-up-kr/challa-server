package com.challa.core.room.port.input

data class GetShootableRoomsResult(val rooms: List<ShootableRoom>) {
    data class ShootableRoom(val id: Long, val title: String, val remainedPhotoCount: Long, val totalPhotoCount: Long)
}
