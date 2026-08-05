package com.challa.web.room.dto

import com.challa.core.room.domain.RoomId
import com.challa.core.room.port.input.JoinRoomResult

data class JoinRoomResponse(val id: RoomId) {
    companion object {
        fun fromResult(result: JoinRoomResult) = JoinRoomResponse(
            id = result.id
        )
    }
}
