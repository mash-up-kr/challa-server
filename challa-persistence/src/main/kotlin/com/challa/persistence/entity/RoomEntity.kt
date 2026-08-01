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
            name = "invitation_code",
            columnNames = ["invitation_code"]
        )
    ]
)
class RoomEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 20)
    val title: String,

    @Column(nullable = false)
    val filmLimit: Long,

    @Column(nullable = false)
    val remainingFilmCount: Long,

    @Column(nullable = false, length = 6)
    val invitationCode: String,

    roomStatus: RoomStatus,

    @Column(nullable = true)
    val printCompletionAt: LocalDateTime?,

    @Column(nullable = false)
    val createdAt: LocalDateTime
) {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var roomStatus: RoomStatus = roomStatus
        private set

    fun toDomain() = Room(
        id = id,
        title = title,
        filmLimit = filmLimit,
        remainingFilmCount = remainingFilmCount,
        invitationCode = invitationCode,
        roomStatus = roomStatus,
        printCompletionAt = printCompletionAt,
        createdAt = createdAt
    )

    fun updateStatus(newStatus: RoomStatus) {
        roomStatus = newStatus
    }

    companion object {
        fun from(room: Room) = RoomEntity(
            id = room.id,
            title = room.title,
            filmLimit = room.filmLimit,
            remainingFilmCount = room.remainingFilmCount,
            invitationCode = room.invitationCode,
            roomStatus = room.roomStatus,
            printCompletionAt = room.printCompletionAt,
            createdAt = room.createdAt
        )
    }
}
