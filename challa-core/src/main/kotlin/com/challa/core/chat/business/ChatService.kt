package com.challa.core.chat.business

import com.challa.core.chat.*
import com.challa.core.chat.domain.Chat
import com.challa.core.chat.domain.ChatType
import com.challa.core.chat.event.ChatCreatedEvent
import com.challa.core.photo.PhotoRepository
import com.challa.core.room.exception.NoMatchingRoomException
import com.challa.core.room.port.output.RoomRepository
import com.challa.core.user.UserRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChatService(
    private val chatRepository: ChatRepository,
    private val roomRepository: RoomRepository,
    private val userRepository: UserRepository,
    private val photoRepository: PhotoRepository,
    private val applicationEventPublisher: ApplicationEventPublisher
) : ChatUseCase {

    override fun getChatsByRoomId(roomId: Long, page: Int, size: Int): GetChatsResult {
        val pageable = PageRequest.of(
            page,
            size,
            Sort.by(Sort.Direction.DESC, "createdAt")
        )

        val room = roomRepository.findByRoomId(roomId) ?: throw NoMatchingRoomException()

        val chats = chatRepository.getChatsByRoomId(roomId, pageable)
            .sortedByDescending(Chat::createdAt)

        val users = userRepository.findAllByIds(
            chats.map(Chat::userId).distinct()
        ).mapNotNull { user ->
            user.id?.let { it to user }
        }.toMap()

        val photos = photoRepository.findAllByIds(
            chats.mapNotNull { it.photoId }.distinct()
        ).mapNotNull { photo ->
            photo.id?.let { it to photo }
        }.toMap()

        return GetChatsResult.from(
            room = room,
            chats = chats,
            photos = photos,
            users = users
        )
    }

    @Transactional
    override fun chat(input: CreateChatCommand): ChatResult {
        require(input.type == ChatType.DEFAULT) {
            "It's for DEFAULT chat request"
        }

        val savedChat = chatRepository.save(input.createWithoutPhoto())
        val user = userRepository.findById(input.userId)
        val result = checkNotNull(ChatResult.from(savedChat, photo = null, user = user))

        publishChatCreated(input.roomId, result)

        return result
    }

    @Transactional
    override fun reactForPhoto(input: CreateChatCommand): ChatResult {
        require(input.photoId != null) {
            "photoId is required"
        }

        require(input.type in REACTION_TYPES) {
            "Unsupported reaction type: ${input.type}"
        }

        val savedChat = chatRepository.save(input.createWithPhoto())
        val user = userRepository.findById(input.userId)
        val photo = photoRepository.findById(input.photoId)
        val result = checkNotNull(ChatResult.from(savedChat, photo = photo, user = user))

        publishChatCreated(input.roomId, result)

        return result
    }

    override fun removeChat(userId: Long, chatId: Long): Long {
        val chat = chatRepository.findById(chatId)

        require(chat != null) { "Chat with id $chatId does not exist" }
        require(chat.userId == userId) { "User with id $userId does not exist in chat $chatId" }

        chatRepository.delete(chatId)
        return chatId
    }

    private fun publishChatCreated(roomId: Long, result: ChatResult) {
        applicationEventPublisher.publishEvent(
            ChatCreatedEvent(
                roomId = roomId,
                chat = result
            )
        )
    }

    private companion object {
        val REACTION_TYPES = setOf(
            ChatType.EMOJI,
            ChatType.COMMENT
        )
    }
}
