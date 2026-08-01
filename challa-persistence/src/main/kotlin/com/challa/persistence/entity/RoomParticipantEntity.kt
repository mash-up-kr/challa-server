package com.challa.persistence.entity

import com.challa.core.room.domain.RoomParticipant
import com.challa.core.room.domain.UserRole
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "room_member")
data class RoomParticipantEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val roomParticipantId: Long? = null,

    @Column(nullable = false)
    val roomId: Long,

    @Column(nullable = false)
    val userId: Long,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: UserRole,

    @Column(nullable = false)
    val createdAt: LocalDateTime
) {
    fun toDomain() = RoomParticipant(
        roomParticipantId = roomParticipantId,
        roomId = roomId,
        userId = userId,
        role = role,
        createdAt = createdAt
    )

    companion object {
        fun from(roomParticipant: RoomParticipant) = RoomParticipantEntity(
            roomId = roomParticipant.roomId,
            userId = roomParticipant.userId,
            role = roomParticipant.role,
            createdAt = roomParticipant.createdAt
        )
    }
}
