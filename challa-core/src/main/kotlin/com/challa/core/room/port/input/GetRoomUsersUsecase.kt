package com.challa.core.room.port.input

interface GetRoomUsersUsecase {
    fun getRoomUsers(getRoomUsersCommand: GetRoomUsersCommand): GetRoomUsersResult
}
