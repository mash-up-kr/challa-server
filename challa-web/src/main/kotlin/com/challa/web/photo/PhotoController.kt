package com.challa.web.photo

import com.challa.core.photo.*
import com.challa.web.common.response.ApiResponse
import com.challa.web.photo.dto.CompletePhotoRequest
import com.challa.web.photo.dto.CompletePhotoResponse
import com.challa.web.photo.dto.GetPhotoDetailResponse
import com.challa.web.photo.dto.ListPhotosResponse
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
        @RequestBody request: CompletePhotoRequest
    ): ApiResponse<CompletePhotoResponse> =
        ApiResponse.ok(CompletePhotoResponse.from(completePhotoUseCase.complete(request.toCommand(userId))))

    @GetMapping
    fun listPhotos(@AuthUserId userId: Long, @RequestParam roomId: Long): ApiResponse<ListPhotosResponse> {
        val result = listPhotosUseCase.listPhotos(ListPhotosCommand(userId = userId, roomId = roomId))

        return ApiResponse.ok(ListPhotosResponse.fromResult(result))
    }

    @GetMapping("/{photoId}")
    fun getPhotoDetail(@AuthUserId userId: Long, @PathVariable photoId: Long): ApiResponse<GetPhotoDetailResponse> {
        val result = getPhotoDetailUseCase.getPhotoDetail(GetPhotoDetailCommand(userId = userId, photoId = photoId))

        return ApiResponse.ok(GetPhotoDetailResponse.fromResult(result))
    }
}
