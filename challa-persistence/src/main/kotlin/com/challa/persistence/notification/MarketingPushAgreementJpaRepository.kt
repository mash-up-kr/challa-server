package com.challa.persistence.notification

import org.springframework.data.jpa.repository.JpaRepository

interface MarketingPushAgreementJpaRepository : JpaRepository<MarketingPushAgreementEntity, Long> {
    fun findByUserId(userId: Long): MarketingPushAgreementEntity?
}
