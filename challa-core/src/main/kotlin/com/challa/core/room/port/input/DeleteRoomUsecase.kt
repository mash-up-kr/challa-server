package com.challa.core.room.port.input

interface DeleteRoomUsecase {
    fun deleteRoom(command: DeleteRoomCommand)
}
