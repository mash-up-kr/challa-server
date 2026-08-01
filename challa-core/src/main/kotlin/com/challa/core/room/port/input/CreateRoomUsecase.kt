package com.challa.core.room.port.input

interface CreateRoomUsecase {
    fun createRoom(input: CreateRoomCommand): CreateRoomResult
}
