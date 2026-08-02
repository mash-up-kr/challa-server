package com.challa.core.upload

import com.challa.core.photo.Photo
import com.challa.core.photo.PhotoRepository
import com.challa.core.room.domain.Room
import com.challa.core.room.domain.RoomStatus
import com.challa.core.room.domain.RoomUser
import com.challa.core.room.port.output.RoomRepository
import com.challa.core.room.port.output.RoomUserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class PhotoUploadServiceTest {
    private val roomUserRepository = mockk<RoomUserRepository>()
    private val roomRepository = mockk<RoomRepository>()
    private val presignedUploadUrlIssuer = mockk<PresignedUploadUrlIssuer>()
    private val photoRepository = mockk<PhotoRepository>()
    private lateinit var service: PhotoUploadService

    @BeforeEach
    fun setUp() {
        service = PhotoUploadService(
            roomUserRepository = roomUserRepository,
            roomRepository = roomRepository,
            presignedUploadUrlIssuer = presignedUploadUrlIssuer,
            photoRepository = photoRepository
        )
    }

    @Test
    fun `decrements the count and creates an empty photo before issuing a URL`() {
        every { roomUserRepository.findByUserIdAndRoomId(USER_ID, ROOM_ID) } returns roomUser()
        every { roomRepository.findByRoomId(ROOM_ID) } returnsMany listOf(
            room(remainedPhotoCount = 2),
            room(remainedPhotoCount = 1)
        )
        every { roomRepository.decrementRemainedPhotoCount(ROOM_ID) } returns true
        every { photoRepository.save(any()) } answers { firstArg<Photo>().copy(id = PHOTO_ID) }
        every { presignedUploadUrlIssuer.issue(any(), "image/jpeg") } returns uploadUrl()

        val result = service.issue(command())

        assertEquals(1, result.remainedPhotoCount)
        assertEquals(PHOTO_ID, result.photoId)
        assertEquals("https://bucket/photo?signature", result.uploadUrl)
        assertEquals("https://bucket/photo", result.imageUrl)
        verify(exactly = 0) { roomRepository.markPhotoPrintPending(any(), any()) }
        verifyOrder {
            roomRepository.decrementRemainedPhotoCount(ROOM_ID)
            roomRepository.findByRoomId(ROOM_ID)
            photoRepository.save(match { it.imageUrl == null })
            presignedUploadUrlIssuer.issue(match { it.startsWith("photo/$USER_ID/") }, "image/jpeg")
        }
    }

    @Test
    fun `changes the room status explicitly when the refreshed count reaches zero`() {
        val requestedAt = LocalDateTime.now()
        val photoPrintCompletionAt = slot<LocalDateTime>()
        every { roomUserRepository.findByUserIdAndRoomId(USER_ID, ROOM_ID) } returns roomUser()
        every { roomRepository.findByRoomId(ROOM_ID) } returnsMany listOf(
            room(remainedPhotoCount = 1),
            room(remainedPhotoCount = 0)
        )
        every { roomRepository.decrementRemainedPhotoCount(ROOM_ID) } returns true
        every {
            roomRepository.markPhotoPrintPending(
                ROOM_ID,
                capture(photoPrintCompletionAt)
            )
        } returns Unit
        every { photoRepository.save(any()) } answers { firstArg<Photo>().copy(id = PHOTO_ID) }
        every { presignedUploadUrlIssuer.issue(any(), "image/jpeg") } returns uploadUrl()

        val result = service.issue(command())

        assertEquals(0, result.remainedPhotoCount)
        assertTrue(!photoPrintCompletionAt.captured.isBefore(requestedAt.plusHours(24)))
        assertTrue(!photoPrintCompletionAt.captured.isAfter(LocalDateTime.now().plusHours(24)))
        verifyOrder {
            roomRepository.decrementRemainedPhotoCount(ROOM_ID)
            roomRepository.findByRoomId(ROOM_ID)
            roomRepository.markPhotoPrintPending(ROOM_ID, any())
            photoRepository.save(any())
            presignedUploadUrlIssuer.issue(any(), "image/jpeg")
        }
    }

    @Test
    fun `does not issue a URL when the room has no remaining photos`() {
        every { roomUserRepository.findByUserIdAndRoomId(USER_ID, ROOM_ID) } returns roomUser()
        every { roomRepository.findByRoomId(ROOM_ID) } returns room(remainedPhotoCount = 0)

        assertThrows(NoRemainedPhotoException::class.java) {
            service.issue(command())
        }

        verify(exactly = 0) { roomRepository.decrementRemainedPhotoCount(any()) }
        verify(exactly = 0) { presignedUploadUrlIssuer.issue(any(), any()) }
        verify(exactly = 0) { photoRepository.save(any()) }
    }

    @Test
    fun `does not issue a URL when another request consumes the last photo first`() {
        every { roomUserRepository.findByUserIdAndRoomId(USER_ID, ROOM_ID) } returns roomUser()
        every { roomRepository.findByRoomId(ROOM_ID) } returns room(remainedPhotoCount = 1)
        every { roomRepository.decrementRemainedPhotoCount(ROOM_ID) } returns false

        assertThrows(NoRemainedPhotoException::class.java) {
            service.issue(command())
        }

        verify(exactly = 0) { presignedUploadUrlIssuer.issue(any(), any()) }
        verify(exactly = 0) { photoRepository.save(any()) }
    }

    private fun command() = IssuePhotoUploadCommand(
        userId = USER_ID,
        roomId = ROOM_ID,
        cameraFilterId = "filter-original",
        contentType = "image/jpeg"
    )

    private fun room(remainedPhotoCount: Long) = Room(
        id = ROOM_ID,
        title = "Trip",
        totalPhotoCount = 24,
        remainedPhotoCount = remainedPhotoCount,
        invitationCode = "123456",
        roomStatus = RoomStatus.SHOOTING,
        photoPrintCompletionAt = null,
        createdAt = LocalDateTime.of(2026, 8, 1, 12, 0)
    )

    private fun roomUser() = RoomUser(
        id = 1,
        roomId = ROOM_ID,
        userId = USER_ID,
        createdAt = LocalDateTime.of(2026, 8, 1, 12, 0)
    )

    private fun uploadUrl() = UploadUrl(
        uploadUrl = "https://bucket/photo?signature",
        imageUrl = "https://bucket/photo",
        expiresInSeconds = 300
    )

    private companion object {
        const val USER_ID = 7L
        const val ROOM_ID = 11L
        const val PHOTO_ID = 31L
    }
}
