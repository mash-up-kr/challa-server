package com.challa.persistence.user

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface RandomNicknameSourceJpaRepository : JpaRepository<RandomNicknameSourceEntity, Long> {
    fun countByType(type: String): Long

    fun findByType(type: String, pageable: Pageable): List<RandomNicknameSourceEntity>
}
