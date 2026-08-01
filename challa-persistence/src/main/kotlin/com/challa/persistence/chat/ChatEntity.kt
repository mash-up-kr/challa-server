package com.challa.persistence.chat

import com.challa.core.chat.domain.Chat
import com.challa.core.chat.domain.ChatType
import com.challa.persistence.common.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "chats")
class ChatEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    var type: ChatType,

    @Column(name = "content", nullable = false)
    var content: String,

    @Column(name = "photo_id")
    var photoId: Long? = null,

    @Column(name = "room_id", nullable = false)
    var roomId: Long,

    @Column(name = "user_id", nullable = false)
    var userId: Long

) : BaseEntity() {

    fun toDomain(): Chat = Chat(
        id = id,
        type = type,
        content = content,
        photoId = photoId,
        roomId = roomId,
        userId = userId,
        createdAt = createdAt
    )

    companion object {
        fun from(chat: Chat): ChatEntity = ChatEntity(
            type = chat.type,
            content = chat.content,
            photoId = chat.photoId,
            roomId = chat.roomId,
            userId = chat.userId,
        )
    }
}
