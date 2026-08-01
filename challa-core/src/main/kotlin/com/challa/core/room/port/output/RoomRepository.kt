package com.challa.core.room.port.output

import com.challa.core.room.domain.Room
import com.challa.core.room.domain.RoomId
import com.challa.core.room.domain.RoomStatus

interface RoomRepository {
    fun save(room: Room): Room

    fun findByInvitationCode(invitationCode: String): Room?

    fun findAllByRoomIdIn(roomIds: List<RoomId>): List<Room>

    fun updateRoomsStatus(roomIds: List<RoomId>, roomStatus: RoomStatus)

    fun findByRoomId(roomId: RoomId): Room?
}

class InvitationCodeConflictException : RuntimeException()
