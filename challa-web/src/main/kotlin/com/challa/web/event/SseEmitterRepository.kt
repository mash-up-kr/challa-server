package com.challa.web.event

import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.ConcurrentHashMap

@Component
class SseEmitterRepository {
    private val emitters = ConcurrentHashMap<Long, ConcurrentHashMap<String, SseEmitter>>()

    fun save(userId: Long, connectionId: String, emitter: SseEmitter) {
        emitters.computeIfAbsent(userId) { ConcurrentHashMap() }[connectionId] = emitter
    }

    fun findAllByUserId(userId: Long): Map<String, SseEmitter> = emitters[userId]?.toMap().orEmpty()

    fun delete(userId: Long, connectionId: String) {
        val userEmitters = emitters[userId] ?: return

        userEmitters.remove(connectionId)
        if (userEmitters.isEmpty()) {
            emitters.remove(userId, userEmitters)
        }
    }
}
