package com.challa.core.photo

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class CompletePhotoServiceTest {
    private val photoRepository = mockk<PhotoRepository>()
    private val service = CompletePhotoService(photoRepository)

    @Test
    fun `updates the image URL of the user's photo`() {
        val command = CompletePhotoCommand(USER_ID, PHOTO_ID, IMAGE_URL)
        every { photoRepository.updateImageUrl(PHOTO_ID, USER_ID, IMAGE_URL) } returns photo()

        val result = service.complete(command)

        assertEquals(IMAGE_URL, result.photo.imageUrl)
        verify { photoRepository.updateImageUrl(PHOTO_ID, USER_ID, IMAGE_URL) }
    }

    @Test
    fun `throws when the photo does not belong to the user`() {
        val command = CompletePhotoCommand(USER_ID, PHOTO_ID, IMAGE_URL)
        every { photoRepository.updateImageUrl(PHOTO_ID, USER_ID, IMAGE_URL) } returns null

        assertThrows(PhotoNotFoundException::class.java) {
            service.complete(command)
        }
    }

    private fun photo() = Photo(
        id = PHOTO_ID,
        roomId = 11,
        userId = USER_ID,
        imageUrl = IMAGE_URL,
        filterId = "filter-original"
    )

    private companion object {
        const val USER_ID = 7L
        const val PHOTO_ID = 31L
        const val IMAGE_URL = "https://bucket/photo"
    }
}
