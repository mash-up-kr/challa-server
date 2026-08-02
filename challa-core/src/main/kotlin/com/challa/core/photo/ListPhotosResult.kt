package com.challa.core.photo

data class ListPhotosResult(val photoProjections: List<PhotoProjection>) {
    data class PhotoProjection(val id: Long, val imageUrl: String?)
}
