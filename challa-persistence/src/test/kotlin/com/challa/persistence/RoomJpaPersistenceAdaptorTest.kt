package com.challa.persistence

import com.challa.core.room.domain.RoomStatus
import com.challa.persistence.entity.RoomEntity
import com.challa.persistence.repository.RoomJpaRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.within
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager
import org.springframework.context.annotation.Import
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@DataJpaTest
@Import(RoomJpaPersistenceAdaptor::class)
class RoomJpaPersistenceAdaptorTest {
    @Autowired
    private lateinit var adaptor: RoomJpaPersistenceAdaptor

    @Autowired
    private lateinit var repository: RoomJpaRepository

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var transactionManager: PlatformTransactionManager

    @Test
    fun `updateRoomsStatus persists status changes through dirty checking`() {
        val room = repository.saveAndFlush(
            RoomEntity(
                title = "Trip",
                totalPhotoCount = 24,
                remainedPhotoCount = 0,
                invitationCode = "123456",
                roomStatus = RoomStatus.PHOTO_PRINT_PENDING,
                coverImageUrl = "https://bucket/photo/11/7/first",
                coverStickerId = 1L,
                coverStickerColorId = 2L,
                photoPrintCompletedAt = LocalDateTime.now(),
                createdAt = LocalDateTime.now(),
                expiresAt = LocalDateTime.now().plus(30, ChronoUnit.DAYS)
            )
        )
        entityManager.clear()

        adaptor.updateRoomsStatus(
            roomIds = listOf(requireNotNull(room.id)),
            roomStatus = RoomStatus.PHOTO_PRINT_COMPLETED
        )
        entityManager.flush()
        entityManager.clear()

        val updatedRoom = repository.findById(requireNotNull(room.id)).orElseThrow()
        assertEquals(RoomStatus.PHOTO_PRINT_COMPLETED, updatedRoom.roomStatus)
    }

    @Test
    fun `decrementRemainedPhotoCount only decrements the count`() {
        val room = repository.saveAndFlush(
            RoomEntity(
                title = "Trip",
                totalPhotoCount = 24,
                remainedPhotoCount = 1,
                invitationCode = "654321",
                roomStatus = RoomStatus.SHOOTING,
                photoPrintCompletedAt = null,
                coverImageUrl = "https://bucket/photo/11/7/first",
                coverStickerId = 1L,
                coverStickerColorId = 2L,
                createdAt = LocalDateTime.now(),
                expiresAt = LocalDateTime.now().plus(30, ChronoUnit.DAYS)
            )
        )
        entityManager.clear()

        assertEquals(true, adaptor.decrementRemainedPhotoCount(requireNotNull(room.id)))
        entityManager.flush()
        entityManager.clear()

        val updatedRoom = repository.findById(requireNotNull(room.id)).orElseThrow()
        assertEquals(0, updatedRoom.remainedPhotoCount)
        assertEquals(RoomStatus.SHOOTING, updatedRoom.roomStatus)
    }

