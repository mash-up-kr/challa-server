package com.challa.core.photo

data class CompletePhotoCommand(val userId: Long, val roomId: Long, val cameraFilterId: String, val imageUrl: String)
