package com.challa.core.app.port.input

data class GetAppVersionResult(
    val updateRequired: Boolean,
    val updateAvailable: Boolean,
    val latestVersion: String,
    val storeUrl: String
)
