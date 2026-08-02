package com.challa.core.photo

data class CompletePhotoCommand(val userId: Long, val photoId: Long, val imageUrl: String)
