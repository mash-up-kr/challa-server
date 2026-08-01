package com.challa.web.chat

import com.challa.core.chat.ChatResult
import com.challa.core.chat.CreateChatCommand
import com.challa.core.chat.GetChatsResult
import com.challa.core.chat.RoomResultForChat
import com.challa.core.chat.domain.ChatType

data class GetChatsResponse(
    val room: RoomResultForChat,
    val chats: List<ChatResult>,
) {
    companion object {
        fun from(result: GetChatsResult) = GetChatsResponse(
            room = result.room,
            chats = result.chats
        )
    }
}

data class CreateChatRequest(
    val roomId: Long,
    val photoId: Long? = null,
    val type: ChatType? = ChatType.DEFAULT,
    val content: String,
) {
    fun toCommand(userId: Long) = CreateChatCommand(
        userId = userId,
        roomId = roomId,
        photoId = photoId,
        type = type ?: ChatType.DEFAULT,
        content = content
    )
}

data class CreateChatResponse(
    val chat: ChatResult
) {
    companion object {
        fun from(result: ChatResult) = CreateChatResponse(
            chat = result
        )
    }
}
