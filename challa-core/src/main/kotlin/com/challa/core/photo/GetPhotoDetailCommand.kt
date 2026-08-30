package com.challa.core.photo

data class GetPhotoDetailCommand(val userId: Long, val roomId: Long, val photoId: Long)
