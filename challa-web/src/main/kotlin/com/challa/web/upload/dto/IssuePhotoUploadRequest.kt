package com.challa.web.upload.dto

import com.challa.core.upload.IssuePhotoUploadCommand

data class IssuePhotoUploadRequest(val roomId: Long, val cameraFilterId: String, val contentType: String) {
    fun toCommand(userId: Long) = IssuePhotoUploadCommand(
        userId = userId,
        roomId = roomId,
        cameraFilterId = cameraFilterId,
        contentType = contentType
    )
}
