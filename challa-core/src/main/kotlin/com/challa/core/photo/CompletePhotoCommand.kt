package com.challa.core.photo

data class CompletePhotoCommand(val userId: Long, val roomId: Long, val cameraFilterName: String, val imageUrl: String)
