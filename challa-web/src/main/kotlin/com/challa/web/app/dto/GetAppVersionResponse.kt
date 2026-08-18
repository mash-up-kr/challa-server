package com.challa.web.app.dto

import com.challa.core.app.port.input.GetAppVersionResult

data class GetAppVersionResponse(
    val updateRequired: Boolean,
    val updateAvailable: Boolean,
    val latestVersion: String,
    val storeUrl: String
) {
    companion object {
        fun fromResult(result: GetAppVersionResult) = GetAppVersionResponse(
            updateRequired = result.updateRequired,
            updateAvailable = result.updateAvailable,
            latestVersion = result.latestVersion,
            storeUrl = result.storeUrl
        )
    }
}
