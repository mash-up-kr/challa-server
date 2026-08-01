package com.challa.web.room.dto

import com.challa.core.room.port.input.ListRoomsResult

data class ListRoomsResponse(val roomProjection: List<ListRoomsResult.RoomProjection>) {
    companion object {
        fun fromResult(listRoomsResult: ListRoomsResult) = ListRoomsResponse(
            roomProjection = listRoomsResult.roomProjections
        )
    }
}
