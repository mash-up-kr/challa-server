package com.challa.core.upload

import java.util.UUID

class IssueUploadUrlService(private val issuer: PresignedUploadUrlIssuer) : IssueUploadUrlUseCase {
    override fun issue(userId: Long, command: IssueUploadUrlCommand): UploadUrl {
        val contentType = command.contentType.trim().lowercase()
        if (contentType !in ALLOWED_CONTENT_TYPES) {
            throw UnsupportedImageTypeException("Unsupported image type: ${command.contentType}")
        }
        return issuer.issue("${command.purpose.keyPrefix}/$userId/${UUID.randomUUID()}", contentType)
    }

    private companion object {
        val ALLOWED_CONTENT_TYPES = setOf("image/jpeg", "image/png", "image/webp")
    }
}
