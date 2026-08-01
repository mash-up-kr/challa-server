package com.challa.core.room.port.input

interface GetRoomUsecase {
    fun getRoom(getRoomCommand: GetRoomCommand): GetRoomResult
}
