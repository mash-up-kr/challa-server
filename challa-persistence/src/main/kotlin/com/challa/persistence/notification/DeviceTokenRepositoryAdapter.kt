package com.challa.persistence.notification

import com.challa.core.notification.DeviceToken
import com.challa.core.notification.DeviceTokenRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class DeviceTokenRepositoryAdapter(private val jpa: DeviceTokenJpaRepository) : DeviceTokenRepository {

    // 같은 기기가 다른 계정으로 로그인하면 토큰 주인만 바뀐다. 토큰은 전역 유일하다.
    @Transactional
    override fun save(deviceToken: DeviceToken): DeviceToken {
        val entity = jpa.findByToken(deviceToken.token)?.apply { userId = deviceToken.userId }
            ?: DeviceTokenEntity(userId = deviceToken.userId, token = deviceToken.token)
        return jpa.save(entity).toDomain()
    }

    @Transactional(readOnly = true)
    override fun findAllByUserId(userId: Long): List<DeviceToken> =
        jpa.findAllByUserId(userId).map(DeviceTokenEntity::toDomain)

    @Transactional
    override fun deleteByUserIdAndToken(userId: Long, token: String) {
        jpa.deleteByUserIdAndToken(userId, token)
    }
}
