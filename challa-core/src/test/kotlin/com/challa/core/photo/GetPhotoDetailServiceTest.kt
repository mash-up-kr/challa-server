package com.challa.core.photo

import com.challa.core.chat.ChatRepository
import com.challa.core.chat.domain.Chat
import com.challa.core.chat.domain.ChatType
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class GetPhotoDetailServiceTest {
    private val photoRepository = mockk<PhotoRepository>()
    private val chatRepository = mockk<ChatRepository>()
    private val service = GetPhotoDetailService(photoRepository, chatRepository)

    @Test
    fun `returns an owned photo with all of its chats`() {
        val command = GetPhotoDetailCommand(userId = USER_ID, photoId = PHOTO_ID)
        val chats = listOf(chat(id = 41, content = "좋아요"), chat(id = 42, content = "멋진 사진"))
        every { photoRepository.findByIdAndUserId(PHOTO_ID, USER_ID) } returns photo()
        every { chatRepository.findAllByPhotoId(PHOTO_ID) } returns chats

        val result = service.getPhotoDetail(command)

        assertEquals(PhotoDetail(id = PHOTO_ID, chats = chats), result.photoDetail)
        verify { photoRepository.findByIdAndUserId(PHOTO_ID, USER_ID) }
        verify { chatRepository.findAllByPhotoId(PHOTO_ID) }
    }

    @Test
    fun `does not retrieve chats when the photo does not belong to the user`() {
        every { photoRepository.findByIdAndUserId(PHOTO_ID, USER_ID) } returns null

        assertThrows(PhotoNotFoundException::class.java) {
            service.getPhotoDetail(GetPhotoDetailCommand(userId = USER_ID, photoId = PHOTO_ID))
        }
        verify(exactly = 0) { chatRepository.findAllByPhotoId(any()) }
    }

    private fun photo() = Photo(
        id = PHOTO_ID,
        roomId = 11,
        userId = USER_ID,
        filterId = "filter-original",
        imageUrl = "https://bucket/photo"
    )

    private fun chat(id: Long, content: String) = Chat(
        id = id,
        type = ChatType.COMMENT,
        content = content,
        photoId = PHOTO_ID,
        roomId = 11,
        userId = 8
    )

    private companion object {
        const val USER_ID = 7L
        const val PHOTO_ID = 31L
    }
}
