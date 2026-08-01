package com.challa.core.shoot.business

import com.challa.core.shoot.CameraFiltersStorage
import com.challa.core.shoot.CameraFilterUseCase
import com.challa.core.shoot.GetCameraFiltersResult
import org.springframework.stereotype.Service

@Service
class CameraFilterService(
    private val cameraFiltersStorage: CameraFiltersStorage
) : CameraFilterUseCase {

    override fun getAll(): GetCameraFiltersResult {
        return GetCameraFiltersResult(cameraFilters = cameraFiltersStorage.getAll())
    }
}
