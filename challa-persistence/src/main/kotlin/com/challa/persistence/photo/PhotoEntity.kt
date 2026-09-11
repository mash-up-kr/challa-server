package com.challa.persistence.photo

import com.challa.core.photo.Photo
import com.challa.persistence.common.BaseEntity
import jakarta.persistence.*

@Entity
@Table(
    name = "photos",
    indexes = [
        Index(name = "idx_photos_room_created_at", columnList = "room_id, created_at")
    ]
)
class PhotoEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "image_url", unique = true)
    var imageUrl: String?,

    @Column(name = "thumbnail_image_url", unique = true)
    var thumbnailImageUrl: String?,

    @Column(name = "filter_id", nullable = false)
    var filterId: String,

    @Column(name = "room_id", nullable = false)
    var roomId: Long,

    @Column(name = "user_id", nullable = false)
    var userId: Long

) : BaseEntity() {
    fun toDomain(): Photo = Photo(
        id = this.id,
        imageUrl = this.imageUrl,
        thumbnailImageUrl = this.thumbnailImageUrl,
        filterId = this.filterId,
        roomId = this.roomId,
        userId = this.userId,
        createdAt = this.createdAt!!
    )

    companion object {
        fun from(photo: Photo): PhotoEntity = PhotoEntity(
            id = photo.id,
            imageUrl = photo.imageUrl,
            thumbnailImageUrl = photo.thumbnailImageUrl,
            filterId = photo.filterId,
            roomId = photo.roomId,
            userId = photo.userId
        )
    }
}
