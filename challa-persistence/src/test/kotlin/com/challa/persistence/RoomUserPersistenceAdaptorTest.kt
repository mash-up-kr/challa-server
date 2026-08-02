package com.challa.persistence

import com.challa.core.room.port.output.RoomMemberCount
import com.challa.persistence.entity.RoomUserEntity
import com.challa.persistence.repository.RoomUserJpaRepository
import org.junit.jupiter.api.Assertions.assertEquals
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
}
