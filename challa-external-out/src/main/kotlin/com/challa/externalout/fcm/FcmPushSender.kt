package com.challa.externalout.fcm

import com.challa.core.notification.PushSender
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.MulticastMessage
import com.google.firebase.messaging.Notification
import org.springframework.stereotype.Component

@Component
class FcmPushSender(private val properties: FcmProperties) : PushSender {

    // 자격증명이 없어도 앱은 뜨고, 실제로 보낼 때만 터진다.
    private val messaging: FirebaseMessaging by lazy {
        check(properties.credentials.isNotBlank()) { "fcm.credentials is not configured" }
        val app = FirebaseApp.getApps().firstOrNull { it.name == FirebaseApp.DEFAULT_APP_NAME }
            ?: FirebaseApp.initializeApp(
                FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(properties.credentials.byteInputStream()))
                    .build()
            )
        FirebaseMessaging.getInstance(app)
    }

    override fun send(tokens: List<String>, title: String, body: String): Int {
        if (tokens.isEmpty()) return 0
        val message = MulticastMessage.builder()
            .setNotification(Notification.builder().setTitle(title).setBody(body).build())
            .addAllTokens(tokens)
            .build()
        return messaging.sendEachForMulticast(message).successCount
    }
}
