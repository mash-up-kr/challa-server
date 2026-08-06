package com.challa.persistence.photo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PhotoJpaRepository : JpaRepository<PhotoEntity, Long> {
    fun findByIdAndUserId(id: Long, userId: Long): PhotoEntity?
    fun findAllByRoomId(roomId: Long): List<PhotoEntity>

    @Query(
        value = """
        SELECT *
        FROM (
            SELECT p.*,
                   ROW_NUMBER() OVER (
                       PARTITION BY p.room_id
                       ORDER BY p.created_at DESC
                   ) AS row_num
            FROM photos p
            WHERE p.room_id IN (:roomIds)
                AND p.image_url IS NOT NULL
        ) ranked
        WHERE ranked.row_num <= 4
        ORDER BY ranked.room_id, ranked.created_at DESC
    """,
        nativeQuery = true
    )
    fun findLatestFourByRoomIds(@Param("roomIds") roomIds: List<Long>): List<PhotoEntity>
}
