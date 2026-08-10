package com.challa.core.photo

import com.challa.core.auth.Provider
import com.challa.core.room.domain.RoomUser
import com.challa.core.room.exception.NoMatchingRoomException
import com.challa.core.room.port.output.RoomUserRepository
import com.challa.core.user.User
import com.challa.core.user.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.time.LocalDateTime

class ListPhotosServiceTest {
    private val roomUserRepository = mockk<RoomUserRepository>()
    private val photoRepository = mockk<PhotoRepository>()
    private val userRepository = mockk<UserRepository>()
    private val service = ListPhotosService(roomUserRepository, photoRepository, userRepository)

    @Test
    fun `returns a photo slice after verifying room participation`() {
        val command = ListPhotosCommand(userId = USER_ID, roomId = ROOM_ID, page = PAGE, size = SIZE)
        val pageable = PageRequest.of(PAGE, SIZE, Sort.by(Sort.Direction.DESC, "createdAt"))
        every { roomUserRepository.findByUserIdAndRoomId(USER_ID, ROOM_ID) } returns
            RoomUser.createMember(roomId = ROOM_ID, userId = USER_ID)
        every { photoRepository.findSliceByRoomId(ROOM_ID, pageable) } returns PhotoSlice(
            photos = listOf(
                photo(id = 31, imageUrl = "https://bucket/photo-31"),
                photo(id = 32, imageUrl = null)
            ),
            hasNext = true
        )
        every { userRepository.findAllByIds(listOf(USER_ID)) } returns listOf(user())

        val result = service.listPhotos(command)

        assertEquals(
            listOf(
                ListPhotosResult.PhotoProjection(
                    id = 31,
                    imageUrl = "https://bucket/photo-31",
                    userNickname = USER_NICKNAME,
                    userProfileImageUrl = USER_PROFILE_IMAGE_URL,
                    createdAt = CREATED_AT
                ),
                ListPhotosResult.PhotoProjection(
                    id = 32,
                    imageUrl = null,
                    userNickname = USER_NICKNAME,
                    userProfileImageUrl = USER_PROFILE_IMAGE_URL,
                    createdAt = CREATED_AT
                )
            ),
            result.photoProjections
        )
        assertEquals(true, result.hasNext)
        verifyOrder {
            roomUserRepository.findByUserIdAndRoomId(USER_ID, ROOM_ID)
            photoRepository.findSliceByRoomId(ROOM_ID, pageable)
            userRepository.findAllByIds(listOf(USER_ID))
        }
    }

    @Test
    fun `does not retrieve photos when the user is not in the room`() {
        every { roomUserRepository.findByUserIdAndRoomId(USER_ID, ROOM_ID) } returns null

        assertThrows(NoMatchingRoomException::class.java) {
            service.listPhotos(ListPhotosCommand(userId = USER_ID, roomId = ROOM_ID, page = PAGE, size = SIZE))
        }
        verify(exactly = 0) { photoRepository.findSliceByRoomId(any(), any()) }
        verify(exactly = 0) { userRepository.findAllByIds(any()) }
    }

    private fun photo(id: Long, imageUrl: String?) = Photo(
        id = id,
        roomId = ROOM_ID,
        userId = USER_ID,
        filterId = "filter-original",
        imageUrl = imageUrl,
        createdAt = CREATED_AT
    )

    private fun user() = User(
        id = USER_ID,
        provider = Provider.KAKAO,
        providerId = "provider-id",
        nickname = USER_NICKNAME,
        profileImageUrl = USER_PROFILE_IMAGE_URL
    )

    private companion object {
        const val USER_ID = 7L
        const val ROOM_ID = 11L
        const val PAGE = 0
        const val SIZE = 24
        const val USER_NICKNAME = "nickname"
        const val USER_PROFILE_IMAGE_URL = "https://bucket/profile-7"
        val CREATED_AT: LocalDateTime = LocalDateTime.of(2026, 8, 11, 12, 0)
    }
}
