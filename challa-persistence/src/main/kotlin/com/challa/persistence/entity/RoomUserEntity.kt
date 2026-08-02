package com.challa.persistence.entity

import com.challa.core.room.domain.RoomUser
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "room_user")
class RoomUserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val roomId: Long,

    @Column(nullable = false)
    val userId: Long,

    @Column(nullable = false)
    val createdAt: LocalDateTime
) {
    fun toDomain() = RoomUser(
        id = id,
        roomId = roomId,
        userId = userId,
        createdAt = createdAt
    )

    companion object {
        fun from(roomUser: RoomUser) = RoomUserEntity(
            id = roomUser.id,
            roomId = roomUser.roomId,
            userId = roomUser.userId,
            createdAt = roomUser.createdAt
        )
    }
}
