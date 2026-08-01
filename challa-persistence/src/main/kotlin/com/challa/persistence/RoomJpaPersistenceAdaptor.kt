package com.challa.persistence

import com.challa.core.room.domain.Room
import com.challa.core.room.domain.RoomId
import com.challa.core.room.domain.RoomStatus
import com.challa.core.room.port.output.InvitationCodeConflictException
import com.challa.core.room.port.output.RoomRepository
import com.challa.persistence.entity.RoomEntity
import com.challa.persistence.repository.RoomJpaRepository
import com.challa.persistence.util.findCause
import org.hibernate.exception.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

private const val INVITATION_CODE_CONSTRAINT = "invitation_code"

@Component
class RoomJpaPersistenceAdaptor(private val roomJpaRepository: RoomJpaRepository) : RoomRepository {
    override fun save(room: Room): Room {
        try {
            return roomJpaRepository.saveAndFlush(RoomEntity.from(room)).toDomain()
        } catch (e: DataIntegrityViolationException) {
            val violation = e.findCause<ConstraintViolationException>()
            if (violation?.kind == ConstraintViolationException.ConstraintKind.UNIQUE &&
                violation.constraintName.equals(INVITATION_CODE_CONSTRAINT, ignoreCase = true)
            ) {
                throw InvitationCodeConflictException()
            }

            throw e
        }
    }

    override fun findByInvitationCode(invitationCode: String): Room? =
        roomJpaRepository.findByInvitationCode(invitationCode)?.toDomain()

    override fun findAllByRoomIdIn(roomIds: List<RoomId>): List<Room> =
        roomJpaRepository.findAllById(roomIds).map { it.toDomain() }

    @Transactional
    override fun updateRoomsStatus(roomIds: List<RoomId>, roomStatus: RoomStatus) {
        if (roomIds.isEmpty()) return

        roomJpaRepository.findAllById(roomIds).forEach { room ->
            room.updateStatus(roomStatus)
        }
    }

    override fun findByRoomId(roomId: RoomId): Room? = roomJpaRepository.findById(roomId).orElse(null)?.toDomain()
}
