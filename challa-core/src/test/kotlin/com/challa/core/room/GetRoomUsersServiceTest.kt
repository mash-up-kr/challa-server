package com.challa.core.room

import com.challa.core.auth.Provider
import com.challa.core.room.application.GetRoomUsersService
import com.challa.core.room.domain.RoomUser
import com.challa.core.room.exception.NoMatchingRoomException
import com.challa.core.room.port.input.GetRoomUsersCommand
import com.challa.core.room.port.input.GetRoomUsersResult
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

class GetRoomUsersServiceTest {
    private val roomUserRepository = mockk<RoomUserRepository>()
    private val userRepository = mockk<UserRepository>()
    private val service = GetRoomUsersService(roomUserRepository, userRepository)

    @Test
    fun `returns the other room users after verifying room participation`() {
        val command = GetRoomUsersCommand(userId = REQUEST_USER_ID, roomId = ROOM_ID)
        every { roomUserRepository.findByUserIdAndRoomId(REQUEST_USER_ID, ROOM_ID) } returns
            RoomUser.createMember(roomId = ROOM_ID, userId = REQUEST_USER_ID)
        every { roomUserRepository.findAllByRoomId(ROOM_ID) } returns listOf(
            RoomUser.createMember(roomId = ROOM_ID, userId = REQUEST_USER_ID),
            RoomUser.createMember(roomId = ROOM_ID, userId = 8L),
            RoomUser.createMember(roomId = ROOM_ID, userId = 9L)
        )
        every { userRepository.findAllByIds(listOf(REQUEST_USER_ID, 8L, 9L)) } returns listOf(
            user(id = REQUEST_USER_ID, nickname = "요청자", profileImageUrl = null),
            user(id = 8L, nickname = "라이언", profileImageUrl = "https://img.example/ryan.png"),
            user(id = 9L, nickname = null, profileImageUrl = null)
        )

        val result = service.getRoomUsers(command)

        assertEquals(
            listOf(
                GetRoomUsersResult.UserProjection(id = REQUEST_USER_ID, nickname = "요청자", profileImageUrl = null),
                GetRoomUsersResult.UserProjection(
                    id = 8L,
                    nickname = "라이언",
                    profileImageUrl = "https://img.example/ryan.png"
                ),
                GetRoomUsersResult.UserProjection(id = 9L, nickname = null, profileImageUrl = null)
            ),
            result.userProjections
        )
        verifyOrder {
            roomUserRepository.findByUserIdAndRoomId(REQUEST_USER_ID, ROOM_ID)
            roomUserRepository.findAllByRoomId(ROOM_ID)
            userRepository.findAllByIds(listOf(REQUEST_USER_ID, 8L, 9L))
        }
    }

    @Test
    fun `does not retrieve room users when the requester is not in the room`() {
        every { roomUserRepository.findByUserIdAndRoomId(REQUEST_USER_ID, ROOM_ID) } returns null

        assertThrows(NoMatchingRoomException::class.java) {
            service.getRoomUsers(GetRoomUsersCommand(userId = REQUEST_USER_ID, roomId = ROOM_ID))
        }

        verify(exactly = 0) { roomUserRepository.findAllByRoomId(any()) }
        verify(exactly = 0) { userRepository.findAllByIds(any()) }
    }

    private fun user(id: Long, nickname: String?, profileImageUrl: String?) = User(
        id = id,
        provider = Provider.KAKAO,
        providerId = "provider-$id",
        nickname = nickname,
        profileImageUrl = profileImageUrl
    )

    private companion object {
        const val REQUEST_USER_ID = 7L
        const val ROOM_ID = 11L
    }
}
