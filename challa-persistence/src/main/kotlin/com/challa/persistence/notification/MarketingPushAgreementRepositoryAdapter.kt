package com.challa.persistence.notification

import com.challa.core.notification.MarketingPushAgreementRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class MarketingPushAgreementRepositoryAdapter(private val jpa: MarketingPushAgreementJpaRepository) :
    MarketingPushAgreementRepository {

    @Transactional(readOnly = true)
    override fun findAgreedByUserId(userId: Long): Boolean? = jpa.findByUserId(userId)?.agreed

    @Transactional
    override fun save(userId: Long, agreed: Boolean) {
        val entity = jpa.findByUserId(userId)?.apply { this.agreed = agreed }
            ?: MarketingPushAgreementEntity(userId = userId, agreed = agreed)
        jpa.save(entity)
    }
}
