package com.challa.web.event

import com.challa.web.common.response.WebSocketResponse
import org.springframework.context.annotation.Lazy
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component

private const val ERROR_DESTINATION = "/queue/errors"

@Component
class WebSocketMessageSender(@param:Lazy private val simpMessagingTemplate: SimpMessagingTemplate) {
    fun <T> sendMessage(destination: String, payload: WebSocketResponse<T>) {
        simpMessagingTemplate.convertAndSend(
            destination,
            payload
        )
    }

    fun sendErrorToUser(userId: String, message: String) {
        val payload = WebSocketResponse.error(message)

        simpMessagingTemplate.convertAndSendToUser(userId, ERROR_DESTINATION, payload)
    }
}
