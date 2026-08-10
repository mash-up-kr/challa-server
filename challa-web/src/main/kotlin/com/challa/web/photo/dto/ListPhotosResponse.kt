package com.challa.web.photo.dto

import com.challa.core.photo.ListPhotosResult
import java.time.LocalDateTime

data class ListPhotosResponse(
    val id: Long,
    val imageUrl: String?,
    val userNickname: String?,
    val userProfileImageUrl: String?,
    val createdAt: LocalDateTime
) {
    companion object {
        fun fromResult(result: ListPhotosResult.PhotoProjection) = ListPhotosResponse(
            id = result.id,
            imageUrl = result.imageUrl,
            userNickname = result.userNickname,
            userProfileImageUrl = result.userProfileImageUrl,
            createdAt = result.createdAt
        )
    }
}
