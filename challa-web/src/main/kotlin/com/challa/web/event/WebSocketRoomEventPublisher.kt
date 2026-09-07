package com.challa.web.event

import com.challa.core.room.application.event.MemberJoinedEvent
import com.challa.core.room.application.event.RoomEventPublisher
import com.challa.web.common.response.ApiResponse
import com.challa.web.event.dto.MemberJoinedResponse
import com.challa.web.event.dto.WebSocketRoomEnvelope
import org.springframework.stereotype.Component

@Component
class WebSocketRoomEventPublisher(private val webSocketMessageSender: WebSocketMessageSender) : RoomEventPublisher {
    override fun publishMemberJoinedToRoom(event: MemberJoinedEvent) {
        webSocketMessageSender.sendMessage(
            destination = "/topic/room/${event.roomId}/member-joined",
            payload = ApiResponse.ok(
                data = WebSocketRoomEnvelope(MemberJoinedResponse.fromEvent(event))
            )
        )
    }

    override fun publishMemberJoinedToUser(targetUserId: Long, event: MemberJoinedEvent) {
        webSocketMessageSender.sendMemberJoinedToUser(
            userId = targetUserId.toString(),
            payload = ApiResponse.ok(
                data = WebSocketRoomEnvelope(MemberJoinedResponse.fromEvent(event))
            )
        )
    }
}
