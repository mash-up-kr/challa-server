package com.challa.bootstrap

import com.challa.core.auth.LoginUseCase
import com.challa.core.auth.RefreshTokenUseCase
import com.challa.core.photo.CompletePhotoUseCase
import com.challa.core.room.domain.RoomCoverSticker
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

    @Test
    fun `context loads and wires the core use cases with their adapters`() {
        assertNotNull(loginUseCase)
        assertNotNull(refreshTokenUseCase)
        assertNotNull(deleteAccountUseCase)
        assertNotNull(issueUploadUrlUseCase)
        assertNotNull(completePhotoUseCase)
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
        override fun getAll() = emptyList<RoomCoverSticker>()
    }
}
