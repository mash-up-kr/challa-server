package com.challa.core.room.port.output

import com.challa.core.room.domain.RoomParticipant

interface RoomParticipantRepository {
    fun save(roomParticipant: RoomParticipant): RoomParticipant
}
