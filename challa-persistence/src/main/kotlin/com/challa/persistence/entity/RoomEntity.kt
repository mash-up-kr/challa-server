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

    @Column(nullable = false, length = 6)
    val invitationCode: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var roomStatus: RoomStatus,

    @Column(nullable = true)
    val coverImageUrl: String?,

    @Column(nullable = false)
    val coverStickerUrl: String,

    @Column(nullable = true)
    var photoPrintCompletedAt: LocalDateTime?,

    @Column(nullable = false)
    val createdAt: LocalDateTime,

    @Column(nullable = false)
    val expiresAt: LocalDateTime
) {
    fun toDomain() = Room(
        id = id,
        title = title,
        totalPhotoCount = totalPhotoCount,
        remainedPhotoCount = remainedPhotoCount,
        invitationCode = invitationCode,
        roomStatus = roomStatus,
        coverImageUrl = coverImageUrl,
        coverStickerUrl = coverStickerUrl,
        photoPrintCompletedAt = photoPrintCompletedAt,
        createdAt = createdAt,
        expiresAt = expiresAt
    )

    fun updateStatus(newStatus: RoomStatus) {
        roomStatus = newStatus
    }

    fun markPhotoPrintPending(newPhotoPrintCompletedAt: LocalDateTime) {
        roomStatus = RoomStatus.PHOTO_PRINT_PENDING
        photoPrintCompletedAt = newPhotoPrintCompletedAt
    }

    companion object {
        fun from(room: Room) = RoomEntity(
            id = room.id,
            title = room.title,
            totalPhotoCount = room.totalPhotoCount,
            remainedPhotoCount = room.remainedPhotoCount,
            invitationCode = room.invitationCode,
            roomStatus = room.roomStatus,
            coverImageUrl = room.coverImageUrl,
            coverStickerUrl = room.coverStickerUrl,
            photoPrintCompletedAt = room.photoPrintCompletedAt,
            createdAt = room.createdAt,
            expiresAt = room.expiresAt
        )
    }
}
