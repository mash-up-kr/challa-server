package com.challa.externalout.s3

import com.challa.externalout.s3.properties.S3Properties
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse

class S3UploadedObjectDeleterTest {
    private val s3Client = mockk<S3Client>()
    private val deleter = S3UploadedObjectDeleter(
        s3Client = s3Client,
        properties = S3Properties(bucket = "photo-bucket", region = "ap-northeast-2")
    )

    @Test
    fun `deletes the object using the configured bucket and image key`() {
        val request = slot<DeleteObjectRequest>()
        every { s3Client.deleteObject(capture(request)) } returns DeleteObjectResponse.builder().build()

        deleter.delete(IMAGE_URL)

        assertEquals("photo-bucket", request.captured.bucket())
        assertEquals(IMAGE_KEY, request.captured.key())
        verify(exactly = 1) { s3Client.deleteObject(any<DeleteObjectRequest>()) }
    }

    private companion object {
        const val IMAGE_KEY = "photo/7/92f48652-0c77-4fde-bc95-f7e09669b40e"
        const val IMAGE_URL = "https://photo-bucket.s3.ap-northeast-2.amazonaws.com/$IMAGE_KEY"
    }
}
