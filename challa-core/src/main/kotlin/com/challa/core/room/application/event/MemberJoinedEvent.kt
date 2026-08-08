package com.challa.core.room.application.event

import com.challa.core.room.domain.RoomId

data class MemberJoinedEvent(val roomId: RoomId, val roomTitle: String, val userNickname: String)
