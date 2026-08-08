package com.challa.core.room.application.event

import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class MemberJoinedEventListener(private val roomEventPublisher: RoomEventPublisher) {
    @Async
    @TransactionalEventListener(
        phase = TransactionPhase.AFTER_COMMIT
    )
    fun handle(event: MemberJoinedEvent) {
        roomEventPublisher.publishMemberJoined(event)
    }
}
