package com.challa.persistence.chat

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class ChatReadAdapter(private val chatRepository: ChatRepository)
