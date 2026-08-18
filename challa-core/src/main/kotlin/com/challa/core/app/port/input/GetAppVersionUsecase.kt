package com.challa.core.app.port.input

interface GetAppVersionUsecase {
    fun getAppVersion(command: GetAppVersionCommand): GetAppVersionResult
}
