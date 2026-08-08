package com.challa.core.room.application.event

interface RoomEventPublisher {
    fun publishMemberJoined(event: MemberJoinedEvent)
}
