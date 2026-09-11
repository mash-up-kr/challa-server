package com.challa.core.upload

import java.util.*

class IssueUploadUrlService(private val issuer: PresignedUploadUrlIssuer) : IssueUploadUrlUseCase {
    override fun issue(userId: Long, command: IssueUploadUrlCommand): UploadUrl {
        val contentType = command.contentType.trim().lowercase()
        if (contentType !in ALLOWED_CONTENT_TYPES) {
            throw UnsupportedImageTypeException("Unsupported image type: ${command.contentType}")
        }

        val key = "${command.purpose.keyPrefix}/$userId/${UUID.randomUUID()}"
        val original = issuer.issue(key, contentType)
        if (command.purpose != UploadPurpose.PHOTO) {
            return original
        }

        // 원본과 같은 경로에서 파일명만 구분되도록 S3 key 뒤에 _thumbnail 을 붙인다.
        val thumbnail = issuer.issue("${key}_thumbnail", contentType)
        return original.copy(
            thumbnailUploadUrl = thumbnail.uploadUrl,
            thumbnailImageUrl = thumbnail.imageUrl
        )
    }

    private companion object {
        val ALLOWED_CONTENT_TYPES = setOf("image/jpeg", "image/png", "image/webp")
    }
}
