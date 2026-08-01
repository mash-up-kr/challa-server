package com.challa.web.room.dto

import com.challa.core.room.port.input.JoinRoomCommand

data class JoinRoomRequest(val inviteCode: String) {
    fun toCommand(userId: Long) = JoinRoomCommand(
        userId = userId,
        inviteCode = inviteCode
    )
}
