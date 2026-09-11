package com.challa.core.photo

import java.time.LocalDateTime

data class ListPhotosResult(val photoProjections: List<PhotoProjection>, val hasNext: Boolean) {
    data class PhotoProjection(
        val id: Long,
        val imageUrl: String?,
        val thumbnailImageUrl: String? = null,
        val userNickname: String,
        val userProfileImageUrl: String?,
        val createdAt: LocalDateTime
    )
}
