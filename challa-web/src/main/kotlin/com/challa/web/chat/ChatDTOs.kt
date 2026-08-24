package com.challa.web.chat

import com.challa.core.chat.ChatResult
import com.challa.core.chat.CreateChatCommand
import com.challa.core.chat.domain.ChatType
import java.time.LocalDateTime

data class GetChatsResponse(
    val type: ChatType,
    val content: String,
    val photoId: Long? = null,
    val photoImageUrl: String? = null,
    val createdAt: LocalDateTime,
    val userName: String? = null,
    val userProfileImageUrl: String? = null
) {
    companion object {
        fun from(result: ChatResult) = GetChatsResponse(
            type = result.type,
            content = result.content,
            photoId = result.photoId,
            photoImageUrl = result.photoImageUrl,
            createdAt = result.createdAt,
            userName = result.user?.name,
            userProfileImageUrl = result.user?.profileImageUrl
        )
    }
}

data class CreateChatRequest(
    val roomId: Long,
    val photoId: Long? = null,
    val type: ChatType? = ChatType.DEFAULT,
    val content: String
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
    val chatId: Long,
    val type: ChatType,
    val content: String,
    val photoId: Long? = null,
    val photoImageUrl: String? = null,
    val createdAt: LocalDateTime,
    val userName: String? = null,
    val userProfileImageUrl: String? = null
) {
    companion object {
        fun from(result: ChatResult) = CreateChatResponse(
            chatId = result.chatId,
            type = result.type,
            content = result.content,
            photoId = result.photoId,
            photoImageUrl = result.photoImageUrl,
            createdAt = result.createdAt,
            userName = result.user?.name,
            userProfileImageUrl = result.user?.profileImageUrl
        )
    }
}

data class DeleteChatResponse(val chatId: Long) {
    companion object {
        fun from(chatId: Long) = DeleteChatResponse(
            chatId = chatId
        )
    }
}
