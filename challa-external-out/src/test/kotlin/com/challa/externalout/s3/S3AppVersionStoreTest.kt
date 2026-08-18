package com.challa.externalout.s3

import com.challa.core.app.domain.AppVersion
import com.challa.externalout.s3.properties.S3FilePathProperties
import com.fasterxml.jackson.core.type.TypeReference
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class S3AppVersionStoreTest {
    private val s3PreLoader = mockk<S3PreLoader>()
    private val filePathProperties = S3FilePathProperties(
        cameraFilters = "challa/camera-filters.json",
        roomCoverStickers = "challa/room-cover-stickers.json",
        roomStickerColors = "challa/room-sticker-colors.json",
        appVersion = APP_VERSION_PATH
    )
    private val store = S3AppVersionStore(s3PreLoader, filePathProperties)

    @Test
    fun `loads the app version from the configured S3 path`() {
        val appVersion = AppVersion(
            android = AppVersion.Version(
                minimumVersion = "1.2.0",
                latestVersion = "1.5.0",
                storeUrl = "https://play.google.com/store/apps/details?id=com.challa"
            ),
            ios = AppVersion.Version(
                minimumVersion = "1.3.0",
                latestVersion = "1.6.0",
                storeUrl = "https://apps.apple.com/app/challa"
            )
        )
        every {
            s3PreLoader.load(
                APP_VERSION_PATH,
                any<TypeReference<AppVersion>>()
            )
        } returns appVersion

        store.initialize()

        assertEquals(appVersion, store.getAppVersion())
        verify(exactly = 1) {
            s3PreLoader.load(
                APP_VERSION_PATH,
                any<TypeReference<AppVersion>>()
            )
        }
    }

    private companion object {
        const val APP_VERSION_PATH = "challa/version.json"
    }
}
