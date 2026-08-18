package com.challa.core.app

import com.challa.core.app.application.GetAppVersionService
import com.challa.core.app.domain.AppOs
import com.challa.core.app.domain.AppVersion
import com.challa.core.app.port.input.GetAppVersionCommand
import com.challa.core.app.port.input.GetAppVersionResult
import com.challa.core.app.port.output.AppVersionProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetAppVersionServiceTest {
    private val appVersionProvider = mockk<AppVersionProvider>()
    private val service = GetAppVersionService(appVersionProvider)

    @Test
    fun `requires an update when the current version is below the minimum version`() {
        every { appVersionProvider.getAppVersion() } returns appVersion()

        val result = service.getAppVersion(GetAppVersionCommand(os = AppOs.ANDROID, version = "1.1.0"))

        assertEquals(
            GetAppVersionResult(
                updateRequired = true,
                updateAvailable = true,
                latestVersion = "1.5.0",
                storeUrl = ANDROID_STORE_URL
            ),
            result
        )
        verify(exactly = 1) { appVersionProvider.getAppVersion() }
    }

    @Test
    fun `offers an optional update when the current version is at least the minimum and below the latest`() {
        every { appVersionProvider.getAppVersion() } returns appVersion()

        val result = service.getAppVersion(GetAppVersionCommand(os = AppOs.IOS, version = "1.3.0"))

        assertEquals(
            GetAppVersionResult(
                updateRequired = false,
                updateAvailable = true,
                latestVersion = "1.6.0",
                storeUrl = IOS_STORE_URL
            ),
            result
        )
        verify(exactly = 1) { appVersionProvider.getAppVersion() }
    }

    @Test
    fun `does not offer an update when the current version is the latest version`() {
        every { appVersionProvider.getAppVersion() } returns appVersion()

        val result = service.getAppVersion(GetAppVersionCommand(os = AppOs.ANDROID, version = "1.5.0"))

        assertEquals(false, result.updateRequired)
        assertEquals(false, result.updateAvailable)
    }

    @Test
    fun `compares each version segment numerically`() {
        every { appVersionProvider.getAppVersion() } returns appVersion(
            android = AppVersion.Version(
                minimumVersion = "1.9.9",
                latestVersion = "1.10.0",
                storeUrl = ANDROID_STORE_URL
            )
        )

        val result = service.getAppVersion(GetAppVersionCommand(os = AppOs.ANDROID, version = "1.10.0"))

        assertEquals(false, result.updateRequired)
        assertEquals(false, result.updateAvailable)
    }

    private fun appVersion(
        android: AppVersion.Version = AppVersion.Version(
            minimumVersion = "1.2.0",
            latestVersion = "1.5.0",
            storeUrl = ANDROID_STORE_URL
        )
    ) = AppVersion(
        android = android,
        ios = AppVersion.Version(
            minimumVersion = "1.3.0",
            latestVersion = "1.6.0",
            storeUrl = IOS_STORE_URL
        )
    )

    private companion object {
        const val ANDROID_STORE_URL = "https://play.google.com/store/apps/details?id=com.challa"
        const val IOS_STORE_URL = "https://apps.apple.com/app/challa"
    }
}
