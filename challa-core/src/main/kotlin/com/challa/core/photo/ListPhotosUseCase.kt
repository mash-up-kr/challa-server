package com.challa.core.photo

interface ListPhotosUseCase {
    fun listPhotos(command: ListPhotosCommand): ListPhotosResult
}
