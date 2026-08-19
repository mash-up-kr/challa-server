package com.challa.core.notification

class MarketingPushAgreementService(private val agreements: MarketingPushAgreementRepository) :
    GetMarketingPushAgreementUseCase,
    UpdateMarketingPushAgreementUseCase {

    override fun get(userId: Long): Boolean = agreements.findAgreedByUserId(userId) ?: false

    override fun update(command: UpdateMarketingPushAgreementCommand) {
        agreements.save(command.userId, command.agreed)
    }
}
