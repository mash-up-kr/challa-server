package com.challa.web.photo.dto

import com.challa.core.photo.CompletePhotoCommand

data class CompletePhotoRequest(val roomId: Long, val cameraFilterId: String, val imageUrl: String) {
    fun toCommand(userId: Long) = CompletePhotoCommand(
        userId = userId,
        roomId = roomId,
        cameraFilterId = cameraFilterId,
        imageUrl = imageUrl
    )
}
