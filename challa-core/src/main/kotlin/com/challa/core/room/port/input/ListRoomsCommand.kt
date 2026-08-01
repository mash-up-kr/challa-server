package com.challa.core.room.port.input

import com.challa.core.room.domain.RoomStatus

data class ListRoomsCommand(val userId: Long, val status: List<RoomStatus>)
