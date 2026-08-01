package com.challa.web.room.dto

import com.challa.core.room.port.input.CreateRoomResult

data class CreateRoomResponse(val inviteCode: String) {
    companion object {
        fun fromResult(createRoomResult: CreateRoomResult) = CreateRoomResponse(
            inviteCode = createRoomResult.inviteCode
        )
    }
}
