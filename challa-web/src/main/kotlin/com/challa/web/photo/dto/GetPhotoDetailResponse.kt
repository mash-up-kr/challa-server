package com.challa.web.photo.dto

import com.challa.core.photo.GetPhotoDetailResult
import com.challa.core.photo.PhotoDetail

data class GetPhotoDetailResponse(val photoDetail: PhotoDetail) {
    companion object {
        fun fromResult(result: GetPhotoDetailResult) = GetPhotoDetailResponse(
            photoDetail = result.photoDetail
        )
    }
}
