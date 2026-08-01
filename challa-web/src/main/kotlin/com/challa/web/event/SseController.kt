package com.challa.web.event

import com.challa.web.security.AuthUserId
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@RestController
@RequestMapping("/api/v1/sse")
class SseController(private val sseConnectionService: SseConnectionService) {
    @GetMapping("/events", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun subscribe(@AuthUserId userId: Long): SseEmitter = sseConnectionService.subscribe(userId)
}
