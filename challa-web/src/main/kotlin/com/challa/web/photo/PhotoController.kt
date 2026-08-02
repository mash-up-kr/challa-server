package com.challa.web.photo

import com.challa.core.photo.CompletePhotoUseCase
import com.challa.web.common.response.ApiResponse
import com.challa.web.photo.dto.CompletePhotoRequest
import com.challa.web.security.AuthUserId
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/photos")
class PhotoController(private val completePhotoUseCase: CompletePhotoUseCase) {
    @PostMapping
    fun complete(@AuthUserId userId: Long, @RequestBody request: CompletePhotoRequest): ApiResponse<Unit?> {
        completePhotoUseCase.complete(request.toCommand(userId))
        return ApiResponse.empty()
    }
}
