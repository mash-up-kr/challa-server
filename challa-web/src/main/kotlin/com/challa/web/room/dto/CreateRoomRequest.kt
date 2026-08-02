package com.challa.web.room.dto

import com.challa.core.room.port.input.CreateRoomCommand

data class CreateRoomRequest(val roomTitle: String, val totalPhotoCount: Long) {
    fun toCommand(userId: Long) = CreateRoomCommand(
        userId = userId,
        roomTitle = this.roomTitle,
        totalPhotoCount = this.totalPhotoCount
    )
}
