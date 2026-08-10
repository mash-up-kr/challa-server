package com.challa.web.photo

import com.challa.core.photo.*
import com.challa.web.common.response.ApiResponse
import com.challa.web.photo.dto.*
import com.challa.web.security.AuthUserId
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/photos")
class PhotoController(
    private val completePhotoUseCase: CompletePhotoUseCase,
    private val listPhotosUseCase: ListPhotosUseCase,
    private val getPhotoDetailUseCase: GetPhotoDetailUseCase
) {
    @PostMapping
    fun complete(
        @AuthUserId userId: Long,
        @RequestBody request: PhotoEnvelope<CompletePhotoRequest>
    ): ApiResponse<PhotoEnvelope<CompletePhotoResponse>> = ApiResponse.ok(
        PhotoEnvelope(CompletePhotoResponse.from(completePhotoUseCase.complete(request.photo.toCommand(userId))))
    )

    @GetMapping
    fun listPhotos(
        @AuthUserId userId: Long,
        @RequestParam roomId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "24") size: Int
    ): ApiResponse<ListPhotosSliceResponse> {
        val result = listPhotosUseCase.listPhotos(
            ListPhotosCommand(
                userId = userId,
                roomId = roomId,
                page = page,
                size = size
            )
        )

        return ApiResponse.ok(
            ListPhotosSliceResponse(
                photos = result.photoProjections.map { ListPhotosResponse.fromResult(it) },
                hasNext = result.hasNext
            )
        )
    }

    @GetMapping("/{photoId}")
    fun getPhotoDetail(
        @AuthUserId userId: Long,
        @PathVariable photoId: Long
    ): ApiResponse<PhotoEnvelope<GetPhotoDetailResponse>> {
        val result = getPhotoDetailUseCase.getPhotoDetail(GetPhotoDetailCommand(userId = userId, photoId = photoId))

        return ApiResponse.ok(PhotoEnvelope(GetPhotoDetailResponse.fromResult(result)))
    }
}
