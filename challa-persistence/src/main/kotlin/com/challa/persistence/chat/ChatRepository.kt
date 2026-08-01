package com.challa.persistence.chat

import org.springframework.data.jpa.repository.JpaRepository

interface ChatRepository : JpaRepository<ChatEntity, Long>
