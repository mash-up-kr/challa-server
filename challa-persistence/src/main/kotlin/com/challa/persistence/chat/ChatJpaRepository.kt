package com.challa.persistence.chat

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface ChatJpaRepository : JpaRepository<ChatEntity, Long> {
    fun findAllByRoomId(roomId: Long, pageable: Pageable): List<ChatEntity>
    fun findAllByPhotoId(photoId: Long): List<ChatEntity>
}
