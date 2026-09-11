package com.challa.web.upload

import com.challa.core.upload.IssueUploadUrlUseCase
import com.challa.web.common.response.ApiResponse
import com.challa.web.security.AuthUserId
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "upload", description = "이미지 업로드용 S3 서명 URL 발급")
@RestController
@RequestMapping("/api/v1/uploads")
class UploadController(private val issueUploadUrlUseCase: IssueUploadUrlUseCase) {
    @Operation(
        summary = "이미지 업로드용 서명 URL 발급",
        description = UPLOAD_URL_GUIDE
    )
    @PostMapping
    fun issue(
        @AuthUserId userId: Long,
        @RequestBody request: UploadEnvelope<IssueUploadUrlRequest>
    ): ApiResponse<UploadEnvelope<UploadUrlResponse>> = ApiResponse.ok(
        UploadEnvelope(UploadUrlResponse.from(issueUploadUrlUseCase.issue(userId, request.upload.toCommand())))
    )
}

private const val UPLOAD_URL_GUIDE = """
이미지 파일을 서버로 보내지 않습니다. 서버는 **S3에 직접 올릴 수 있는 서명 URL만** 발급하고,
실제 파일 전송은 클라이언트가 S3로 직접 합니다.

## 전체 흐름 (3단계)

**1단계 — 이 API.**

기본적으로 `uploadUrl`(원본 업로드용 서명 URL)과 `imageUrl`(원본의 공개 URL)을 함께 받습니다.
`purpose`가 `PHOTO`이면 리사이징 이미지용 `thumbnailUploadUrl`과 `thumbnailImageUrl`도 함께 받습니다.

**2단계 — S3로 직접 PUT.** 이 요청은 우리 서버가 아니라 S3로 나가므로 **Swagger로는 테스트할 수 없습니다.**
```
PUT {1단계에서 받은 uploadUrl}
Content-Type: image/jpeg      ← 1단계에서 보낸 contentType과 정확히 같아야 함
Body: 원본 이미지 바이너리 그대로 ← multipart/form-data 아님
```
`purpose`가 `PHOTO`이면 리사이징 이미지도 같은 방식으로 별도 PUT 합니다.
```
PUT {1단계에서 받은 thumbnailUploadUrl}
Content-Type: image/jpeg      ← 원본과 동일한 contentType
Body: 리사이징 이미지 바이너리 그대로
```
- `Authorization` 헤더를 **넣지 마세요.** 넣으면 서명이 깨져 403이 납니다.
- 응답 200이면 성공입니다.

**3단계 — 업로드 결과 반영.** 프로필 이미지는 `imageUrl`을 프로필 수정 API에 전달합니다.
```
PUT /api/v1/users/me
{ "user": { "nickname": "...", "profileImageUrl": "{imageUrl}" } }
```
촬영 사진은 `roomId`, `cameraFilterName`과 함께 1단계에서 받은 원본·리사이징 이미지 공개 URL을
완료 API에 전달합니다. 리사이징 이미지를 업로드하지 못한 경우 `thumbnailImageUrl`은 생략하거나 null로 보냅니다.
```
POST /api/v1/photos
{
  "photo": {
    "roomId": 1,
    "cameraFilterName": "filter-original",
    "imageUrl": "{imageUrl}",
    "thumbnailImageUrl": "{thumbnailImageUrl}"
  }
}
```

## 주의사항

- `uploadUrl`과 `thumbnailUploadUrl`은 **5분 후 만료**됩니다. 화면 진입 시점에 미리 받아두지 말고,
  사용자가 사진을 고른 직후에 발급하세요.
- 각 업로드 URL은 **1회용·1파일용**입니다. `PHOTO` 한 건에는 한 번의 API 호출로 원본과 썸네일 URL 쌍을 발급합니다.
- `imageUrl`과 `thumbnailImageUrl`은 **영구 공개 URL**이라 그대로 저장하고 캐싱해도 됩니다.
- 2단계 실패는 서버가 알지 못합니다. 원본 업로드가 실패하면 3단계를 호출하지 말고 1단계부터 재시도하세요.
  썸네일만 실패한 경우에는 `thumbnailImageUrl`을 null로 보내 완료할 수 있습니다.

## 실패 응답

| 상황 | 상태 | message |
|---|---|---|
| 허용하지 않는 `contentType` | 400 | `Unsupported image type: ...` |
| `purpose` 값이 잘못됨 | 400 | `Malformed or invalid request body` |
| 액세스 토큰 없음·만료 | 401 | `Authentication required` / `Invalid or expired token` |

2단계에서 403이 나면 원인은 셋 중 하나입니다 — `Content-Type` 불일치, `Authorization` 헤더를 함께 보냄,
또는 5분이 지나 만료(`Request has expired`).
"""
