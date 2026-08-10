package com.challa.core.photo

data class ListPhotosCommand(val userId: Long, val roomId: Long, val page: Int, val size: Int)
