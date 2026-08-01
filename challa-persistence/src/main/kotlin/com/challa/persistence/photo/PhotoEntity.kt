package com.challa.persistence.photo

import com.challa.core.photo.Photo
import com.challa.persistence.common.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "photos")
class PhotoEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "image_url", nullable = false, unique = true)
    var imageUrl: String,

    @Column(name = "room_id", nullable = false)
    var roomId: Long,

    @Column(name = "user_id", nullable = false)
    var userId: Long

) : BaseEntity() {

    fun toDomain(): Photo = Photo(
        id = this.id,
        imageUrl = this.imageUrl,
        roomId = this.roomId,
        userId = this.userId,
    )
}
