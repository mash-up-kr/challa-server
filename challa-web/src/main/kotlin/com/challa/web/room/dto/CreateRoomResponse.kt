package com.challa.web.room.dto

import com.challa.core.room.port.input.CreateRoomResult

data class CreateRoomResponse(val invitationCode: String) {
    companion object {
        fun fromResult(result: CreateRoomResult) = CreateRoomResponse(
            invitationCode = result.invitationCode
        )
    }
}
