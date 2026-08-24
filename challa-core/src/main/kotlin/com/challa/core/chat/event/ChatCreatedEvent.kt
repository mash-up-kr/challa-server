package com.challa.core.chat.event

import com.challa.core.chat.ChatResult

data class ChatCreatedEvent(val roomId: Long, val chat: ChatResult)
