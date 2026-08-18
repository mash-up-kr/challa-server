package com.challa.core.app.application

import com.challa.core.app.port.input.GetAppVersionCommand
import com.challa.core.app.port.input.GetAppVersionResult
import com.challa.core.app.port.input.GetAppVersionUsecase
import com.challa.core.app.port.output.AppVersionProvider
import org.springframework.stereotype.Service

@Service
class GetAppVersionService(private val appVersionProvider: AppVersionProvider) : GetAppVersionUsecase {
    override fun getAppVersion(command: GetAppVersionCommand): GetAppVersionResult {
        val version = appVersionProvider.getAppVersion().getVersionByOs(command.os)

        return GetAppVersionResult(
            updateRequired = version.isUpdateRequired(command.version),
            updateAvailable = version.isUpdateAvailable(command.version),
            latestVersion = version.latestVersion,
            storeUrl = version.storeUrl
        )
    }
}
