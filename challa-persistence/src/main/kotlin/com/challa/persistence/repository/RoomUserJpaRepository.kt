package com.challa.persistence.repository

import com.challa.core.room.domain.RoomUserId
import com.challa.persistence.entity.RoomUserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface RoomUserJpaRepository : JpaRepository<RoomUserEntity, RoomUserId> {
    fun findAllByUserId(userId: Long): List<RoomUserEntity>

    fun findByUserIdAndRoomId(userId: Long, roomId: Long): RoomUserEntity?
}
