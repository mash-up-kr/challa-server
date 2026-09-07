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
        // 구버전 클라이언트와의 하위 호환성을 위해 기존 방 토픽과 신규 사용자별 구독 포인트에 함께 발송
        roomEventPublisher.publishMemberJoinedToRoom(event)

        event.targetUserIds.forEach { targetUserId ->
            roomEventPublisher.publishMemberJoinedToUser(targetUserId, event)
        }
    }
}
