package com.challa.core.room.port.output

import com.challa.core.room.domain.RoomId

data class RoomMemberCount(val roomId: RoomId, val memberCount: Long)
