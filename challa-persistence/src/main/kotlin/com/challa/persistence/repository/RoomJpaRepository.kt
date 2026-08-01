package com.challa.persistence.repository

import com.challa.core.room.domain.RoomId
import com.challa.persistence.entity.RoomEntity
import org.springframework.data.jpa.repository.JpaRepository

interface RoomJpaRepository : JpaRepository<RoomEntity, RoomId>
