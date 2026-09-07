package com.challa.core.room.application.event

interface RoomEventPublisher {
    fun publishMemberJoinedToRoom(event: MemberJoinedEvent)
    fun publishMemberJoinedToUser(targetUserId: Long, event: MemberJoinedEvent)
}
