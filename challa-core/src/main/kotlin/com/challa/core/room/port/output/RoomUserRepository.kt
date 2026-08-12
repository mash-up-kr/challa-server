package com.challa.core.room.port.output

import com.challa.core.room.domain.RoomId
import com.challa.core.room.domain.RoomUser

interface RoomUserRepository {
    fun save(roomUser: RoomUser): RoomUser

    fun insertIfAbsent(roomUser: RoomUser): Boolean

    fun findAllByUserId(userId: Long): List<RoomUser>

    fun countMembersByRoomIds(roomIds: List<RoomId>): List<RoomMemberCount>

    fun findByUserIdAndRoomId(userId: Long, roomId: Long): RoomUser?

    fun findAllByRoomId(roomId: Long): List<RoomUser>
}
