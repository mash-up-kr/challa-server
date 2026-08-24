package com.challa.core.chat.event

import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class ChatCreatedEventListener(private val chatEventPublisher: ChatEventPublisher) {
    @Async
    @TransactionalEventListener(
        phase = TransactionPhase.AFTER_COMMIT
    )
    fun handle(event: ChatCreatedEvent) {
        chatEventPublisher.publishChatCreated(event)
    }
}
