package com.challa.web.security

import com.challa.web.security.JwtAuthenticationFilter.Companion.AUTH_USER_ID_ATTRIBUTE
import org.springframework.http.HttpStatus
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.HandshakeInterceptor

@Component
class WebSocketAuthenticationHandshakeInterceptor : HandshakeInterceptor {
    override fun beforeHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        attributes: MutableMap<String, Any>
    ): Boolean {
        val servletRequest = (request as ServletServerHttpRequest).servletRequest
        val userId = servletRequest.getAttribute(AUTH_USER_ID_ATTRIBUTE)
        if (userId == null) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED)
            return false
        }

        attributes["userId"] = userId

        return true
    }

    override fun afterHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        exception: Exception?
    ) {
        return
    }
}
