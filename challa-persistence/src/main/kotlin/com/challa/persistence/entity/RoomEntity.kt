package com.challa.persistence.entity

import com.challa.core.room.domain.Room
import com.challa.core.room.domain.RoomStatus
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "room",
    uniqueConstraints = [
        UniqueConstraint(
            name = "invite_code",
            columnNames = ["invite_code"]
        )
    ]
)
data class RoomEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val roomId: Long? = null,

    @Column(nullable = false, length = 20)
    val title: String,

    @Column(nullable = false)
    val filmLimit: Long,

    @Column(nullable = false)
    val remainingFilmCount: Long,

    @Column(nullable = false, length = 6)
    val inviteCode: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val roomStatus: RoomStatus,

    @Column(nullable = true)
    val printCompletionAt: LocalDateTime?,

    @Column(nullable = false)
    val isActive: Boolean,

    @Column(nullable = false)
    val createdAt: LocalDateTime
) {
    fun toDomain() = Room(
        roomId = roomId,
        title = title,
        filmLimit = filmLimit,
        remainingFilmCount = remainingFilmCount,
        inviteCode = inviteCode,
        roomStatus = roomStatus,
        printCompletionAt = printCompletionAt,
        isActive = isActive,
        createdAt = createdAt
    )

    companion object {
        fun from(room: Room) = RoomEntity(
            title = room.title,
            filmLimit = room.filmLimit,
            remainingFilmCount = room.remainingFilmCount,
            inviteCode = room.inviteCode,
            roomStatus = room.roomStatus,
            printCompletionAt = room.printCompletionAt,
            isActive = room.isActive,
            createdAt = room.createdAt
        )
    }
}
