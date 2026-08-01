package com.challa.persistence

import com.challa.core.room.domain.RoomStatus
import com.challa.persistence.entity.RoomEntity
import com.challa.persistence.repository.RoomJpaRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager
import org.springframework.context.annotation.Import
import java.time.LocalDateTime

@DataJpaTest
@Import(RoomJpaPersistenceAdaptor::class)
class RoomJpaPersistenceAdaptorTest {
    @Autowired
    private lateinit var adaptor: RoomJpaPersistenceAdaptor

    @Autowired
    private lateinit var repository: RoomJpaRepository

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Test
    fun `updateRoomsStatus persists status changes through dirty checking`() {
        val room = repository.saveAndFlush(
            RoomEntity(
                title = "Trip",
                filmLimit = 24,
                remainingFilmCount = 0,
                invitationCode = "123456",
                roomStatus = RoomStatus.PRINT_PENDING,
                printCompletionAt = LocalDateTime.now(),
                createdAt = LocalDateTime.now()
            )
        )
        entityManager.clear()

        adaptor.updateRoomsStatus(
            roomIds = listOf(requireNotNull(room.id)),
            roomStatus = RoomStatus.PRINT_COMPLETED
        )
        entityManager.flush()
        entityManager.clear()

        val updatedRoom = repository.findById(requireNotNull(room.id)).orElseThrow()
        assertEquals(RoomStatus.PRINT_COMPLETED, updatedRoom.roomStatus)
    }
}
