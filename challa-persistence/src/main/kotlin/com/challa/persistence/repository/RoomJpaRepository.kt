package com.challa.persistence.repository

import com.challa.core.room.domain.RoomId
import com.challa.core.room.domain.RoomStatus
import com.challa.persistence.entity.RoomEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional

interface RoomJpaRepository : JpaRepository<RoomEntity, RoomId> {
    fun findByInviteCode(inviteCode: String): RoomEntity?

    fun findAllByRoomIdIn(roomIds: List<RoomId>): List<RoomEntity>

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
        """
        update RoomEntity room
           set room.roomStatus = :roomStatus
         where room.roomId in :roomIds
        """
    )
    fun updateRoomsStatus(roomIds: List<RoomId>, roomStatus: RoomStatus): Int
}