    @Test
    fun `markPhotoPrintPending persists status and completion time through dirty checking`() {
        val photoPrintCompletedAt = LocalDateTime.now().plusHours(24)
        val entity = repository.saveAndFlush(
            RoomEntity(
                title = "Trip",
                totalPhotoCount = 1,
                remainedPhotoCount = 1,
                invitationCode = "112233",
                roomStatus = RoomStatus.SHOOTING,
                coverImageUrl = "https://bucket/photo/11/7/first",
                coverStickerId = 1L,
                coverStickerColorId = 2L,
                photoPrintCompletedAt = null,
                createdAt = LocalDateTime.now(),
                expiresAt = LocalDateTime.now().plus(30, ChronoUnit.DAYS)
            )
        )
        entityManager.clear()
        adaptor.markPhotoPrintPending(
            roomId = requireNotNull(entity.id),
            photoPrintCompletedAt = photoPrintCompletedAt
        )
        entityManager.flush()
        entityManager.clear()

        val updatedRoom = repository.findById(requireNotNull(entity.id)).get()
        assertEquals(RoomStatus.PHOTO_PRINT_PENDING, updatedRoom.roomStatus)
        assertThat(updatedRoom.photoPrintCompletedAt).isCloseTo(photoPrintCompletedAt, within(1, ChronoUnit.MILLIS))
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    fun `the conditional update prevents concurrent requests from consuming the same last photo`() {
        val room = repository.saveAndFlush(
            RoomEntity(
                title = "Concurrent",
                totalPhotoCount = 1,
                remainedPhotoCount = 1,
                invitationCode = "777777",
                roomStatus = RoomStatus.SHOOTING,
                photoPrintCompletedAt = null,
                coverImageUrl = "https://bucket/photo/11/7/first",
                coverStickerId = 1L,
                coverStickerColorId = 2L,
                createdAt = LocalDateTime.now(),
                expiresAt = LocalDateTime.now().plus(30, ChronoUnit.DAYS)
            )
        )
        val roomId = requireNotNull(room.id)
        val start = CountDownLatch(1)
        val executor = Executors.newFixedThreadPool(2)

        try {
            val attempts = List(2) {
                executor.submit<Boolean> {
                    start.await()
                    TransactionTemplate(transactionManager).execute {
                        val currentRoom = adaptor.findByRoomId(roomId) ?: return@execute false
                        if (currentRoom.remainedPhotoCount <= 0) {
                            false
                        } else {
                            adaptor.decrementRemainedPhotoCount(roomId)
                        }
                    } ?: false
                }
            }
            start.countDown()

            assertEquals(1, attempts.count { it.get() })
            assertEquals(0, repository.findById(roomId).orElseThrow().remainedPhotoCount)
        } finally {
            executor.shutdownNow()
        }
    }

    @Test
    fun `soft deleted room remains stored but is excluded from active room queries`() {
        val deletedRoom = repository.saveAndFlush(roomEntity(invitationCode = "445566", title = "Deleted"))
        val activeRoom = repository.saveAndFlush(roomEntity(invitationCode = "778899", title = "Active"))
        val deletedRoomId = requireNotNull(deletedRoom.id)
        val activeRoomId = requireNotNull(activeRoom.id)
        val deletedAt = LocalDateTime.of(2026, 8, 26, 12, 0)
        entityManager.clear()

        assertEquals(true, adaptor.softDelete(roomId = deletedRoomId, deletedAt = deletedAt))
        entityManager.flush()
        entityManager.clear()

        val storedRoom = repository.findById(deletedRoomId).orElseThrow()
        assertEquals(deletedAt, storedRoom.deletedAt)
        assertNull(adaptor.findByRoomId(deletedRoomId))
        assertNull(adaptor.findByInvitationCode("445566"))
        assertEquals(listOf(activeRoomId), adaptor.findAllById(listOf(deletedRoomId, activeRoomId)).map { it.id })
        assertFalse(adaptor.softDelete(roomId = deletedRoomId, deletedAt = deletedAt.plusSeconds(1)))
    }

    @Test
    fun `room updates ignore soft deleted rooms`() {
        val room = repository.saveAndFlush(roomEntity(invitationCode = "334455", title = "Original"))
        val roomId = requireNotNull(room.id)
        val originalPhotoPrintCompletedAt = room.photoPrintCompletedAt
        entityManager.clear()
        adaptor.softDelete(roomId = roomId, deletedAt = LocalDateTime.of(2026, 8, 26, 12, 0))

        adaptor.updateTitle(roomId = roomId, title = "Updated")
        adaptor.updateCover(
            roomId = roomId,
            coverImageUrl = "https://bucket/updated",
            coverStickerId = 3L,
            coverStickerColorId = 4L
        )
        adaptor.updateRoomsStatus(listOf(roomId), RoomStatus.PHOTO_PRINT_COMPLETED)
        adaptor.markPhotoPrintPending(roomId, LocalDateTime.of(2026, 8, 27, 12, 0))
        entityManager.flush()
        entityManager.clear()

        val storedRoom = repository.findById(roomId).orElseThrow()
        assertEquals("Original", storedRoom.title)
        assertEquals("https://bucket/photo/11/7/first", storedRoom.coverImageUrl)
        assertEquals(1L, storedRoom.coverStickerId)
        assertEquals(2L, storedRoom.coverStickerColorId)
        assertEquals(RoomStatus.SHOOTING, storedRoom.roomStatus)
        assertEquals(originalPhotoPrintCompletedAt, storedRoom.photoPrintCompletedAt)
    }

    private fun roomEntity(invitationCode: String, title: String) = RoomEntity(
        title = title,
        totalPhotoCount = 24,
        remainedPhotoCount = 24,
        invitationCode = invitationCode,
        roomStatus = RoomStatus.SHOOTING,
        coverImageUrl = "https://bucket/photo/11/7/first",
        coverStickerId = 1L,
        coverStickerColorId = 2L,
        photoPrintCompletedAt = null,
        createdAt = LocalDateTime.of(2026, 8, 1, 12, 0),
        expiresAt = LocalDateTime.of(2026, 8, 31, 12, 0)
    )
}
