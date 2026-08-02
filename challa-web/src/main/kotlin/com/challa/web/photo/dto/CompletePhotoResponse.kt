package com.challa.web.photo.dto

import com.challa.core.photo.CompletePhotoResult

data class CompletePhotoResponse(val remainedPhotoCount: Long) {
    companion object {
        fun from(result: CompletePhotoResult) = CompletePhotoResponse(
            remainedPhotoCount = result.remainedPhotoCount
        )
    }
}
