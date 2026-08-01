package com.challa.web.shoots

import com.challa.core.shoot.GetCameraFiltersResult
import com.challa.core.shoot.domain.CameraFilter

data class GetCameraFiltersResponse(val cameraFilters: List<CameraFilter>) {
    companion object {
        fun fromResult(result: GetCameraFiltersResult) = GetCameraFiltersResponse(
            cameraFilters = result.cameraFilters
        )
    }
}
