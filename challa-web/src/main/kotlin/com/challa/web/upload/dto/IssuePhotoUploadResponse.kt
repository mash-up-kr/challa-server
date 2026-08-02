package com.challa.web.upload.dto

import com.challa.core.upload.IssuePhotoUploadResult

data class IssuePhotoUploadResponse(
    val upload: PhotoUploadUrlResponse,
    val photo: IssuedPhotoResponse,
    val room: PhotoUploadRoomResponse
) {
    companion object {
        fun from(result: IssuePhotoUploadResult) = IssuePhotoUploadResponse(
            upload = PhotoUploadUrlResponse(
                uploadUrl = result.uploadUrl,
                imageUrl = result.imageUrl,
                expiresInSeconds = result.expiresInSeconds
            ),
            photo = IssuedPhotoResponse(id = result.photoId),
            room = PhotoUploadRoomResponse(remainedPhotoCount = result.remainedPhotoCount)
        )
    }
}
