package com.challa.core.upload

interface PresignedUploadUrlIssuer {
    /** 클라이언트가 [contentType] 헤더를 붙여 PUT 할 서명 URL과, 업로드 후 읽을 공개 URL을 함께 준다. */
    fun issue(key: String, contentType: String): UploadUrl
}
