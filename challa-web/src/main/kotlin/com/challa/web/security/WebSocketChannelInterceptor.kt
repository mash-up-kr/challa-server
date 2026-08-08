package com.challa.web.security

import com.challa.core.room.port.input.ValidateRoomUserCommand
import com.challa.core.room.port.input.ValidateRoomUserUsecase
import com.challa.web.event.WebSocketMessageSender
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.stereotype.Component

private val ROOM_SUBSCRIBE_DESTINATION = Regex("""^/topic/room/(\d+)/member-joined$""")

@Component
class WebSocketChannelInterceptor(
    private val validateRoomUserUsecase: ValidateRoomUserUsecase,
    private val webSocketMessageSender: WebSocketMessageSender
) : ChannelInterceptor {
    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*>? {
        val accessor = StompHeaderAccessor.wrap(message)

        when (accessor.command) {
            StompCommand.SUBSCRIBE -> {
                val destination = accessor.destination!!
                val userId = accessor.sessionAttributes?.get("userId") as Long

                val roomDestinationMatch = ROOM_SUBSCRIBE_DESTINATION.matchEntire(destination)

                when {
                    roomDestinationMatch != null -> {
                        val roomId = roomDestinationMatch.groupValues[1].toLong()
                        val validateRoomUserCommand = ValidateRoomUserCommand(
                            userId = userId,
                            roomId = roomId
                        )
                        val isValid = validateRoomUserUsecase.validateRoomMember(validateRoomUserCommand).isValid
                        if (!isValid) {
                            sendErrorToUser(
                                userId = userId.toString(),
                                message = "Invalid room user"
                            )

                            return null
                        }
                    }

                    PermittedDestination.isPermitted(destination) -> {}

                    else -> {
                        sendErrorToUser(
                            userId = userId.toString(),
                            message = "Invalid destination"
                        )

                        return null
                    }
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
        "/user/queue/error"
    )

    fun isPermitted(destination: String): Boolean = destination in destinations
}
