package com.challa.web.chat

import com.challa.core.chat.ChatUseCase
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import java.security.Principal

@Controller
class WebSocketChatController(private val chatUseCase: ChatUseCase) {
    @MessageMapping("/room/{roomId}/chat")
    fun createChat(@DestinationVariable roomId: Long, principal: Principal, request: ChatEnvelope<CreateChatRequest>) {
        val command = request.chat.toCommand(principal.name.toLong())
        require(command.roomId == roomId) { "Room ID does not match destination" }

        chatUseCase.chat(command)
    }

    @MessageMapping("/room/{roomId}/chat/reaction")
    fun createChatForReaction(
        @DestinationVariable roomId: Long,
        principal: Principal,
        request: ChatEnvelope<CreateChatRequest>
    ) {
        val command = request.chat.toCommand(principal.name.toLong())
        require(command.roomId == roomId) { "Room ID does not match destination" }

        chatUseCase.reactForPhoto(command)
    }
}
