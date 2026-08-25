package com.challa.persistence

import com.challa.core.room.domain.RoomStatus
import com.challa.core.room.port.output.RoomMemberCount
import com.challa.persistence.entity.RoomEntity
import com.challa.persistence.entity.RoomUserEntity
import com.challa.persistence.repository.RoomJpaRepository
import com.challa.persistence.repository.RoomUserJpaRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.context.annotation.Import
import java.time.LocalDateTime

@DataJpaTest
@Import(RoomUserPersistenceAdaptor::class)
class RoomUserPersistenceAdaptorTest {
    @Autowired
    private lateinit var adaptor: RoomUserPersistenceAdaptor

    @Autowired
    private lateinit var repository: RoomUserJpaRepository

    @Autowired
    private lateinit var roomRepository: RoomJpaRepository

    @Test
    fun `counts members for all requested room IDs in one aggregation`() {
        val createdAt = LocalDateTime.now()
        repository.saveAllAndFlush(
            listOf(
                RoomUserEntity(roomId = 1L, userId = 11L, createdAt = createdAt),
                RoomUserEntity(roomId = 1L, userId = 12L, createdAt = createdAt),
                RoomUserEntity(roomId = 2L, userId = 21L, createdAt = createdAt),
                RoomUserEntity(roomId = 3L, userId = 31L, createdAt = createdAt)
            )
        )

        val result = adaptor.countMembersByRoomIds(listOf(1L, 2L))

        assertEquals(
            setOf(
                RoomMemberCount(roomId = 1L, memberCount = 2L),
                RoomMemberCount(roomId = 2L, memberCount = 1L)
            ),
            result.toSet()
        )
    }

    @Test
    fun `returns an empty list when no room IDs are requested`() {
        assertEquals(emptyList<RoomMemberCount>(), adaptor.countMembersByRoomIds(emptyList()))
    }

    @Test
    fun `findAllByRoomId returns only users in the requested room`() {
        val createdAt = LocalDateTime.now()
        repository.saveAllAndFlush(
            listOf(
                RoomUserEntity(roomId = 1L, userId = 11L, createdAt = createdAt),
                RoomUserEntity(roomId = 1L, userId = 12L, createdAt = createdAt.plusSeconds(1)),
                RoomUserEntity(roomId = 2L, userId = 21L, createdAt = createdAt)
            )
        )

        val result = adaptor.findAllByRoomId(1L)

        assertEquals(setOf(11L, 12L), result.map { it.userId }.toSet())
        assertEquals(setOf(1L), result.map { it.roomId }.toSet())
    }

    @Test
    fun `findByUserIdAndRoomId excludes membership in a soft deleted room`() {
        val room = roomRepository.saveAndFlush(roomEntity(invitationCode = "123456"))
        val roomId = requireNotNull(room.id)
        repository.saveAndFlush(
            RoomUserEntity(
                roomId = roomId,
                userId = 11L,
                createdAt = LocalDateTime.of(2026, 8, 1, 12, 0)
            )
        )

        assertNotNull(adaptor.findByUserIdAndRoomId(userId = 11L, roomId = roomId))

        roomRepository.softDelete(roomId = roomId, deletedAt = LocalDateTime.of(2026, 8, 26, 12, 0))

        assertNull(adaptor.findByUserIdAndRoomId(userId = 11L, roomId = roomId))
    }

    @Test
    fun `deleteAllByRoomId deletes only users in the requested room`() {
        val createdAt = LocalDateTime.of(2026, 8, 1, 12, 0)
        repository.saveAllAndFlush(
            listOf(
                RoomUserEntity(roomId = 1L, userId = 11L, createdAt = createdAt),
                RoomUserEntity(roomId = 1L, userId = 12L, createdAt = createdAt),
                RoomUserEntity(roomId = 2L, userId = 21L, createdAt = createdAt)
            )
        )

        adaptor.deleteAllByRoomId(1L)

        assertEquals(emptyList<RoomUserEntity>(), repository.findAllByRoomId(1L))
        assertEquals(listOf(21L), repository.findAllByRoomId(2L).map { it.userId })
    }

    private fun roomEntity(invitationCode: String) = RoomEntity(
        title = "Trip",
        totalPhotoCount = 24,
        remainedPhotoCount = 24,
        invitationCode = invitationCode,
        roomStatus = RoomStatus.SHOOTING,
        coverImageUrl = null,
        coverStickerId = 1L,
        coverStickerColorId = 2L,
        photoPrintCompletedAt = null,
        createdAt = LocalDateTime.of(2026, 8, 1, 12, 0),
        expiresAt = LocalDateTime.of(2026, 8, 31, 12, 0)
    )
}
