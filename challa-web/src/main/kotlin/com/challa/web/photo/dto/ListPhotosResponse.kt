package com.challa.web.photo.dto

import com.challa.core.photo.ListPhotosResult

data class ListPhotosResponse(val photoProjections: List<ListPhotosResult.PhotoProjection>) {
    companion object {
        fun fromResult(result: ListPhotosResult) = ListPhotosResponse(
            photoProjections = result.photoProjections
        )
    }
}
