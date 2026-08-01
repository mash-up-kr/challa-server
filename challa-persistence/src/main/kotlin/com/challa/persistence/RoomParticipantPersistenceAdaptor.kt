package com.challa.persistence

import com.challa.core.room.domain.RoomParticipant
import com.challa.core.room.port.output.RoomParticipantRepository
import com.challa.persistence.entity.RoomParticipantEntity
import com.challa.persistence.repository.RoomParticipantJpaRepository
import org.springframework.stereotype.Component

@Component
class RoomParticipantPersistenceAdaptor(private val roomParticipantJpaRepository: RoomParticipantJpaRepository) :
    RoomParticipantRepository {
    override fun save(roomParticipant: RoomParticipant): RoomParticipant =
        roomParticipantJpaRepository.save(RoomParticipantEntity.from(roomParticipant)).toDomain()
}
