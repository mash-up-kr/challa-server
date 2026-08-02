package com.challa.persistence.photo

import org.springframework.data.jpa.repository.JpaRepository

interface PhotoJpaRepository : JpaRepository<PhotoEntity, Long> {
    fun findByIdAndUserId(id: Long, userId: Long): PhotoEntity?
    fun findAllByRoomId(roomId: Long): List<PhotoEntity>
}
