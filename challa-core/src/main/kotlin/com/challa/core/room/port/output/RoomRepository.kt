package com.challa.core.room.port.output

import com.challa.core.room.domain.Room
import com.challa.core.room.domain.RoomId
import com.challa.core.room.domain.RoomStatus
import java.time.LocalDateTime

interface RoomRepository {
    fun save(room: Room): Room

    fun saveAll(rooms: List<Room>): List<Room>

    fun findByInvitationCode(invitationCode: String): Room?

    fun findAllById(roomIds: List<RoomId>): List<Room>

    fun updateRoomsStatus(roomIds: List<RoomId>, roomStatus: RoomStatus)

    fun markPhotoPrintPending(roomId: RoomId, photoPrintCompletedAt: LocalDateTime)

    fun findByRoomId(roomId: RoomId): Room?

    fun decrementRemainedPhotoCount(roomId: RoomId): Boolean
}

class InvitationCodeConflictException : RuntimeException()
