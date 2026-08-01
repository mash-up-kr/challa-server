package com.challa.core.room.port.output

import com.challa.core.room.domain.Room

interface RoomRepository {
    fun save(room: Room): Room

    fun findByInviteCode(inviteCode: String): Room?
}

class InviteCodeConflictException : RuntimeException()
