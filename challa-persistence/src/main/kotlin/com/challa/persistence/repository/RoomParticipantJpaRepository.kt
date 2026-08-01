package com.challa.persistence.repository

import com.challa.core.room.domain.RoomParticipantId
import com.challa.persistence.entity.RoomParticipantEntity
import org.springframework.data.jpa.repository.JpaRepository

interface RoomParticipantJpaRepository : JpaRepository<RoomParticipantEntity, RoomParticipantId> {
    fun findAllByUserId(userId: Long): List<RoomParticipantEntity>
}
