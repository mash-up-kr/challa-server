package com.challa.web.room.dto

import com.challa.core.room.port.input.CreateRoomCommand

data class CreateRoomRequest(val title: String, val totalPhotoCount: Long) {
    fun toCommand(userId: Long) = CreateRoomCommand(
        userId = userId,
        roomTitle = title,
        totalPhotoCount = totalPhotoCount
    )
}
