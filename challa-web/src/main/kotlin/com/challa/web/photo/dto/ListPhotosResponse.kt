package com.challa.web.photo.dto

import com.challa.core.photo.ListPhotosResult

data class ListPhotosResponse(val id: Long, val imageUrl: String?) {
    companion object {
        fun fromResult(result: ListPhotosResult.PhotoProjection) = ListPhotosResponse(
            id = result.id,
            imageUrl = result.imageUrl
        )
    }
}
