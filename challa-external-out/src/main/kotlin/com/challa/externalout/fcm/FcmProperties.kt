package com.challa.externalout.fcm

import org.springframework.boot.context.properties.ConfigurationProperties

/** [credentials] 는 Firebase 서비스 계정 JSON 원문이다. 비어 있으면 푸시 전송 시점에 실패한다. */
@ConfigurationProperties(prefix = "fcm")
data class FcmProperties(val credentials: String = "")
