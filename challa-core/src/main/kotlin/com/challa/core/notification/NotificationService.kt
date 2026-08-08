package com.challa.core.notification

class NotificationService(private val deviceTokens: DeviceTokenRepository, private val pushSender: PushSender) :
    RegisterDeviceTokenUseCase,
    DeleteDeviceTokenUseCase,
    SendTestPushUseCase {

    override fun register(command: RegisterDeviceTokenCommand) {
        deviceTokens.save(DeviceToken(userId = command.userId, token = command.token))
    }

    override fun delete(command: DeleteDeviceTokenCommand) {
        deviceTokens.deleteByUserIdAndToken(command.userId, command.token)
    }

    // ponytail: FCM 이 만료 토큰으로 응답해도 정리하지 않는다. 미전송이 쌓이면 send() 가 실패 토큰을 돌려주고 지우도록 넓힌다.
    override fun sendTest(command: SendTestPushCommand): SendTestPushResult {
        val tokens = deviceTokens.findAllByUserId(command.userId).map { it.token }
        return SendTestPushResult(sentCount = pushSender.send(tokens, command.title, command.body))
    }
}
