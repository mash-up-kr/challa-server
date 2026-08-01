package com.challa.core.photo

interface PhotoRepository {
    fun findById(photoId: Long): Photo?
    fun findAllByIds(photoIds: List<Long>): List<Photo>
}
