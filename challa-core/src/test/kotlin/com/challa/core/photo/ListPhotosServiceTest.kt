package com.challa.core.photo

import com.challa.core.room.application.NoMatchingRoomException
import com.challa.core.room.domain.RoomUser
import com.challa.core.room.port.output.RoomUserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class ListPhotosServiceTest {
    private val roomUserRepository = mockk<RoomUserRepository>()
    private val photoRepository = mockk<PhotoRepository>()
    private val service = ListPhotosService(roomUserRepository, photoRepository)

    @Test
    fun `returns all photos after verifying room participation`() {
        val command = ListPhotosCommand(userId = USER_ID, roomId = ROOM_ID)
        every { roomUserRepository.findByUserIdAndRoomId(USER_ID, ROOM_ID) } returns
            RoomUser.createMember(roomId = ROOM_ID, userId = USER_ID)
        every { photoRepository.findAllByRoomId(ROOM_ID) } returns listOf(
            photo(id = 31, imageUrl = "https://bucket/photo-31"),
            photo(id = 32, imageUrl = null)
        )

        val result = service.listPhotos(command)

        assertEquals(
            listOf(
                ListPhotosResult.PhotoProjection(id = 31, imageUrl = "https://bucket/photo-31"),
                ListPhotosResult.PhotoProjection(id = 32, imageUrl = null)
            ),
            result.photoProjections
        )
        verifyOrder {
            roomUserRepository.findByUserIdAndRoomId(USER_ID, ROOM_ID)
            photoRepository.findAllByRoomId(ROOM_ID)
        }
    }

    @Test
    fun `does not retrieve photos when the user is not in the room`() {
        every { roomUserRepository.findByUserIdAndRoomId(USER_ID, ROOM_ID) } returns null

        assertThrows(NoMatchingRoomException::class.java) {
            service.listPhotos(ListPhotosCommand(userId = USER_ID, roomId = ROOM_ID))
        }
        verify(exactly = 0) { photoRepository.findAllByRoomId(any()) }
    }

    private fun photo(id: Long, imageUrl: String?) = Photo(
        id = id,
        roomId = ROOM_ID,
        userId = USER_ID,
        filterId = "filter-original",
        imageUrl = imageUrl
    )

    private companion object {
        const val USER_ID = 7L
        const val ROOM_ID = 11L
    }
}
