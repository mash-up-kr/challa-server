package com.challa.web.room.dto

import com.challa.core.room.domain.Room
import com.challa.core.room.port.input.GetRoomResult

data class GetRoomResponse(val room: Room) {
    companion object {
        fun fromResult(getRoomResult: GetRoomResult) = GetRoomResponse(room = getRoomResult.room)
    }
}
