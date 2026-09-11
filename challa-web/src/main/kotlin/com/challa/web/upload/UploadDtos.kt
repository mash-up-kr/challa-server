package com.challa.web.upload

import com.challa.core.upload.IssueUploadUrlCommand
import com.challa.core.upload.UploadPurpose
import com.challa.core.upload.UploadUrl
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

data class UploadEnvelope<T : Any>(val upload: T)

data class IssueUploadUrlRequest(
    @param:JsonProperty(required = true)
    @field:Schema(
        requiredMode = Schema.RequiredMode.REQUIRED,
        description = "이미지의 용도. 저장 위치만 달라지고 사용 방법은 같다",
        example = "PROFILE_IMAGE"
    )
    val purpose: UploadPurpose,
    @param:JsonProperty(required = true)
    @field:Schema(
        requiredMode = Schema.RequiredMode.REQUIRED,
        description = "올릴 이미지의 MIME 타입. image/jpeg, image/png, image/webp 만 허용한다. " +
            "2단계 PUT 의 Content-Type 헤더와 정확히 같아야 하며, 다르면 S3 가 403 을 낸다",
        example = "image/jpeg"
    )
    val contentType: String
) {
    fun toCommand(): IssueUploadUrlCommand = IssueUploadUrlCommand(purpose = purpose, contentType = contentType)
}

data class UploadUrlResponse(
    @field:Schema(
        description = "S3 업로드용 서명 URL. 여기로 Authorization 헤더 없이 이미지 바이너리를 PUT 한다. " +
            "1회용이며 expiresInSeconds 후 만료된다"
    )
    val uploadUrl: String,
    @field:Schema(
        description = "업로드 성공 후 이미지를 읽을 공개 URL. 만료되지 않으므로 그대로 DB 에 저장하고 캐싱해도 된다. " +
            "프로필 이미지라면 이 값을 PUT /api/v1/users/me 의 profileImageUrl 로 보낸다"
    )
    val imageUrl: String,
    @field:Schema(description = "uploadUrl 의 남은 유효 시간(초)", example = "300")
    val expiresInSeconds: Long,
    @field:Schema(
        description = "PHOTO 용도의 리사이징 이미지 업로드용 서명 URL. 다른 용도에서는 null"
    )
    val thumbnailUploadUrl: String?,
    @field:Schema(
        description = "PHOTO 용도의 리사이징 이미지 공개 URL. 업로드 완료 요청에 전달하며 다른 용도에서는 null"
    )
    val thumbnailImageUrl: String?
) {
    companion object {
        fun from(result: UploadUrl) = UploadUrlResponse(
            uploadUrl = result.uploadUrl,
            imageUrl = result.imageUrl,
            expiresInSeconds = result.expiresInSeconds,
            thumbnailUploadUrl = result.thumbnailUploadUrl,
            thumbnailImageUrl = result.thumbnailImageUrl
        )
    }
}
