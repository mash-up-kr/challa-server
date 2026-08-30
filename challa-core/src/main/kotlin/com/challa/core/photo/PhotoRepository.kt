package com.challa.core.photo

import org.springframework.data.domain.Pageable

interface PhotoRepository {
    fun save(photo: Photo): Photo
    fun findById(photoId: Long): Photo?
    fun findAllByIds(photoIds: List<Long>): List<Photo>
    fun findSliceByRoomId(roomId: Long, pageable: Pageable): PhotoSlice
    fun findLatestFourByRoomIdsIn(roomIds: List<Long>): List<Photo>
}
