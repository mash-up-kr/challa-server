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
    val totalPhotoCount: Long,

    @Column(nullable = false)
    val remainedPhotoCount: Long,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var roomStatus: RoomStatus,

    @Column(nullable = false, length = 6)
    val invitationCode: String,

    @Column(nullable = true)
    var photoPrintCompletionAt: LocalDateTime?,

    @Column(nullable = false)
    val createdAt: LocalDateTime
) {
    fun toDomain() = Room(
        id = id,
        title = title,
        totalPhotoCount = totalPhotoCount,
        remainedPhotoCount = remainedPhotoCount,
        invitationCode = invitationCode,
        roomStatus = roomStatus,
        photoPrintCompletionAt = photoPrintCompletionAt,
        createdAt = createdAt
    )

    fun updateStatus(newStatus: RoomStatus) {
        roomStatus = newStatus
    }

    fun markPhotoPrintPending(newPhotoPrintCompletionAt: LocalDateTime) {
        roomStatus = RoomStatus.PHOTO_PRINT_PENDING
        photoPrintCompletionAt = newPhotoPrintCompletionAt
    }

    companion object {
        fun from(room: Room) = RoomEntity(
            id = room.id,
            title = room.title,
            totalPhotoCount = room.totalPhotoCount,
            remainedPhotoCount = room.remainedPhotoCount,
            invitationCode = room.invitationCode,
            roomStatus = room.roomStatus,
            photoPrintCompletionAt = room.photoPrintCompletionAt,
            createdAt = room.createdAt
        )
    }
}
