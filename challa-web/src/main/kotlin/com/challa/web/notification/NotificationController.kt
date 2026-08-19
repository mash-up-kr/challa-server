package com.challa.web.notification

import com.challa.core.notification.DeleteDeviceTokenUseCase
import com.challa.core.notification.GetMarketingPushAgreementUseCase
import com.challa.core.notification.RegisterDeviceTokenUseCase
import com.challa.core.notification.SendTestPushUseCase
import com.challa.core.notification.UpdateMarketingPushAgreementUseCase
import com.challa.web.common.response.ApiResponse
import com.challa.web.security.AuthUserId
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/notifications")
class NotificationController(
    private val registerDeviceTokenUseCase: RegisterDeviceTokenUseCase,
    private val deleteDeviceTokenUseCase: DeleteDeviceTokenUseCase,
    private val sendTestPushUseCase: SendTestPushUseCase,
    private val getMarketingPushAgreementUseCase: GetMarketingPushAgreementUseCase,
    private val updateMarketingPushAgreementUseCase: UpdateMarketingPushAgreementUseCase
) {
    @PostMapping("/tokens")
    fun registerToken(
        @AuthUserId userId: Long,
        @RequestBody request: NotificationEnvelope<RegisterDeviceTokenRequest>
    ): ApiResponse<Unit?> {
        registerDeviceTokenUseCase.register(request.notification.toCommand(userId))
        return ApiResponse.empty()
    }

    @DeleteMapping("/tokens")
    fun deleteToken(
        @AuthUserId userId: Long,
        @RequestBody request: NotificationEnvelope<DeleteDeviceTokenRequest>
    ): ApiResponse<Unit?> {
        deleteDeviceTokenUseCase.delete(request.notification.toCommand(userId))
        return ApiResponse.empty()
    }

    @Operation(
        summary = "테스트 푸시 발송",
        description = """
            내 계정에 등록된 FCM 토큰 전부로 푸시를 보내고, 전송에 성공한 토큰 수를 `sentCount` 로 돌려준다.

            - 먼저 `POST /api/v1/notifications/tokens` 로 기기 토큰을 등록해야 한다.
            - `title`, `body` 는 생략 가능하다. 기본값은 `찰라` / `테스트 푸시입니다`.
            - `sentCount` 가 0 이면 이 계정에 등록된 토큰이 없다는 뜻이다.
            - 만료된 토큰은 자동으로 정리하지 않는다. 등록은 돼 있는데 안 오면 앱에서 토큰을 다시 등록해라.
            - 서버에 FCM 자격증명이 없으면 500 이 난다.
        """
    )
    @PostMapping("/test")
    fun sendTest(
        @AuthUserId userId: Long,
        @RequestBody request: NotificationEnvelope<SendTestPushRequest>
    ): ApiResponse<NotificationEnvelope<SendTestPushResponse>> {
        val result = sendTestPushUseCase.sendTest(request.notification.toCommand(userId))
        return ApiResponse.ok(NotificationEnvelope(SendTestPushResponse.fromResult(result)))
    }

    @Operation(summary = "광고성 푸시 수신 동의 조회")
    @GetMapping("/marketing")
    fun getMarketingAgreement(
        @AuthUserId userId: Long
    ): ApiResponse<NotificationEnvelope<MarketingPushAgreementResponse>> = ApiResponse.ok(
        NotificationEnvelope(MarketingPushAgreementResponse(getMarketingPushAgreementUseCase.get(userId)))
    )

    @Operation(summary = "광고성 푸시 수신 동의 변경")
    @PostMapping("/marketing")
    fun updateMarketingAgreement(
        @AuthUserId userId: Long,
        @RequestBody request: NotificationEnvelope<UpdateMarketingPushAgreementRequest>
    ): ApiResponse<Unit?> {
        updateMarketingPushAgreementUseCase.update(request.notification.toCommand(userId))
        return ApiResponse.empty()
    }
}
