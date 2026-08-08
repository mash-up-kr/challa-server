package com.challa.persistence.notification

import org.springframework.data.jpa.repository.JpaRepository

interface DeviceTokenJpaRepository : JpaRepository<DeviceTokenEntity, Long> {
    fun findByToken(token: String): DeviceTokenEntity?

    fun findAllByUserId(userId: Long): List<DeviceTokenEntity>

    fun deleteByUserIdAndToken(userId: Long, token: String)
}
