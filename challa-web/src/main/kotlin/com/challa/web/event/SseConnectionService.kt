package com.challa.web.event

import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.UUID

const val DEFAULT_TIMEOUT = 60L * 60L * 1000L

@Component
class SseConnectionService(private val emitterRepository: SseEmitterRepository) {
    fun subscribe(userId: Long): SseEmitter {
        val connectionId = UUID.randomUUID().toString()
        val emitter = SseEmitter(DEFAULT_TIMEOUT)

        emitterRepository.save(
            userId = userId,
            connectionId = connectionId,
            emitter = emitter
        )
        emitter.onCompletion { emitterRepository.delete(userId, connectionId) }
        emitter.onTimeout {
            emitterRepository.delete(userId, connectionId)
            emitter.complete()
        }
        emitter.onError { emitterRepository.delete(userId, connectionId) }

        emitter.send(
            SseEmitter.event()
                .name("connected")
                .data(SseConnectedResponse(connectionId = connectionId))
        )

        return emitter
    }
}
