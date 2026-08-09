package com.challa.core.photo

import com.challa.core.room.domain.Room
import com.challa.core.room.domain.RoomStatus
import com.challa.core.room.domain.RoomUser
import com.challa.core.room.exception.NoMatchingRoomException
import com.challa.core.room.port.output.RoomRepository
import com.challa.core.room.port.output.RoomUserRepository
import com.challa.core.upload.UploadedObjectDeleter
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class CompletePhotoServiceTest {
    private val roomUserRepository = mockk<RoomUserRepository>()
    private val roomRepository = mockk<RoomRepository>()
    private val photoRepository = mockk<PhotoRepository>()
    private val uploadedObjectDeleter = mockk<UploadedObjectDeleter>()
    private lateinit var service: CompletePhotoService

    @BeforeEach
    fun setUp() {
        service = CompletePhotoService(
            roomUserRepository = roomUserRepository,
            roomRepository = roomRepository,
            photoRepository = photoRepository,
            uploadedObjectDeleter = uploadedObjectDeleter
        )
    }

    @Test
    fun `decrements the count and saves the completed photo`() {
        every { roomUserRepository.findByUserIdAndRoomId(USER_ID, ROOM_ID) } returns roomUser()
        every { roomRepository.findByRoomId(ROOM_ID) } returnsMany listOf(
            room(remainedPhotoCount = 2),
            room(remainedPhotoCount = 1)
        )
        every { roomRepository.decrementRemainedPhotoCount(ROOM_ID) } returns true
        every { photoRepository.save(any()) } answers { firstArg<Photo>().copy(id = PHOTO_ID) }

        val result = service.complete(command())

        assertEquals(1, result.remainedPhotoCount)
        verify(exactly = 0) { uploadedObjectDeleter.delete(any()) }
        verify(exactly = 0) { roomRepository.markPhotoPrintPending(any(), any()) }
        verifyOrder {
            roomUserRepository.findByUserIdAndRoomId(USER_ID, ROOM_ID)
            roomRepository.findByRoomId(ROOM_ID)
            roomRepository.decrementRemainedPhotoCount(ROOM_ID)
            roomRepository.findByRoomId(ROOM_ID)
            photoRepository.save(
                match {
                    it.roomId == ROOM_ID &&
                        it.userId == USER_ID &&
                        it.filterId == CAMERA_FILTER_NAME &&
                        it.imageUrl == IMAGE_URL
                }
            )
        }
    }

    @Test
    fun `marks photo printing pending when the last photo is completed`() {
        val requestedAt = LocalDateTime.now()
        val completionAt = slot<LocalDateTime>()
        every { roomUserRepository.findByUserIdAndRoomId(USER_ID, ROOM_ID) } returns roomUser()
        every { roomRepository.findByRoomId(ROOM_ID) } returnsMany listOf(
            room(remainedPhotoCount = 1),
            room(remainedPhotoCount = 0)
        )
        every { roomRepository.decrementRemainedPhotoCount(ROOM_ID) } returns true
        every { roomRepository.markPhotoPrintPending(ROOM_ID, capture(completionAt)) } returns Unit
        every { photoRepository.save(any()) } answers { firstArg<Photo>().copy(id = PHOTO_ID) }

        val result = service.complete(command())

        assertEquals(0, result.remainedPhotoCount)
        assertTrue(!completionAt.captured.isBefore(requestedAt.plusHours(24)))
        assertTrue(!completionAt.captured.isAfter(LocalDateTime.now().plusHours(24)))
        verify { roomRepository.markPhotoPrintPending(ROOM_ID, any()) }
    }

    @Test
    fun `deletes the uploaded object when the conditional decrement fails`() {
        every { roomUserRepository.findByUserIdAndRoomId(USER_ID, ROOM_ID) } returns roomUser()
        every { roomRepository.findByRoomId(ROOM_ID) } returns room(remainedPhotoCount = 0)
        every { roomRepository.decrementRemainedPhotoCount(ROOM_ID) } returns false
        every { uploadedObjectDeleter.delete(IMAGE_URL) } returns Unit

        assertThrows(NoRemainedPhotoException::class.java) {
            service.complete(command())
        }

        verifyOrder {
            roomRepository.decrementRemainedPhotoCount(ROOM_ID)
            uploadedObjectDeleter.delete(IMAGE_URL)
        }
        verify(exactly = 0) { photoRepository.save(any()) }
        verify(exactly = 0) { roomRepository.markPhotoPrintPending(any(), any()) }
    }

    @Test
    fun `does not consume a photo when the user is not a room member`() {
        every { roomUserRepository.findByUserIdAndRoomId(USER_ID, ROOM_ID) } returns null

        assertThrows(NoMatchingRoomException::class.java) {
            service.complete(command())
        }

        verify(exactly = 0) { roomRepository.decrementRemainedPhotoCount(any()) }
        verify(exactly = 0) { uploadedObjectDeleter.delete(any()) }
        verify(exactly = 0) { photoRepository.save(any()) }
    }

    private fun command() = CompletePhotoCommand(
        userId = USER_ID,
        roomId = ROOM_ID,
        cameraFilterName = CAMERA_FILTER_NAME,
        imageUrl = IMAGE_URL
    )

    private fun room(remainedPhotoCount: Long) = Room(
        id = ROOM_ID,
        title = "Trip",
        totalPhotoCount = 24,
        remainedPhotoCount = remainedPhotoCount,
        invitationCode = "123456",
        roomStatus = RoomStatus.SHOOTING,
        photoPrintCompletedAt = null,
        createdAt = LocalDateTime.of(2026, 8, 1, 12, 0),
        expiresAt = LocalDateTime.of(2026, 8, 31, 12, 0)
    )

    private fun roomUser() = RoomUser(
        id = 1,
        roomId = ROOM_ID,
        userId = USER_ID,
        createdAt = LocalDateTime.of(2026, 8, 1, 12, 0)
    )

    private companion object {
        const val USER_ID = 7L
        const val ROOM_ID = 11L
        const val PHOTO_ID = 31L
        const val CAMERA_FILTER_NAME = "filter-original"
        const val IMAGE_URL = "https://bucket/photo/7/92f48652-0c77-4fde-bc95-f7e09669b40e"
    }
}
