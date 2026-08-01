package com.challa.web.event

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

class SseEmitterRepositoryTest {
    private val repository = SseEmitterRepository()

    @Test
    fun `emitters are stored and removed per user`() {
        val first = SseEmitter()
        val second = SseEmitter()

        repository.save(userId = 7L, connectionId = "first", emitter = first)
        repository.save(userId = 7L, connectionId = "second", emitter = second)

        assertThat(repository.findAllByUserId(7L)).containsExactlyInAnyOrderEntriesOf(
            mapOf("first" to first, "second" to second)
        )

        repository.delete(userId = 7L, connectionId = "first")
        assertThat(repository.findAllByUserId(7L)).containsOnlyKeys("second")

        repository.delete(userId = 7L, connectionId = "second")
        assertThat(repository.findAllByUserId(7L)).isEmpty()
    }
}
