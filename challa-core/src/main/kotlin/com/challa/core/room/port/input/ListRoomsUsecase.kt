package com.challa.core.room.port.input

interface ListRoomsUsecase {
    fun listRooms(listRoomsCommand: ListRoomsCommand): ListRoomsResult
}
