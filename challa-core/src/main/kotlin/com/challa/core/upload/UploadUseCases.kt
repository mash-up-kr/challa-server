package com.challa.core.upload

interface IssueUploadUrlUseCase {
    fun issue(userId: Long, command: IssueUploadUrlCommand): UploadUrl
}

enum class UploadPurpose(val keyPrefix: String) {
    PROFILE_IMAGE("profile"),
    PHOTO("photo")
}

data class IssueUploadUrlCommand(val purpose: UploadPurpose, val contentType: String)

data class UploadUrl(val uploadUrl: String, val imageUrl: String, val expiresInSeconds: Long)
