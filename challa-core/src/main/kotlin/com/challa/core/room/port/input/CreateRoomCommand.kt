package com.challa.core.room.port.input

data class CreateRoomCommand(val userId: Long, val roomTitle: String, val totalPhotoCount: Long)
