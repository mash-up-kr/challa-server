package com.challa.core.notification

interface RegisterDeviceTokenUseCase {
    fun register(command: RegisterDeviceTokenCommand)
}

interface DeleteDeviceTokenUseCase {
    fun delete(command: DeleteDeviceTokenCommand)
}

interface SendTestPushUseCase {
    fun sendTest(command: SendTestPushCommand): SendTestPushResult
}

data class RegisterDeviceTokenCommand(val userId: Long, val token: String)

data class DeleteDeviceTokenCommand(val userId: Long, val token: String)

data class SendTestPushCommand(val userId: Long, val title: String, val body: String)

data class SendTestPushResult(val sentCount: Int)

interface GetMarketingPushAgreementUseCase {
    fun get(userId: Long): Boolean
}

interface UpdateMarketingPushAgreementUseCase {
    fun update(command: UpdateMarketingPushAgreementCommand)
}

data class UpdateMarketingPushAgreementCommand(val userId: Long, val agreed: Boolean)
