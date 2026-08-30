package com.challa.core.photo

import com.challa.core.chat.ChatRepository
import com.challa.core.chat.domain.Chat
import com.challa.core.chat.domain.ChatType
import com.challa.core.room.domain.RoomUser
import com.challa.core.room.exception.NoMatchingRoomException
import com.challa.core.room.port.output.RoomUserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class GetPhotoDetailServiceTest {
    private val photoRepository = mockk<PhotoRepository>()
    private val chatRepository = mockk<ChatRepository>()
    private val roomUserRepository = mockk<RoomUserRepository>()
    private val service = GetPhotoDetailService(photoRepository, chatRepository, roomUserRepository)

    @Test
    fun `returns a photo from a room the user participates in with all of its chats`() {
        val command = GetPhotoDetailCommand(userId = USER_ID, roomId = ROOM_ID, photoId = PHOTO_ID)
        val chats = listOf(chat(id = 41, content = "좋아요"), chat(id = 42, content = "멋진 사진"))
        every { roomUserRepository.findByUserIdAndRoomId(USER_ID, ROOM_ID) } returns roomUser()
        every { photoRepository.findById(PHOTO_ID) } returns photo()
        every { chatRepository.findAllByPhotoId(PHOTO_ID) } returns chats

        val result = service.getPhotoDetail(command)

        assertEquals(PhotoDetail(id = PHOTO_ID, chats = chats), result.photoDetail)
        verify { roomUserRepository.findByUserIdAndRoomId(USER_ID, ROOM_ID) }
        verify { photoRepository.findById(PHOTO_ID) }
        verify { chatRepository.findAllByPhotoId(PHOTO_ID) }
    }

    @Test
    fun `does not retrieve a photo when the user does not participate in the room`() {
        every { roomUserRepository.findByUserIdAndRoomId(USER_ID, ROOM_ID) } returns null

        assertThrows(NoMatchingRoomException::class.java) {
            service.getPhotoDetail(GetPhotoDetailCommand(userId = USER_ID, roomId = ROOM_ID, photoId = PHOTO_ID))
        }
        verify(exactly = 0) { photoRepository.findById(any()) }
        verify(exactly = 0) { chatRepository.findAllByPhotoId(any()) }
    }

    @Test
    fun `does not retrieve chats when the photo does not exist`() {
        every { roomUserRepository.findByUserIdAndRoomId(USER_ID, ROOM_ID) } returns roomUser()
        every { photoRepository.findById(PHOTO_ID) } returns null

        assertThrows(PhotoNotFoundException::class.java) {
            service.getPhotoDetail(GetPhotoDetailCommand(userId = USER_ID, roomId = ROOM_ID, photoId = PHOTO_ID))
        }
        verify(exactly = 0) { chatRepository.findAllByPhotoId(any()) }
    }

    @Test
    fun `does not retrieve chats when the photo belongs to another room`() {
        every { roomUserRepository.findByUserIdAndRoomId(USER_ID, ROOM_ID) } returns roomUser()
        every { photoRepository.findById(PHOTO_ID) } returns photo(roomId = OTHER_ROOM_ID)

        assertThrows(PhotoNotFoundException::class.java) {
            service.getPhotoDetail(GetPhotoDetailCommand(userId = USER_ID, roomId = ROOM_ID, photoId = PHOTO_ID))
        }
        verify(exactly = 0) { chatRepository.findAllByPhotoId(any()) }
    }

    private fun roomUser() = RoomUser.createMember(roomId = ROOM_ID, userId = USER_ID)

    private fun photo(roomId: Long = ROOM_ID) = Photo(
        id = PHOTO_ID,
        roomId = roomId,
        userId = USER_ID,
        filterId = "filter-original",
        imageUrl = "https://bucket/photo"
    )

    private fun chat(id: Long, content: String) = Chat(
        id = id,
        type = ChatType.COMMENT,
        content = content,
        photoId = PHOTO_ID,
        roomId = ROOM_ID,
        userId = 8
    )

    private companion object {
        const val USER_ID = 7L
        const val ROOM_ID = 11L
        const val OTHER_ROOM_ID = 12L
        const val PHOTO_ID = 31L
    }
}
