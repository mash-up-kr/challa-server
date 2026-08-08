package com.challa.web.security

import com.challa.web.security.JwtAuthenticationFilter.Companion.AUTH_USER_ID_ATTRIBUTE
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.server.ServerHttpResponse
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.web.socket.WebSocketHandler

class WebSocketAuthenticationHandshakeInterceptorTest {
    @Test
    fun `rejects a handshake without an authenticated user with unauthorized status`() {
        val servletRequest = mockk<HttpServletRequest>()
        val request = mockk<ServletServerHttpRequest>()
        val response = mockk<ServerHttpResponse>(relaxed = true)
        val webSocketHandler = mockk<WebSocketHandler>()

        every { request.servletRequest } returns servletRequest
        every { servletRequest.getAttribute(AUTH_USER_ID_ATTRIBUTE) } returns null

        val result = WebSocketAuthenticationHandshakeInterceptor().beforeHandshake(
            request = request,
            response = response,
            wsHandler = webSocketHandler,
            attributes = mutableMapOf()
        )

        assertFalse(result)
        verify(exactly = 1) { response.setStatusCode(HttpStatus.UNAUTHORIZED) }
    }
}
