package com.challa.core.chat.business

import com.challa.core.chat.ChatRepository
import com.challa.core.chat.ChatResult
import com.challa.core.chat.ChatUseCase
import com.challa.core.chat.CreateChatCommand
import com.challa.core.chat.GetChatsResult
import com.challa.core.chat.domain.Chat
import com.challa.core.chat.domain.ChatType
import com.challa.core.photo.PhotoRepository
import com.challa.core.room.application.NoMatchingRoomException
import com.challa.core.room.port.output.RoomRepository
import com.challa.core.user.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class ChatService(
    private val chatRepository: ChatRepository,
    private val roomRepository: RoomRepository,
    private val userRepository: UserRepository,
    private val photoRepository: PhotoRepository
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

    override fun chat(input: CreateChatCommand): ChatResult {
        require(input.type == ChatType.DEFAULT) {
            "It's for DEFAULT chat request"
        }

        val savedChat = chatRepository.save(input.createWithoutPhoto())
        return ChatResult.from(savedChat)
    }

    override fun reactForPhoto(input: CreateChatCommand): ChatResult {
        require(input.photoId != null) {
            "photoId is required"
        }

        require(input.type in REACTION_TYPES) {
            "Unsupported reaction type: ${input.type}"
        }

        val savedChat = chatRepository.save(input.createWithPhoto())
        return ChatResult.from(savedChat)
    }

    private companion object {
        val REACTION_TYPES = setOf(
            ChatType.EMOJI,
            ChatType.COMMENT
        )
    }
}
