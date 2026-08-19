package com.challa.core.notification

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MarketingPushAgreementServiceTest {
    private class FakeAgreementRepository : MarketingPushAgreementRepository {
        val store = mutableMapOf<Long, Boolean>()

        override fun findAgreedByUserId(userId: Long): Boolean? = store[userId]

        override fun save(userId: Long, agreed: Boolean) {
            store[userId] = agreed
        }
    }

    private val agreements = FakeAgreementRepository()
    private val service = MarketingPushAgreementService(agreements)

    @Test
    fun `no record means not agreed`() {
        assertFalse(service.get(1))
    }

    @Test
    fun `update persists the new agreement`() {
        service.update(UpdateMarketingPushAgreementCommand(userId = 1, agreed = true))
        assertTrue(service.get(1))

        service.update(UpdateMarketingPushAgreementCommand(userId = 1, agreed = false))
        assertFalse(service.get(1))
    }

    @Test
    fun `agreement is per user`() {
        service.update(UpdateMarketingPushAgreementCommand(userId = 1, agreed = true))

        assertFalse(service.get(2))
    }
}
