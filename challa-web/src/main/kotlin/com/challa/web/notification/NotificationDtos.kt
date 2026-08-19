package com.challa.web.notification

import com.challa.core.notification.DeleteDeviceTokenCommand
import com.challa.core.notification.RegisterDeviceTokenCommand
import com.challa.core.notification.SendTestPushCommand
import com.challa.core.notification.SendTestPushResult
import com.challa.core.notification.UpdateMarketingPushAgreementCommand
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

data class NotificationEnvelope<T : Any>(val notification: T)

data class RegisterDeviceTokenRequest(
    @param:JsonProperty(required = true)
    @field:Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "FCM registration token")
    val token: String
) {
    fun toCommand(userId: Long): RegisterDeviceTokenCommand = RegisterDeviceTokenCommand(userId, token)
}

data class DeleteDeviceTokenRequest(
    @param:JsonProperty(required = true)
    @field:Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "FCM registration token")
    val token: String
) {
    fun toCommand(userId: Long): DeleteDeviceTokenCommand = DeleteDeviceTokenCommand(userId = userId, token = token)
}

data class SendTestPushRequest(val title: String = "찰라", val body: String = "테스트 푸시입니다") {
    fun toCommand(userId: Long): SendTestPushCommand = SendTestPushCommand(userId, title, body)
}

data class SendTestPushResponse(val sentCount: Int) {
    companion object {
        fun fromResult(result: SendTestPushResult): SendTestPushResponse =
            SendTestPushResponse(sentCount = result.sentCount)
    }
}

data class UpdateMarketingPushAgreementRequest(
    @param:JsonProperty(required = true)
    @field:Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "광고성 푸시 수신 동의 여부")
    val agreed: Boolean
) {
    fun toCommand(userId: Long): UpdateMarketingPushAgreementCommand =
        UpdateMarketingPushAgreementCommand(userId = userId, agreed = agreed)
}

data class MarketingPushAgreementResponse(val agreed: Boolean)
