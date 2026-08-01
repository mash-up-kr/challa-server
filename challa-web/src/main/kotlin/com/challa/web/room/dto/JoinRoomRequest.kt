package com.challa.web.room.dto

import com.challa.core.room.port.input.JoinRoomCommand

data class JoinRoomRequest(val invitationCode: String) {
    fun toCommand(userId: Long) = JoinRoomCommand(
        userId = userId,
        invitationCode = invitationCode
    )
}
