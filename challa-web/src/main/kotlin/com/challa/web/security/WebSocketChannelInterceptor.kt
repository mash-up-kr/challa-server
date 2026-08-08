package com.challa.web.security

import com.challa.web.event.WebSocketMessageSender
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.stereotype.Component

@Component
class WebSocketChannelInterceptor(private val webSocketMessageSender: WebSocketMessageSender) : ChannelInterceptor {
    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*>? {
        val accessor = StompHeaderAccessor.wrap(message)

        when (accessor.command) {
            StompCommand.SUBSCRIBE -> {
                val destination = accessor.destination!!
                val userId = accessor.sessionAttributes?.get("userId") as Long

                if (!PermittedDestination.isPermitted(destination)) {
                    sendErrorToUser(
                        userId = userId.toString(),
                        message = "Invalid destination"
                    )

                    return null
                }
            }

            else -> {}
        }

        return message
    }

    private fun sendErrorToUser(userId: String, message: String) {
        webSocketMessageSender.sendErrorToUser(
            userId = userId,
            message = message
        )
    }
}

object PermittedDestination {
    private val destinations = listOf(
        "/user/queue/errors"
    )

    fun isPermitted(destination: String): Boolean = destination in destinations
}
