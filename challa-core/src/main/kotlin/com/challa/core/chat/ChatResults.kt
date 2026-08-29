package com.challa.core.chat

import com.challa.core.chat.domain.Chat
import com.challa.core.chat.domain.ChatType
import com.challa.core.photo.Photo
import com.challa.core.room.domain.Room
import com.challa.core.user.User
import java.time.LocalDateTime

private const val DELETED_USER_ID = 0L

data class GetChatsResult(val room: RoomResultForChat, val chats: List<ChatResult>) {
    companion object {
        fun from(room: Room, chats: List<Chat>, photos: Map<Long, Photo>, users: Map<Long, User>): GetChatsResult =
            GetChatsResult(
                room = RoomResultForChat.from(room),
                chats = chats.mapNotNull { chat ->
                    val photo = chat.photoId?.let { photos[it] }
                    ChatResult.from(chat, photo, users[chat.userId])
                }
            )
    }
}

data class RoomResultForChat(val title: String) {
    companion object {
        fun from(room: Room) = RoomResultForChat(
            title = room.title
        )
    }
}

data class ChatResult(
    val chatId: Long,
    val type: ChatType,
    val content: String,
    val photoId: Long? = null,
    val photoImageUrl: String? = null,
    val createdAt: LocalDateTime,
    val user: UserResultForChat? = null
) {
    companion object {
        fun from(chat: Chat, photo: Photo?, user: User?): ChatResult? {
            val createdAt = chat.createdAt ?: return null

            return ChatResult(
                chatId = chat.id ?: 0L,
                type = chat.type,
                content = chat.content,
                photoId = chat.photoId,
                photoImageUrl = photo?.imageUrl,
                createdAt = createdAt,
                user = UserResultForChat.from(user)
            )
        }

        fun from(chat: Chat) = ChatResult(
            chatId = chat.id ?: 0L,
            type = chat.type,
            content = chat.content,
            photoId = chat.photoId,
            createdAt = chat.createdAt ?: LocalDateTime.now()
        )
    }
}

data class UserResultForChat(val id: Long, val name: String, val profileImageUrl: String? = null) {
    companion object {
        fun from(user: User?): UserResultForChat = UserResultForChat(
            id = user?.id ?: DELETED_USER_ID,
            name = User.displayNicknameOf(user),
            profileImageUrl = user?.profileImageUrl
        )
    }
}
