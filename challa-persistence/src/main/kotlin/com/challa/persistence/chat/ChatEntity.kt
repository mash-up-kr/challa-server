package com.challa.persistence.chat

import com.challa.persistence.common.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "chats")
class ChatEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "type", nullable = false)
    var type: String, // PLAIN_TEXT,EMOJI,COMMENT

    @Column(name = "content", nullable = false)
    var content: String,

    @Column(name = "photo_id")
    var photoId: Long? = null,

    @Column(name = "room_id", nullable = false)
    var roomId: Long,

    @Column(name = "user_id", nullable = false)
    var userId: Long,

) : BaseEntity()
