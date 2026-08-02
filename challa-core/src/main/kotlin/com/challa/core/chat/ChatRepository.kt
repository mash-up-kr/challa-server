package com.challa.core.chat

import com.challa.core.chat.domain.Chat
import org.springframework.data.domain.Pageable

interface ChatRepository {
    fun getChatsByRoomId(roomId: Long, pageable: Pageable): List<Chat>
    fun findAllByPhotoId(photoId: Long): List<Chat>
    fun save(chat: Chat): Chat
}
