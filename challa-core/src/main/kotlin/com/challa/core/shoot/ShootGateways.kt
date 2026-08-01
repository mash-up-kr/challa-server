package com.challa.core.shoot

import com.challa.core.shoot.domain.CameraFilter

interface CameraFiltersStorage {
    fun getAll(): List<CameraFilter>
}
