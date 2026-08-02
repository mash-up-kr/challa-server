package com.challa.web.shoots

import com.challa.core.shoot.CameraFilterUseCase
import com.challa.web.common.response.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/shoots")
class ShootController(private val cameraFilterUseCase: CameraFilterUseCase) {

    @GetMapping("/camera-filters")
    fun getCameraFilters(): ApiResponse<ShootEnvelope<GetCameraFiltersResponse>> =
        ApiResponse.ok(ShootEnvelope(GetCameraFiltersResponse.fromResult(cameraFilterUseCase.getAll())))
}
