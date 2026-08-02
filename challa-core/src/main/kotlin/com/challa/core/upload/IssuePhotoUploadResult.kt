package com.challa.core.upload

data class IssuePhotoUploadResult(
    val photoId: Long,
    val uploadUrl: String,
    val imageUrl: String,
    val expiresInSeconds: Long,
    val remainedPhotoCount: Long
)
