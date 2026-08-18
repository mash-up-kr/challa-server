package com.challa.bootstrap

import com.challa.core.app.domain.AppVersion
import com.challa.core.app.port.input.GetAppVersionUsecase
import com.challa.core.app.port.output.AppVersionProvider
import com.challa.core.auth.LoginUseCase
import com.challa.core.auth.RefreshTokenUseCase
import com.challa.core.photo.CompletePhotoUseCase
import com.challa.core.room.domain.RoomCoverSticker
import com.challa.core.room.domain.RoomStickerColor
import com.challa.core.room.port.output.RoomCoverStickerProvider
import com.challa.core.shoot.CameraFiltersStorage
import com.challa.core.shoot.domain.CameraFilter
import com.challa.core.upload.IssueUploadUrlUseCase
import com.challa.core.user.DeleteAccountUseCase
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
@Import(TestStorageConfig::class)
class ChallaApplicationTests {

    @Autowired
    private lateinit var loginUseCase: LoginUseCase

    @Autowired
    private lateinit var refreshTokenUseCase: RefreshTokenUseCase

    @Autowired
    private lateinit var deleteAccountUseCase: DeleteAccountUseCase

    @Autowired
    private lateinit var issueUploadUrlUseCase: IssueUploadUrlUseCase

    @Autowired
    private lateinit var completePhotoUseCase: CompletePhotoUseCase

    @Autowired
    private lateinit var getAppVersionUsecase: GetAppVersionUsecase

    @Test
    fun `context loads and wires the core use cases with their adapters`() {
        assertNotNull(loginUseCase)
        assertNotNull(refreshTokenUseCase)
        assertNotNull(deleteAccountUseCase)
        assertNotNull(issueUploadUrlUseCase)
        assertNotNull(completePhotoUseCase)
        assertNotNull(getAppVersionUsecase)
    }
}

@TestConfiguration
class TestStorageConfig {

    @Bean
    fun cameraFiltersStorage(): CameraFiltersStorage = object : CameraFiltersStorage {
        override fun getAll() = emptyList<CameraFilter>()
    }

    @Bean
    fun roomCoverStickerProvider(): RoomCoverStickerProvider = object : RoomCoverStickerProvider {
        override fun getAllStickers() = emptyList<RoomCoverSticker>()
        override fun getAllColors() = emptyList<RoomStickerColor>()
    }

    @Bean
    fun appVersionProvider(): AppVersionProvider = object : AppVersionProvider {
        override fun getAppVersion() = AppVersion(
            android = appVersionPlatform(),
            ios = appVersionPlatform()
        )

        private fun appVersionPlatform() = AppVersion.Version(
            minimumVersion = "1.0.0",
            latestVersion = "1.0.0",
            storeUrl = "https://example.com"
        )
    }
}
