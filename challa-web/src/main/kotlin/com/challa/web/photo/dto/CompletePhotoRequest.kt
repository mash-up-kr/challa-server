package com.challa.web.photo.dto

import com.challa.core.photo.CompletePhotoCommand

data class CompletePhotoRequest(
    val roomId: Long,
    val cameraFilterName: String,
    val imageUrl: String,
    val thumbnailImageUrl: String? = null
) {
    fun toCommand(userId: Long) = CompletePhotoCommand(
        userId = userId,
        roomId = roomId,
        cameraFilterName = cameraFilterName,
        imageUrl = imageUrl,
        thumbnailImageUrl = thumbnailImageUrl
    )
}
