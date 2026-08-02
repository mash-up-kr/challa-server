package com.challa.web.photo.dto

import com.challa.core.photo.CompletePhotoCommand

data class CompletePhotoRequest(val photoId: Long, val imageUrl: String) {
    fun toCommand(userId: Long) = CompletePhotoCommand(
        userId = userId,
        photoId = photoId,
        imageUrl = imageUrl
    )
}
