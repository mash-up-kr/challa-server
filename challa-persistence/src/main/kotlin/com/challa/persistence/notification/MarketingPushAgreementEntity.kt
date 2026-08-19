package com.challa.persistence.notification

import com.challa.persistence.common.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    name = "marketing_push_agreements",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_marketing_push_agreements_user_id", columnNames = ["user_id"])
    ]
)
class MarketingPushAgreementEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "user_id", nullable = false)
    var userId: Long,

    @Column(name = "agreed", nullable = false)
    var agreed: Boolean
) : BaseEntity()
