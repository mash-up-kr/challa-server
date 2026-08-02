package com.challa.core.photo

interface CompletePhotoUseCase {
    fun complete(command: CompletePhotoCommand): CompletePhotoResult
}
