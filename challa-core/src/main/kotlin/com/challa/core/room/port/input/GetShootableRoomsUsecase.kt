package com.challa.core.room.port.input

interface GetShootableRoomsUsecase {
    fun getShootableRooms(getShootableRoomsCommand: GetShootableRoomsCommand): GetShootableRoomsResult
}
