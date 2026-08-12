package com.challa.persistence.repository

import com.challa.core.room.domain.RoomUserId
import com.challa.core.room.port.output.RoomMemberCount
import com.challa.persistence.entity.RoomUserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface RoomUserJpaRepository : JpaRepository<RoomUserEntity, RoomUserId> {
    fun findAllByUserId(userId: Long): List<RoomUserEntity>

    @Modifying
    @Query(
        value = """
            INSERT INTO room_user (room_id, user_id, created_at)
            VALUES (:roomId, :userId, :createdAt)
            ON CONFLICT (room_id, user_id) DO NOTHING
        """,
        nativeQuery = true
    )
    fun insertIfAbsent(
        @Param("roomId") roomId: Long,
        @Param("userId") userId: Long,
        @Param("createdAt") createdAt: LocalDateTime
    ): Int

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
