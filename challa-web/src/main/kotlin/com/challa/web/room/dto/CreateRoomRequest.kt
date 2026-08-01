package com.challa.web.room.dto

import com.challa.core.room.port.input.CreateRoomCommand

data class CreateRoomRequest(val roomTitle: String, val filmLimit: Long) {
    fun toCommand(userId: Long) = CreateRoomCommand(
        userId = userId,
        roomTitle = this.roomTitle,
        filmLimit = this.filmLimit
    )
}
