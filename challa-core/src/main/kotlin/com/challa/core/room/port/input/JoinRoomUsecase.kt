package com.challa.core.room.port.input

interface JoinRoomUsecase {
    fun joinRoom(joinRoomCommand: JoinRoomCommand): JoinRoomResult
}
