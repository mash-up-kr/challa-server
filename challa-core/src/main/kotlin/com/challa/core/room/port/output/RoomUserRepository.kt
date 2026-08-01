package com.challa.core.room.port.output

import com.challa.core.room.domain.RoomUser

interface RoomUserRepository {
    fun save(roomUser: RoomUser): RoomUser

    fun findAllByUserId(userId: Long): List<RoomUser>

    fun findByUserIdAndRoomId(userId: Long, roomId: Long): RoomUser?
}
