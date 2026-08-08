package com.challa.persistence.notification

import com.challa.core.notification.DeviceToken
import com.challa.persistence.common.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    name = "device_tokens",
    uniqueConstraints = [UniqueConstraint(name = "uk_device_tokens_token", columnNames = ["token"])],
    indexes = [Index(name = "idx_device_tokens_user_id", columnList = "user_id")]
)
class DeviceTokenEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "user_id", nullable = false)
    var userId: Long,

    @Column(name = "token", nullable = false, length = 512)
    var token: String
) : BaseEntity() {
    fun toDomain(): DeviceToken = DeviceToken(id = id, userId = userId, token = token)
}
