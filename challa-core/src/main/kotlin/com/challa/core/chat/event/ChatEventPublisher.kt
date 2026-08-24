package com.challa.core.chat.event

interface ChatEventPublisher {
    fun publishChatCreated(event: ChatCreatedEvent)
}
