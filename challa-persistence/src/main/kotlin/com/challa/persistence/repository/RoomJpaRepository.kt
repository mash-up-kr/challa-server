package com.challa.persistence.repository

import com.challa.core.room.domain.RoomId
import com.challa.persistence.entity.RoomEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface RoomJpaRepository : JpaRepository<RoomEntity, RoomId> {
    fun findByInvitationCode(invitationCode: String): RoomEntity?

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
        """
        UPDATE RoomEntity room
        SET room.remainedPhotoCount = room.remainedPhotoCount - 1
        WHERE room.id = :roomId
          AND room.remainedPhotoCount > 0
        """
    )
    fun decrementRemainedPhotoCount(@Param("roomId") roomId: RoomId): Int
}
