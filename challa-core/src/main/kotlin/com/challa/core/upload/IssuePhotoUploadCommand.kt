package com.challa.core.upload

data class IssuePhotoUploadCommand(
    val userId: Long,
    val roomId: Long,
    val cameraFilterId: String,
    val contentType: String
)
