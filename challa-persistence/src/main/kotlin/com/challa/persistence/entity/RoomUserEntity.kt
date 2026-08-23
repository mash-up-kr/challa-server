package com.challa.persistence.entity

import com.challa.core.room.domain.RoomUser
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "room_user",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_room_user_room_id_user_id",
            columnNames = ["room_id", "user_id"]
        )
    ]
)
class RoomUserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val roomId: Long,

    @Column(nullable = false)
    val userId: Long,

    @Column(nullable = false)
    val createdAt: LocalDateTime,

    @Column(nullable = true)
    var photoPrintCompletionCheckedAt: LocalDateTime? = null
) {
    fun toDomain() = RoomUser(
        id = id,
        roomId = roomId,
        userId = userId,
        createdAt = createdAt,
        photoPrintCompletionCheckedAt = photoPrintCompletionCheckedAt
    )

    companion object {
        fun from(roomUser: RoomUser) = RoomUserEntity(
            id = roomUser.id,
            roomId = roomUser.roomId,
            userId = roomUser.userId,
            createdAt = roomUser.createdAt,
            photoPrintCompletionCheckedAt = roomUser.photoPrintCompletionCheckedAt
        )
    }
}
