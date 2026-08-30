package com.challa.persistence.photo

import com.challa.core.photo.Photo
import com.challa.core.photo.PhotoRepository
import com.challa.core.photo.PhotoSlice
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class PhotoAdapter(private val repository: PhotoJpaRepository) : PhotoRepository {

    @Transactional
    override fun save(photo: Photo): Photo = repository.saveAndFlush(PhotoEntity.from(photo)).toDomain()

    override fun findById(photoId: Long): Photo? = repository.findById(photoId).orElse(null)?.toDomain()

    override fun findAllByIds(photoIds: List<Long>): List<Photo> = repository.findAllById(photoIds).map {
        it.toDomain()
    }

    override fun findSliceByRoomId(roomId: Long, pageable: Pageable): PhotoSlice {
        val photoSlice = repository.findSliceByRoomId(roomId, pageable)

        return PhotoSlice(
            photos = photoSlice.content.map { it.toDomain() },
            hasNext = photoSlice.hasNext()
        )
    }

    override fun findLatestFourByRoomIdsIn(roomIds: List<Long>): List<Photo> =
        repository.findLatestFourByRoomIds(roomIds).map { it.toDomain() }
}
