package com.challa.web.config

import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.MessageHandler
import org.springframework.messaging.simp.broker.SimpleBrokerMessageHandler
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ExecutorChannelInterceptor
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component

@Component
class WebSocketSubscriptionReceiptInterceptor : ExecutorChannelInterceptor {
    override fun afterMessageHandled(
        message: Message<*>,
        channel: MessageChannel,
        handler: MessageHandler,
        ex: Exception?
    ) {
        if (ex != null || handler !is SimpleBrokerMessageHandler) {
            return
        }

        val subscribeAccessor = StompHeaderAccessor.wrap(message)
        if (subscribeAccessor.command != StompCommand.SUBSCRIBE) {
            return
        }

        val destination = subscribeAccessor.destination ?: return
        if (handler.destinationPrefixes.none(destination::startsWith)) {
            return
        }

        val receiptId = subscribeAccessor.receipt ?: return
        val sessionId = subscribeAccessor.sessionId ?: return
        subscribeAccessor.subscriptionId ?: return

        val receiptAccessor = StompHeaderAccessor.create(StompCommand.RECEIPT).apply {
            setReceiptId(receiptId)
            setSessionId(sessionId)
        }
        val receiptMessage = MessageBuilder.createMessage(
            ByteArray(0),
            receiptAccessor.messageHeaders
        )

        handler.clientOutboundChannel.send(receiptMessage)
    }
}
