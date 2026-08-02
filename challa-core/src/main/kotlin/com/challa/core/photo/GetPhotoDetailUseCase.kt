package com.challa.core.photo

interface GetPhotoDetailUseCase {
    fun getPhotoDetail(command: GetPhotoDetailCommand): GetPhotoDetailResult
}
