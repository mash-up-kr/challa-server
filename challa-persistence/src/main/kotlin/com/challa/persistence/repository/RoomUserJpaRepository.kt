package com.challa.persistence.repository

import com.challa.core.room.domain.RoomUserId
import com.challa.core.room.port.output.RoomMemberCount
import com.challa.persistence.entity.RoomUserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface RoomUserJpaRepository : JpaRepository<RoomUserEntity, RoomUserId> {
    fun findAllByUserId(userId: Long): List<RoomUserEntity>

    @Query(
        """
        SELECT roomUser.roomId, COUNT(roomUser.id)
        FROM RoomUserEntity roomUser
        WHERE roomUser.roomId IN :roomIds
        GROUP BY roomUser.roomId
        """
    )
    fun countMembersByRoomIds(@Param("roomIds") roomIds: Collection<Long>): List<RoomMemberCount>

    fun findByUserIdAndRoomId(userId: Long, roomId: Long): RoomUserEntity?

    fun findAllByRoomId(roomId: Long): List<RoomUserEntity>
}
