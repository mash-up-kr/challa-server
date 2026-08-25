package com.challa.core.room

import com.challa.core.room.application.DeleteRoomService
import com.challa.core.room.domain.RoomUser
import com.challa.core.room.exception.NoMatchingRoomException
import com.challa.core.room.port.input.DeleteRoomCommand
import com.challa.core.room.port.output.RoomRepository
import com.challa.core.room.port.output.RoomUserRepository
import io.mockk.*
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class DeleteRoomServiceTest {
    private val roomRepository = mockk<RoomRepository>()
    private val roomUserRepository = mockk<RoomUserRepository>()
    private val service = DeleteRoomService(roomRepository, roomUserRepository)

    @Test
    fun `soft deletes the room and deletes all room users when the requester is a member`() {
        val requestedAt = LocalDateTime.now()
        val deletedAt = slot<LocalDateTime>()
        every { roomUserRepository.findByUserIdAndRoomId(USER_ID, ROOM_ID) } returns roomUser()
        every { roomRepository.softDelete(ROOM_ID, capture(deletedAt)) } returns true
        every { roomUserRepository.deleteAllByRoomId(ROOM_ID) } returns Unit

        service.deleteRoom(command())

        assertTrue(!deletedAt.captured.isBefore(requestedAt))
        assertTrue(!deletedAt.captured.isAfter(LocalDateTime.now()))
        verifyOrder {
            roomUserRepository.findByUserIdAndRoomId(USER_ID, ROOM_ID)
            roomRepository.softDelete(ROOM_ID, deletedAt.captured)
            roomUserRepository.deleteAllByRoomId(ROOM_ID)
        }
    }

    @Test
    fun `does not delete the room when the requester is not a member`() {
        every { roomUserRepository.findByUserIdAndRoomId(USER_ID, ROOM_ID) } returns null

        assertThrows(NoMatchingRoomException::class.java) {
            service.deleteRoom(command())
        }

        verify(exactly = 0) { roomRepository.softDelete(any(), any()) }
        verify(exactly = 0) { roomUserRepository.deleteAllByRoomId(any()) }
    }

    @Test
    fun `does not delete room users when the room soft delete fails`() {
        every { roomUserRepository.findByUserIdAndRoomId(USER_ID, ROOM_ID) } returns roomUser()
        every { roomRepository.softDelete(ROOM_ID, any()) } returns false

        assertThrows(NoMatchingRoomException::class.java) {
            service.deleteRoom(command())
        }

        verify(exactly = 0) { roomUserRepository.deleteAllByRoomId(any()) }
    }

    private fun command() = DeleteRoomCommand(userId = USER_ID, roomId = ROOM_ID)

    private fun roomUser() = RoomUser(
        id = 1L,
        roomId = ROOM_ID,
        userId = USER_ID,
        createdAt = LocalDateTime.of(2026, 8, 1, 12, 0)
    )

    private companion object {
        const val USER_ID = 7L
        const val ROOM_ID = 11L
    }
}
