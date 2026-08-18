package com.challa.web.app

import com.challa.core.app.domain.AppOs
import com.challa.core.app.port.input.GetAppVersionUsecase
import com.challa.web.app.dto.AppEnvelope
import com.challa.web.app.dto.GetAppVersionRequest
import com.challa.web.app.dto.GetAppVersionResponse
import com.challa.web.common.response.ApiResponse
import com.challa.web.security.PublicEndpoint
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/app")
class AppController(private val getAppVersionUsecase: GetAppVersionUsecase) {
    @PublicEndpoint
    @GetMapping("/version")
    fun getAppVersion(
        @RequestParam os: AppOs,
        @RequestParam version: String
    ): ApiResponse<AppEnvelope<GetAppVersionResponse>> {
        val result = getAppVersionUsecase.getAppVersion(GetAppVersionRequest(os = os, version = version).toCommand())

        return ApiResponse.ok(AppEnvelope(GetAppVersionResponse.fromResult(result)))
    }
}
