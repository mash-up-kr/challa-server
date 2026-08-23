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
    var title: String,

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
    var coverImageUrl: String?,

    @Column(nullable = true)
    var coverStickerId: Long?,

    @Column(nullable = true)
    var coverStickerColorId: Long?,

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
        coverStickerId = coverStickerId,
        coverStickerColorId = coverStickerColorId,
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

    fun updateCover(coverImageUrl: String?, coverStickerId: Long?, coverStickerColorId: Long?) {
        this.coverImageUrl = coverImageUrl
        this.coverStickerId = coverStickerId
        this.coverStickerColorId = coverStickerColorId
    }

    fun updateTitle(title: String) {
        this.title = title
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
            coverStickerId = room.coverStickerId,
            coverStickerColorId = room.coverStickerColorId,
            photoPrintCompletedAt = room.photoPrintCompletedAt,
            createdAt = room.createdAt,
            expiresAt = room.expiresAt
        )
    }
}
