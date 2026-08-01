package com.challa.core.chat

import com.challa.core.chat.domain.Chat
import com.challa.core.chat.domain.ChatType

data class CreateChatCommand(
    val userId: Long,
    val roomId: Long,
    val photoId: Long? = null,
    val type: ChatType,
    val content: String
) {
    fun createWithPhoto() = Chat(
        type = type,
        content = content,
        userId = userId,
        roomId = roomId,
        photoId = photoId
    )

    fun createWithoutPhoto() = Chat(
        type = type,
        content = content,
        userId = userId,
        roomId = roomId,
        photoId = null
    )
}
