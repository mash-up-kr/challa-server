package com.challa.core.photo

interface PhotoRepository {
    fun save(photo: Photo): Photo
    fun findById(photoId: Long): Photo?
    fun findByIdAndUserId(photoId: Long, userId: Long): Photo?
    fun findAllByIds(photoIds: List<Long>): List<Photo>
    fun findAllByRoomId(roomId: Long): List<Photo>
    fun findLatestFourByRoomIdsIn(roomIds: List<Long>): List<Photo>
}
