package com.challa.core.notification

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class NotificationServiceTest {
    private class FakeDeviceTokenRepository : DeviceTokenRepository {
        val store = mutableListOf<DeviceToken>()
        private var sequence = 0L

        override fun save(deviceToken: DeviceToken): DeviceToken {
            store.removeIf { it.token == deviceToken.token }
            val saved = deviceToken.copy(id = ++sequence)
            store += saved
            return saved
        }

        override fun findAllByUserId(userId: Long): List<DeviceToken> = store.filter { it.userId == userId }

        override fun deleteByUserIdAndToken(userId: Long, token: String) {
            store.removeIf { it.userId == userId && it.token == token }
        }
    }

    private class FakePushSender : PushSender {
        var lastTokens: List<String> = emptyList()

        override fun send(tokens: List<String>, title: String, body: String): Int {
            lastTokens = tokens
            return tokens.size
        }
    }

    private lateinit var tokens: FakeDeviceTokenRepository
    private lateinit var pushSender: FakePushSender
    private lateinit var service: NotificationService

    @BeforeEach
    fun setUp() {
        tokens = FakeDeviceTokenRepository()
        pushSender = FakePushSender()
        service = NotificationService(tokens, pushSender)
    }

    @Test
    fun `re-registering the same token moves it to the new owner`() {
        service.register(RegisterDeviceTokenCommand(userId = 1, token = "device-a"))
        service.register(RegisterDeviceTokenCommand(userId = 2, token = "device-a"))

        assertEquals(emptyList<DeviceToken>(), tokens.findAllByUserId(1))
        assertEquals(listOf("device-a"), tokens.findAllByUserId(2).map { it.token })
    }

    @Test
    fun `delete only removes the caller's own token`() {
        service.register(RegisterDeviceTokenCommand(userId = 1, token = "device-a"))

        service.delete(DeleteDeviceTokenCommand(userId = 2, token = "device-a"))
        assertEquals(1, tokens.findAllByUserId(1).size)

        service.delete(DeleteDeviceTokenCommand(userId = 1, token = "device-a"))
        assertEquals(0, tokens.findAllByUserId(1).size)
    }

    @Test
    fun `test push targets every token of the user and none of the others`() {
        service.register(RegisterDeviceTokenCommand(userId = 1, token = "device-a"))
        service.register(RegisterDeviceTokenCommand(userId = 1, token = "device-b"))
        service.register(RegisterDeviceTokenCommand(userId = 2, token = "device-c"))

        val result = service.sendTest(SendTestPushCommand(userId = 1, title = "제목", body = "본문"))

        assertEquals(listOf("device-a", "device-b"), pushSender.lastTokens)
        assertEquals(2, result.sentCount)
    }

    @Test
    fun `no registered token sends nothing`() {
        val result = service.sendTest(SendTestPushCommand(userId = 9, title = "제목", body = "본문"))

        assertEquals(0, result.sentCount)
    }
}
