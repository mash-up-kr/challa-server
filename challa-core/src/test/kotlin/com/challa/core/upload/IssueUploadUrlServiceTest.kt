package com.challa.core.upload

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class IssueUploadUrlServiceTest {
    private class RecordingIssuer : PresignedUploadUrlIssuer {
        val keys = mutableListOf<String>()
        var lastContentType: String? = null

        override fun issue(key: String, contentType: String): UploadUrl {
            keys += key
            lastContentType = contentType
            return UploadUrl(
                uploadUrl = "https://bucket/$key?sig=x",
                imageUrl = "https://bucket/$key",
                expiresInSeconds = 300
            )
        }
    }

    private lateinit var issuer: RecordingIssuer
    private lateinit var service: IssueUploadUrlService

    @BeforeEach
    fun setUp() {
        issuer = RecordingIssuer()
        service = IssueUploadUrlService(issuer)
    }

    @Test
    fun `keys are namespaced by purpose and user so one user cannot overwrite another`() {
        service.issue(7, IssueUploadUrlCommand(UploadPurpose.PROFILE_IMAGE, "image/jpeg"))
        service.issue(8, IssueUploadUrlCommand(UploadPurpose.PHOTO, "image/png"))

        assert(issuer.keys[0].matches(Regex("^profile/7/[0-9a-f-]{36}$"))) { issuer.keys[0] }
        assert(issuer.keys[1].matches(Regex("^photo/8/[0-9a-f-]{36}$"))) { issuer.keys[1] }
    }

    @Test
    fun `two requests never collide on the same key`() {
        service.issue(1, IssueUploadUrlCommand(UploadPurpose.PROFILE_IMAGE, "image/jpeg"))
        service.issue(1, IssueUploadUrlCommand(UploadPurpose.PROFILE_IMAGE, "image/jpeg"))

        assertNotEquals(issuer.keys[0], issuer.keys[1])
    }

    @Test
    fun `content type is normalized before it is signed`() {
        service.issue(1, IssueUploadUrlCommand(UploadPurpose.PROFILE_IMAGE, "  IMAGE/JPEG "))

        assertEquals("image/jpeg", issuer.lastContentType)
    }

    @Test
    fun `a non-image content type is rejected instead of signed`() {
        assertThrows(UnsupportedImageTypeException::class.java) {
            service.issue(1, IssueUploadUrlCommand(UploadPurpose.PROFILE_IMAGE, "application/pdf"))
        }
        assertEquals(emptyList<String>(), issuer.keys)
    }
}
