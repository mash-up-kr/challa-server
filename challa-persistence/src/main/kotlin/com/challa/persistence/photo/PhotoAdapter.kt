package com.challa.persistence.photo

import com.challa.core.photo.Photo
import com.challa.core.photo.PhotoRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class PhotoAdapter(private val repository: PhotoJpaRepository) : PhotoRepository {

    @Transactional
    override fun save(photo: Photo): Photo = repository.saveAndFlush(PhotoEntity.from(photo)).toDomain()

    override fun findById(photoId: Long): Photo? = repository.findById(photoId).orElse(null)?.toDomain()

    override fun findByIdAndUserId(photoId: Long, userId: Long): Photo? =
        repository.findByIdAndUserId(photoId, userId)?.toDomain()

    override fun findAllByIds(photoIds: List<Long>): List<Photo> = repository.findAllById(photoIds).map {
        it.toDomain()
    }

    override fun findAllByRoomId(roomId: Long): List<Photo> = repository.findAllByRoomId(roomId).map { it.toDomain() }
}
