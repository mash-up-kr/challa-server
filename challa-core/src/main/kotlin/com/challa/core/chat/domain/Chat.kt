package com.challa.core.chat.domain

import java.time.LocalDateTime

data class Chat(
    val id: Long? = null,
    val type: ChatType,
    val content: String,
    val photoId: Long? = null,
    val roomId: Long,
    val userId: Long,
    val createdAt: LocalDateTime? = null
)
