package com.challa.externalout.s3

import com.challa.core.upload.IssueUploadUrlCommand
import com.challa.core.upload.IssueUploadUrlService
import com.challa.core.upload.UploadPurpose
import com.challa.externalout.s3.properties.S3Properties
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.net.URI

class S3PresignedUrlAdapterTest {
    @Test
    fun `PHOTO produces signed URLs for both original and thumbnail objects`() {
        val adapter = S3PresignedUrlAdapter(
            S3Properties(
                bucket = "photo-bucket",
                region = "ap-northeast-2",
                accessKey = "test-access-key",
                secretKey = "test-secret-key"
            )
        )

        val result = IssueUploadUrlService(adapter).issue(
            userId = 7,
            command = IssueUploadUrlCommand(UploadPurpose.PHOTO, "image/jpeg")
        )

        val thumbnailUploadUrl = requireNotNull(result.thumbnailUploadUrl)
        val thumbnailImageUrl = requireNotNull(result.thumbnailImageUrl)
        assertTrue(result.uploadUrl.contains("X-Amz-Signature="))
        assertTrue(thumbnailUploadUrl.contains("X-Amz-Signature="))
        assertEquals("${URI.create(result.imageUrl).path}_thumbnail", URI.create(thumbnailImageUrl).path)
    }
}
