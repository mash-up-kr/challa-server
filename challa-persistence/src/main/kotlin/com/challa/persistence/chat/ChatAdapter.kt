package com.challa.persistence.chat

import com.challa.core.chat.ChatRepository
import com.challa.core.chat.domain.Chat
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class ChatAdapter(private val repository: ChatJpaRepository) : ChatRepository {

    @Transactional
    override fun save(chat: Chat): Chat = repository.save(ChatEntity.from(chat)).toDomain()

    override fun getChatsByRoomId(roomId: Long, pageable: Pageable): List<Chat> =
        repository.findAllByRoomId(roomId, pageable)
            .filter { it.createdAt != null }
            .map { it.toDomain() }
}
