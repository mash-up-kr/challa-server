package com.challa.core.room.port.output

import com.challa.core.room.domain.Room

interface RoomRepository {
    fun save(room: Room): Room
}

class InviteCodeConflictException : RuntimeException()
