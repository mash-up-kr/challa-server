package com.challa.persistence.chat

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class ChatCommandAdapter(
    private val chatRepository: ChatRepository,
) {
}
