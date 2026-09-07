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
import java.util.concurrent.ConcurrentHashMap

private const val AUTHORIZED_ROOM_IDS_ATTRIBUTE = "authorizedRoomIds"
private val ROOM_SUBSCRIBE_DESTINATION = Regex("""^/topic/room/(\d+)/(?:member-joined|chat)$""")
private val ROOM_SEND_DESTINATION = Regex("""^/app/room/(\d+)/chat(?:/reaction)?$""")

private class AuthorizedRooms(val roomIds: MutableSet<Long> = ConcurrentHashMap.newKeySet())

@Component
class WebSocketChannelInterceptor(
    private val validateRoomUserUsecase: ValidateRoomUserUsecase,
    private val webSocketMessageSender: WebSocketMessageSender
) : ChannelInterceptor {
    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*>? {
        val accessor = StompHeaderAccessor.wrap(message)

        when (accessor.command) {
            StompCommand.SUBSCRIBE -> return authorizeSubscribe(message, accessor)

            StompCommand.SEND -> return authorizeSend(message, accessor)

            else -> {}
        }

        return message
    }

    private fun authorizeSubscribe(message: Message<*>, accessor: StompHeaderAccessor): Message<*>? {
        val destination = accessor.destination ?: return null
        val userId = accessor.sessionAttributes?.get("userId") as Long
        val roomDestinationMatch = ROOM_SUBSCRIBE_DESTINATION.matchEntire(destination)

        return when {
            roomDestinationMatch != null -> {
                val roomId = roomDestinationMatch.groupValues[1].toLong()
                if (!authorizeRoom(accessor, userId, roomId)) {
                    sendErrorToUser(userId.toString(), "Invalid room user")

                    return null
                }

                return message
            }

            PermittedDestination.isPermitted(destination) -> message

            else -> {
                sendErrorToUser(userId.toString(), "Invalid destination")

                return null
            }
        }
    }

    private fun authorizeSend(message: Message<*>, accessor: StompHeaderAccessor): Message<*>? {
        val destination = accessor.destination ?: return null
        val userId = accessor.sessionAttributes?.get("userId") as Long
        val roomDestinationMatch = ROOM_SEND_DESTINATION.matchEntire(destination)

        if (roomDestinationMatch == null) {
            sendErrorToUser(userId.toString(), "Invalid destination")
            return null
        }

        val roomId = roomDestinationMatch.groupValues[1].toLong()
        if (!authorizeRoom(accessor, userId, roomId)) {
            sendErrorToUser(userId.toString(), "Invalid room user")
            return null
        }

        return message
    }

    private fun authorizeRoom(accessor: StompHeaderAccessor, userId: Long, roomId: Long): Boolean {
        val authorizedRoomIds = accessor.authorizedRoomIds()

        return synchronized(authorizedRoomIds) {
            if (roomId in authorizedRoomIds) {
                return@synchronized true
            }

            val command = ValidateRoomUserCommand(
                userId = userId,
                roomId = roomId
            )
            val isValid = validateRoomUserUsecase.validateRoomMember(command).isValid
            if (isValid) {
                authorizedRoomIds.add(roomId)
            }

            return@synchronized isValid
        }
    }

    private fun sendErrorToUser(userId: String, message: String) {
        webSocketMessageSender.sendErrorToUser(
            userId = userId,
            message = message
        )
    }
}

private fun StompHeaderAccessor.authorizedRoomIds(): MutableSet<Long> {
    val attributes = requireNotNull(sessionAttributes) { "WebSocket session attributes not found" }

    return synchronized(attributes) {
        val authorizedRooms = attributes[AUTHORIZED_ROOM_IDS_ATTRIBUTE] as? AuthorizedRooms
            ?: AuthorizedRooms().also { attributes[AUTHORIZED_ROOM_IDS_ATTRIBUTE] = it }

        authorizedRooms.roomIds
    }
}

object PermittedDestination {
    private val destinations = listOf(
        "/user/queue/error",
        "/user/queue/member-joined"
    )

    fun isPermitted(destination: String): Boolean = destination in destinations
}
