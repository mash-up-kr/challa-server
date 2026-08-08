package com.challa.core.notification

interface DeviceTokenRepository {
    fun save(deviceToken: DeviceToken): DeviceToken

    fun findAllByUserId(userId: Long): List<DeviceToken>

    fun deleteByUserIdAndToken(userId: Long, token: String)
}

interface PushSender {
    /** 전송에 성공한 토큰 수를 돌려준다. */
    fun send(tokens: List<String>, title: String, body: String): Int
}
