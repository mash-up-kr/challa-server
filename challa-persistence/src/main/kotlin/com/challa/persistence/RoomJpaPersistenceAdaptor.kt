package com.challa.persistence

import com.challa.core.room.domain.Room
import com.challa.core.room.port.output.InviteCodeConflictException
import com.challa.core.room.port.output.RoomRepository
import com.challa.persistence.entity.RoomEntity
import com.challa.persistence.repository.RoomJpaRepository
import com.challa.persistence.util.findCause
import org.hibernate.exception.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Component

private const val INVITE_CODE_CONSTRAINT = "invite_code"

@Component
class RoomJpaPersistenceAdaptor(private val roomJpaRepository: RoomJpaRepository) : RoomRepository {
    override fun save(room: Room): Room {
        try {
            return roomJpaRepository.saveAndFlush(RoomEntity.from(room)).toDomain()
        } catch (e: DataIntegrityViolationException) {
            val violation = e.findCause<ConstraintViolationException>()
            if (violation?.kind == ConstraintViolationException.ConstraintKind.UNIQUE &&
                violation.constraintName.equals(INVITE_CODE_CONSTRAINT, ignoreCase = true)
            ) {
                throw InviteCodeConflictException()
            }

            throw e
        }
    }
}
