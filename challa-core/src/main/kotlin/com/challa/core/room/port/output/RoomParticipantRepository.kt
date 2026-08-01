package com.challa.core.room.port.output

import com.challa.core.room.domain.RoomParticipant

interface RoomParticipantRepository {
    fun save(roomParticipant: RoomParticipant): RoomParticipant

    fun findAllByUserId(userId: Long): List<RoomParticipant>

    fun findByUserIdAndRoomId(userId: Long, roomId: Long): RoomParticipant?
}
