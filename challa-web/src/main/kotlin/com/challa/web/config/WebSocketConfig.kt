package com.challa.web.config

import com.challa.web.security.WebSocketAuthenticationHandshakeInterceptor
import com.challa.web.security.WebSocketChannelInterceptor
import com.challa.web.security.WebSocketPrincipalHandshakeHandler
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig(
    private val webSocketAuthenticationHandshakeInterceptor: WebSocketAuthenticationHandshakeInterceptor,
    private val webSocketChannelInterceptor: WebSocketChannelInterceptor,
    private val webSocketPrincipalHandshakeHandler: WebSocketPrincipalHandshakeHandler
) : WebSocketMessageBrokerConfigurer {
    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry
            .addEndpoint("/api/v1/ws")
            .addInterceptors(webSocketAuthenticationHandshakeInterceptor)
            .setHandshakeHandler(webSocketPrincipalHandshakeHandler)
            .setAllowedOriginPatterns("*")
    }

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.setApplicationDestinationPrefixes("/app")
        registry.enableSimpleBroker(
            "/topic",
            "/queue"
        )
    }

    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        registration.interceptors(webSocketChannelInterceptor)
    }
}
