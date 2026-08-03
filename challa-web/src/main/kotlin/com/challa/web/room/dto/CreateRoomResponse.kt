package com.challa.web.room.dto

import com.challa.core.room.port.input.CreateRoomResult

data class CreateRoomResponse(val id: Long) {
    companion object {
        fun fromResult(result: CreateRoomResult) = CreateRoomResponse(
            id = result.id
        )
    }
}
