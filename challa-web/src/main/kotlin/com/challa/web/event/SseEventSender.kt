package com.challa.web.event

import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@Component
class SseEventSender(private val emitterRepository: SseEmitterRepository) {
    fun send(userId: Long, eventName: String, data: Any) {
        emitterRepository.findAllByUserId(userId).forEach { (connectionId, emitter) ->
            try {
                emitter.send(SseEmitter.event().name(eventName).data(data))
            } catch (exception: Exception) {
                emitterRepository.delete(userId = userId, connectionId = connectionId)
                emitter.completeWithError(exception)
            }
        }
    }

    fun sendAll(userIds: Collection<Long>, eventName: String, data: Any) {
        userIds.forEach { userId -> send(userId = userId, eventName = eventName, data = data) }
    }
}
