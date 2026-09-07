package com.challa.web.event

import com.challa.web.common.response.ApiResponse
import org.springframework.context.annotation.Lazy
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component

private const val MEMBER_JOINED_DESTINATION = "/queue/member-joined"
private const val ERROR_DESTINATION = "/queue/error"

@Component
class WebSocketMessageSender(@param:Lazy private val simpMessagingTemplate: SimpMessagingTemplate) {
    fun <T> sendMessage(destination: String, payload: ApiResponse<T>) {
        simpMessagingTemplate.convertAndSend(
            destination,
            payload
        )
    }

    fun sendErrorToUser(userId: String, message: String) {
        val payload = ApiResponse.error(message)

        simpMessagingTemplate.convertAndSendToUser(userId, ERROR_DESTINATION, payload)
    }

    fun <T> sendMemberJoinedToUser(userId: String, payload: ApiResponse<T>) {
        simpMessagingTemplate.convertAndSendToUser(userId, MEMBER_JOINED_DESTINATION, payload)
    }
}
