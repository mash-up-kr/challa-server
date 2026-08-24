package com.challa.web.event

import com.challa.core.chat.event.ChatCreatedEvent
import com.challa.core.chat.event.ChatEventPublisher
import com.challa.web.chat.ChatEnvelope
import com.challa.web.chat.CreateChatResponse
import com.challa.web.common.response.ApiResponse
import org.springframework.stereotype.Component

@Component
class WebSocketChatEventPublisher(private val webSocketMessageSender: WebSocketMessageSender) : ChatEventPublisher {
    override fun publishChatCreated(event: ChatCreatedEvent) {
        webSocketMessageSender.sendMessage(
            destination = "/topic/room/${event.roomId}/chat",
            payload = ApiResponse.ok(
                data = ChatEnvelope(CreateChatResponse.from(event.chat))
            )
        )
    }
}
