package com.challa.persistence

import com.challa.core.room.application.DeleteRoomService
import com.challa.core.room.domain.RoomStatus
import com.challa.core.room.port.input.DeleteRoomCommand
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
@Import(DeleteRoomService::class, RoomJpaPersistenceAdaptor::class, RoomUserPersistenceAdaptor::class)
class DeleteRoomPersistenceIntegrationTest {
    @Autowired
    private lateinit var service: DeleteRoomService

    @Autowired
    private lateinit var roomAdaptor: RoomJpaPersistenceAdaptor

    @Autowired
    private lateinit var roomUserAdaptor: RoomUserPersistenceAdaptor

    @Autowired
    private lateinit var roomRepository: RoomJpaRepository

    @Autowired
    private lateinit var roomUserRepository: RoomUserJpaRepository

    @Test
    fun `a room member can soft delete the room and all room users`() {
        val deletedRoom = roomRepository.saveAndFlush(roomEntity(invitationCode = "123456", title = "Deleted"))
        val activeRoom = roomRepository.saveAndFlush(roomEntity(invitationCode = "654321", title = "Active"))
        val deletedRoomId = requireNotNull(deletedRoom.id)
        val activeRoomId = requireNotNull(activeRoom.id)
        val createdAt = LocalDateTime.of(2026, 8, 1, 12, 0)
        roomUserRepository.saveAllAndFlush(
            listOf(
                RoomUserEntity(roomId = deletedRoomId, userId = 11L, createdAt = createdAt),
                RoomUserEntity(roomId = deletedRoomId, userId = 12L, createdAt = createdAt),
                RoomUserEntity(roomId = activeRoomId, userId = 21L, createdAt = createdAt)
            )
        )

        service.deleteRoom(DeleteRoomCommand(userId = 12L, roomId = deletedRoomId))

        assertNotNull(roomRepository.findById(deletedRoomId).orElseThrow().deletedAt)
        assertNull(roomAdaptor.findByRoomId(deletedRoomId))
        assertNull(roomAdaptor.findByInvitationCode("123456"))
        assertEquals(listOf(activeRoomId), roomAdaptor.findAllById(listOf(deletedRoomId, activeRoomId)).map { it.id })
        assertEquals(emptyList<RoomUserEntity>(), roomUserRepository.findAllByRoomId(deletedRoomId))
        assertNotNull(roomAdaptor.findByRoomId(activeRoomId))
        assertNotNull(roomUserAdaptor.findByUserIdAndRoomId(userId = 21L, roomId = activeRoomId))
    }

    private fun roomEntity(invitationCode: String, title: String) = RoomEntity(
        title = title,
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
